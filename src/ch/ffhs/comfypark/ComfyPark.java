package ch.ffhs.comfypark;

import ch.ffhs.comfypark.config.ComfyParkConfig;
import ch.ffhs.comfypark.config.models.MySQL;
import ch.ffhs.comfypark.lambdaHandler.login.models.LoginRequest;
import ch.ffhs.comfypark.lambdaHandler.login.models.LoginResponse;
import ch.ffhs.comfypark.lambdaHandler.models.BasicRequest;
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
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM users WHERE username = :username AND SHA2(CONCAT(password, '")
				.append(getConfig().getBackend().getHashSalt()).append("'), 256) = :password");

		try (Connection con = sql2o.open()) {
			List<User> users = con.createQuery(query.toString()).addParameter("username", request.getUsername())
					.addParameter("password", request.getPassword()).executeAndFetch(User.class);

			if (!users.isEmpty()) {
				return new LoginResponse(true, "Authentication success", users.get(0),
						getHash(Integer.toString(users.get(0).getUid())));
			}
		} catch (Exception e) {
			// TODO
		}

		return new BasicResponse(false, "Error - Authentication failed");
	}

	public BasicResponse processAuthentication(BasicRequest request) {
		if (request != null && request.getUserToken() != null && request.getUserToken().length() != 0) {
			StringBuilder query = new StringBuilder();

			query.append("SELECT * FROM users WHERE SHA2(CONCAT(uid, '" + getConfig().getBackend().getHashSalt()
					+ "'), 256) = '").append(request.getUserToken()).append("'");

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
				return new BasicResponse(true, "Authentication OK");
			}
		}

		return new BasicResponse(false, "Error authentication failed");
	}

	public BasicResponse processParkingRequest(ParkingRequest request) {
		// check checkin/checkout
		Parking lastParking = getLastParking();

		if (lastParking == null) {
			return checkIn(request);
		} else {
			return checkOut(request, lastParking);
		}
	}

	public BasicResponse processStatusRequest(BasicRequest request) {
		// check checkin/checkout
		Parking lastParking = getLastParking();

		if (lastParking == null) {
			return new BasicResponse(true, "You haven't Checked-In");
		} else {
			return new StatusResponse(true, "You have successfully Checked-In at Gate " + lastParking.getUid(),
					lastParking.getGateIn(), lastParking.getTimeIn());
		}
	}

	private Boolean isAuthenticated() {
		return (getUser() != null && getUser().getUid() > 0);
	}

	private Parking getLastParking() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM parking WHERE userid = ").append(getUser().getUid())
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
		if (getGateProvider().openGate(request.getGateId())) {
			StringBuilder query = new StringBuilder();
			String currentTimestamp = getTime();

			query.append("INSERT INTO parking (userId, gateIn, timeIn) VALUES(:userId, :gateIn, :timeIn)");

			try (Connection con = getSql2o().open()) {
				con.createQuery(query.toString()).addParameter("userId", getUser().getUid())
						.addParameter("gateIn", request.getGateId()).addParameter("timeIn", currentTimestamp)
						.executeUpdate();
			} catch (Exception e) {
				return new BasicResponse(false, "Error could not process Check-In");
			}

			return new ParkingResponse(true, "You have successfully Checked-In at Gate " + request.getGateId(),
					request.getGateId(), 1, currentTimestamp);
		}

		return new BasicResponse(false, "Error could not open Gate");
	}

	private BasicResponse checkOut(ParkingRequest request, Parking lastParking) {
		// calc parking time
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

		// calc parking price
		double price = 5.55 + 0.0125 * parkingTime;

		// format parking price
		DecimalFormat df = new DecimalFormat("#.##");
		price = Double.valueOf(df.format(price));

		if (getPaymentProvider().processPayment(getUser().getUid(), price)) {
			if (getGateProvider().openGate(request.getGateId())) {
				StringBuilder query = new StringBuilder();
				String currentTimestamp = getTime();

				query.append(
						"UPDATE parking SET gateOut = :gateOut, timeOut = :timeOut, price = :price WHERE uid = :uid");

				try (Connection con = getSql2o().open()) {
					con.createQuery(query.toString()).addParameter("gateOut", request.getGateId())
							.addParameter("timeOut", currentTimestamp).addParameter("price", price)
							.addParameter("uid", lastParking.getUid()).executeUpdate();
				} catch (Exception e) {
					return new BasicResponse(false, "Error could not process Check-Out");
				}

				return new ParkingResponse(true, "You have successfully Checked-Out at Gate " + request.getGateId()
						+ " - Parking Taxes: " + price + " CHF", request.getGateId(), 2);
			}

			return new BasicResponse(false, "Error could not open Gate");
		}

		return new BasicResponse(false, "Error could process Payment");
	}

	private String getTime() {
		Calendar currentdate = Calendar.getInstance();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("CET"));

		return formatter.format(currentdate.getTime());
	}

	public String getHash(String data) {
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
			//throw new RuntimeException(ex);
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