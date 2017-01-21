package ch.ffhs.comfypark.lambdaHandler.parking.models;

import ch.ffhs.comfypark.lambdaHandler.models.BasicRequest;

public class ParkingRequest extends BasicRequest {
	private int gateId;

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

}