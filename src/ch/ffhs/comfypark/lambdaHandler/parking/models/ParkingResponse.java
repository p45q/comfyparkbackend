package ch.ffhs.comfypark.lambdaHandler.parking.models;

import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;

public class ParkingResponse extends BasicResponse {
	private int gateId;
	private int gateAction;
	private String timeIn;

	public ParkingResponse(Boolean success, String message, int gateId, int gateAction, String timeIn) {
		super(success, message);

		setGateId(gateId);
		setGateAction(gateAction);
		setTimeIn(timeIn);
	}

	public ParkingResponse(Boolean success, String message, int gateId, int gateAction) {
		this(success, message, gateId, gateAction, "");
	}
	
	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

	public int getGateAction() {
		return gateAction;
	}

	public void setGateAction(int gateAction) {
		this.gateAction = gateAction;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

}