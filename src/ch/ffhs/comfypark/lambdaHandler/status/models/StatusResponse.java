package ch.ffhs.comfypark.lambdaHandler.status.models;

import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;

public class StatusResponse extends BasicResponse {
	private int gateId;
	private String timeIn;

	public StatusResponse(Boolean success, String message, int gateId, String timeIn) {
		super(success, message);

		setGateId(gateId);
		setTimeIn(timeIn);
	}

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

}