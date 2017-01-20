package ch.ffhs.comfypark;

import ch.ffhs.comfypark.model.AtGateRequest;
import ch.ffhs.comfypark.model.AtGateResponse;
import ch.ffhs.comfypark.model.db.Parking;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

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

public class LambdaFunctionHandler implements RequestHandler<AtGateRequest, AtGateResponse> {
	private GateProvider gateProvider;
	private PaymentProvider paymentProvider;

	private Sql2o sql2o;

	public LambdaFunctionHandler() {
		super();

		gateProvider = new GateProvider();
		paymentProvider = new PaymentProvider();

		sql2o = new Sql2o("jdbc:mysql://comfyparkdb.cjn6mrex9bqq.eu-west-1.rds.amazonaws.com:3306/comfypark", "ffhs",
				"glauer.ch");
	}

	public AtGateResponse handleRequest(AtGateRequest request, Context context) {
		// check required data
		if (request.getCmd().length() == 0) {
			return new AtGateResponse("Error required data missing", false);
		}

		// check checkin/checkout
		Parking lastParking = getLastParking(request);

		switch (request.getCmd()) {
		case "getStatus":
			if (lastParking == null) {
				return new AtGateResponse("You haven't Checked-In", 0, 000);
			} else {
				return new AtGateResponse("You have successfully Checked-In at Gate " + lastParking.getUid(),
						lastParking.getGateIn(), 1, lastParking.getTimeIn());
			}

		case "parking":
			// check required data
			if (request.getGateUUID() == 0 || request.getCustomerID() == 0) {
				return new AtGateResponse("Error required data missing", false);
			}

			if (lastParking == null) {
				return checkIn(request);
			} else {
				return checkOut(request, lastParking);
			}

		}

		return new AtGateResponse("Error required data missing", false);
	}

	private Parking getLastParking(AtGateRequest request) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM parking WHERE userid = ").append(request.getCustomerID())
				.append(" AND gateIn IS NOT NULL AND gateOut IS NULL");

		try (Connection con = sql2o.open()) {
			List<Parking> parkings = con.createQuery(query.toString()).executeAndFetch(Parking.class);

			if (parkings.isEmpty()) {
				return null;
			} else {
				return parkings.get(0);
			}
		} catch (Exception e) {
			return null;
		}

	}

	private AtGateResponse checkIn(AtGateRequest request) {
		if (gateProvider.openGate(request.getGateUUID())) {
			StringBuilder query = new StringBuilder();
			String currentTimestamp = getTime();

			query.append("INSERT INTO parking (userId, gateIn, timeIn) VALUES(:userId, :gateIn, :timeIn)");

			try (Connection con = sql2o.open()) {
				con.createQuery(query.toString()).addParameter("userId", request.getCustomerID())
						.addParameter("gateIn", request.getGateUUID()).addParameter("timeIn", currentTimestamp)
						.executeUpdate();
			} catch (Exception e) {
				return new AtGateResponse("Error could not process Check-In", false);
			}

			return new AtGateResponse("You have successfully Checked-In at Gate " + request.getGateUUID(),
					request.getGateUUID(), 1, currentTimestamp);
		}

		return new AtGateResponse("Error could not open Gate", false);
	}

	private AtGateResponse checkOut(AtGateRequest request, Parking parking) {
		// calc parking time
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long parkingTime;

		try {
			Date d1 = format.parse(parking.getTimeIn());
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

		if (paymentProvider.processPayment(request.getCustomerID(), price)) {
			if (gateProvider.openGate(request.getGateUUID())) {
				StringBuilder query = new StringBuilder();
				String currentTimestamp = getTime();

				query.append(
						"UPDATE parking SET gateOut = :gateOut, timeOut = :timeOut, price = :price WHERE uid = :uid");

				try (Connection con = sql2o.open()) {
					con.createQuery(query.toString()).addParameter("gateOut", request.getGateUUID())
							.addParameter("timeOut", currentTimestamp).addParameter("price", price)
							.addParameter("uid", parking.getUid()).executeUpdate();
				} catch (Exception e) {
					return new AtGateResponse("Error could not process Check-Out", false);
				}

				return new AtGateResponse("You have successfully Checked-Out at Gate " + request.getGateUUID()
						+ " - Parking Taxes: " + price + " CHF", request.getGateUUID(), 2);
			}

			return new AtGateResponse("Error could not open Gate", false);
		}

		return new AtGateResponse("Error could process Payment", false);
	}

	private String getTime() {
		Calendar currentdate = Calendar.getInstance();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("CET"));

		return formatter.format(currentdate.getTime());
	}
}