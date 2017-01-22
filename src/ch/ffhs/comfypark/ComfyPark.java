package ch.ffhs.comfypark;

import ch.ffhs.comfypark.config.ComfyParkConfig;
import ch.ffhs.comfypark.config.models.MySQL;
import ch.ffhs.comfypark.lambdaHandler.login.models.LoginRequest;
import ch.ffhs.comfypark.lambdaHandler.login.models.LoginResponse;
import ch.ffhs.comfypark.lambdaHandler.models.SignedRequest;
import ch.ffhs.comfypark.lambdaHandler.models.UnauthorizedResponse;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;
import ch.ffhs.comfypark.lambdaHandler.parking.models.ParkingRequest;
import ch.ffhs.comfypark.lambdaHandler.parking.models.ParkingResponse;
import ch.ffhs.comfypark.lambdaHandler.status.models.StatusResponse;
import ch.ffhs.comfypark.mysql.models.Parking;
import ch.ffhs.comfypark.mysql.models.User;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class ComfyPark {
	private ComfyParkConfig config;
	private GateProvider gateProvider;
	private PaymentProvider paymentProvider;

	private User user;

	private Sql2o sql2o;

	public ComfyPark() {
		setConfig(new ComfyParkConfig());
		setGateProvider(new GateProvider());
		setPaymentProvider(new PaymentProvider());

		setUser(new User());

		MySQL mysqlConfig = getConfig().getMysql();
		setSql2o(new Sql2o("jdbc:mysql://" + mysqlConfig.getServername() + ":3306/" + mysqlConfig.getDbname(),
				mysqlConfig.getUsername(), mysqlConfig.getPassword()));
	}

	public BasicResponse processLoginRequest(LoginRequest request) {
		// check required data
		if (request != null && request.getUsername() != null && request.getUsername().length() != 0 && request.getPassword() != null && request.getPassword().length() != 0) {	
			StringBuilder query = new StringBuilder()
				.append("SELECT * FROM users WHERE username = :username AND SHA2(CONCAT(password, '")
				.append(getConfig().getBackend().getHashSalt())
				.append("'), 256) = :password");
	
			try (Connection con = sql2o.open()) {
				List<User> users = con.createQuery(query.toString())
					.addParameter("username", request.getUsername())
					.addParameter("password", request.getPassword())
					.executeAndFetch(User.class);
	
				if (!users.isEmpty()) {
					String currentTimestamp = getTime();
					User userTemp = users.get(0);
	
					// update user meta data
					String updateQuery = "UPDATE users SET lastLogin = :lastLogin, countLogins = countLogins + 1 WHERE uid = :uid";
					con.createQuery(updateQuery)
						.addParameter("lastLogin", currentTimestamp)
						.addParameter("uid", userTemp.getUid())
						.executeUpdate();
	
					// get latest user data
					String selectQuery = "SELECT * FROM users WHERE uid = :uid";
					List<User> latestUsers = con.createQuery(selectQuery)
							.addParameter("uid", users.get(0).getUid())
							.executeAndFetch(User.class);
	
					if (!latestUsers.isEmpty()) {
						// return user-token for further requests
						return new LoginResponse(true, "Login successful", latestUsers.get(0), buildUserToken(latestUsers.get(0)));
					}
				}
			} catch (Exception e) {
				// TODO
			}

			return new BasicResponse(false, "Login failed, please check your username and password");
		}

		return new BasicResponse(false, "Login failed, required data missing");
	}

	public BasicResponse processUserTokenAuthentication(SignedRequest request) {
		if (request != null && request.getUserToken() != null && request.getUserToken().length() != 0) {
			Calendar calendar = Calendar.getInstance();

			StringBuilder query = new StringBuilder()
				.append("SELECT * FROM users WHERE SHA2(CONCAT(") 
					.append("uid, ")
					.append("countLogins, ")
					.append("'").append(calendar.get(Calendar.DAY_OF_YEAR)).append("', ")
					.append("'").append(getConfig().getBackend().getHashSalt()).append("'")
				.append("), 256)")
				.append("=")
				.append("'").append(request.getUserToken()).append("'");
				
			try (Connection con = sql2o.open()) {
				List<User> users = con.createQuery(query.toString()).executeAndFetch(User.class);

				if (!users.isEmpty()) {
					setUser(users.get(0));
				} else {
					setUser(new User());
				}
			} catch (Exception e) {
				setUser(new User());
			}

			if (isAuthenticated()) {
				return new BasicResponse(true, "Authenticated successful");
			}
			
			return new UnauthorizedResponse(false, "Invalid request, user authentication expired");
		}

		return new UnauthorizedResponse(false, "Illegal request, user authentication missing");
	}

	public BasicResponse processRequestTokenVerification(SignedRequest request) {
		if (request != null && request.getRequestToken() != null && request.getRequestToken().length() != 0) {
			if (buildRequestToken(getUser(), request).equals(request.getRequestToken())) {
				return new BasicResponse(true, "Request verified successful");
			}
			else{
				return new UnauthorizedResponse(false, "Invalid request, request authentication expired");
			}
		}

		return new UnauthorizedResponse(false, "Illegal request, request authentication missing");
	}

	public BasicResponse processParkingRequest(ParkingRequest request) {
		// check required data
		if (request.getGateId() != 0) {
			Parking lastParking = getLastParking();
			
			// determine check-in or check-out
			if (lastParking == null) {
				return checkIn(request);
			} else {
				return checkOut(request, lastParking);
			}
		}
				
		return new BasicResponse(false, "Error required data missing");
	}

	public BasicResponse processStatusRequest(SignedRequest request) {
		Parking lastParking = getLastParking();
		
		// determine checked-in or checked-out
		if (lastParking == null) {
			return new BasicResponse(true, "Not checked-in yet");
		} else {
			return new StatusResponse(true, "Checked-in at gate " + lastParking.getUid(),
					lastParking.getGateIn(), lastParking.getTimeIn());
		}
	}

	private String buildUserToken(User user) {
		Calendar calendar = Calendar.getInstance();

		StringBuilder tokenValue = new StringBuilder()
			.append(user.getUid())
			.append(user.getCountLogins())
			.append(calendar.get(Calendar.DAY_OF_YEAR));

		return getHash(tokenValue.toString());
	}

	private String buildRequestToken(User user, SignedRequest request) {
		StringBuilder tokenValue = new StringBuilder()
			.append(user.getUid())
			.append(user.getCountLogins())
			.append(request.buildRequestTokenValue());
		
		return getHash(tokenValue.toString());
	}

	private Boolean isAuthenticated() {
		return (getUser() != null && getUser().getUid() > 0);
	}

	private Parking getLastParking() {
		StringBuilder query = new StringBuilder()
			.append("SELECT * FROM parking WHERE userid = ")
			.append(getUser().getUid())
			.append(" AND gateIn IS NOT NULL AND gateOut IS NULL");

		try (Connection con = getSql2o().open()) {
			List<Parking> parkings = con.createQuery(query.toString()).executeAndFetch(Parking.class);

			if (!parkings.isEmpty()) {
				return parkings.get(0);

			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private BasicResponse checkIn(ParkingRequest request) {
		// open gate
		if (getGateProvider().openGate(request.getGateId())) {
			String currentTimestamp = getTime();
			String query = "INSERT INTO parking (userId, gateIn, timeIn) VALUES(:userId, :gateIn, :timeIn)";

			try (Connection con = getSql2o().open()) {
				con.createQuery(query.toString())
					.addParameter("userId", getUser().getUid())
					.addParameter("gateIn", request.getGateId())
					.addParameter("timeIn", currentTimestamp)
					.executeUpdate();
			} catch (Exception e) {
				return new BasicResponse(false, "Check-in failed, couldn't process request");
			}

			return new ParkingResponse(true, "Check-in at gate " + request.getGateId() + " successful",
					request.getGateId(), 1, currentTimestamp);
		}

		return new BasicResponse(false, "Check-in failed, couldn't open gate");
	}

	private BasicResponse checkOut(ParkingRequest request, Parking lastParking) {
		// calculate parking time
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long parkingTime;

		try {
			Date d1 = format.parse(lastParking.getTimeIn());
			Date d2 = format.parse(getTime());

			long diff = d2.getTime() - d1.getTime();
			parkingTime = diff / 1000;

		} catch (ParseException e) {
			parkingTime = 60;
		}

		// calculate parking fee
		double parkingFee = 5.55 + 0.0125 * parkingTime;

		// round and format parking fee
		parkingFee = Math.round(parkingFee * 20.0) / 20.0;
		
		DecimalFormat df = new DecimalFormat("0.00");

		// process payment 
		if (getPaymentProvider().processPayment(getUser(), parkingFee)) {
			// open gate
			if (getGateProvider().openGate(request.getGateId())) {
				String currentTimestamp = getTime();
				String query = "UPDATE parking SET gateOut = :gateOut, timeOut = :timeOut, parkingFee = :parkingFee WHERE uid = :uid";

				try (Connection con = getSql2o().open()) {
					con.createQuery(query)
						.addParameter("gateOut", request.getGateId())
						.addParameter("timeOut", currentTimestamp)
						.addParameter("parkingFee", parkingFee)
						.addParameter("uid", lastParking.getUid())
						.executeUpdate();
				} catch (Exception e) {
					return new BasicResponse(false, "Check-out failed, couldn't process request");
				}
				
				StringBuilder parkingResponse = new StringBuilder()
					.append("Check-out at gate " + request.getGateId() + " successful, ")
					.append("Parking duration: ")
						.append(convertSeconds((int)parkingTime))
					.append(" - Parking fees: ")
						.append(df.format(parkingFee)).append(" CHF");
				
				return new ParkingResponse(true, parkingResponse.toString(), request.getGateId(), 2);		
			}

			return new BasicResponse(false, "Check-out failed, couldn't open gate");
		}

		return new BasicResponse(false, "Check-out failed, couldn't process payment");
	}

	private String getTime() {
		Calendar currentdate = Calendar.getInstance();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("CET"));

		return formatter.format(currentdate.getTime());
	}
	
	private String convertSeconds(int seconds){
	    int h = seconds/ 3600;
	    int m = (seconds % 3600) / 60;
	    int s = seconds % 60;
	    String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
	    String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
	    String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
	    return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
	}

	private String getHash(String data) {
		try {
			// add salt
			data = data + getConfig().getBackend().getHashSalt();

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			return "";
		}
	}

	private User getUser() {
		return user;
	}

	private void setUser(User user) {
		this.user = user;
	}

	private ComfyParkConfig getConfig() {
		return config;
	}

	private void setConfig(ComfyParkConfig config) {
		this.config = config;
	}

	private GateProvider getGateProvider() {
		return gateProvider;
	}

	private void setGateProvider(GateProvider gateProvider) {
		this.gateProvider = gateProvider;
	}

	private PaymentProvider getPaymentProvider() {
		return paymentProvider;
	}

	private void setPaymentProvider(PaymentProvider paymentProvider) {
		this.paymentProvider = paymentProvider;
	}

	private Sql2o getSql2o() {
		return sql2o;
	}

	private void setSql2o(Sql2o sql2o) {
		this.sql2o = sql2o;
	}
}