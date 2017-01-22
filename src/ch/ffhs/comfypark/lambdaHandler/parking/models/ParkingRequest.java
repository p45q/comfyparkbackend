package ch.ffhs.comfypark.lambdaHandler.parking.models;

import ch.ffhs.comfypark.lambdaHandler.models.SignedRequest;

public class ParkingRequest extends SignedRequest {
	public int gateId;

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}
}