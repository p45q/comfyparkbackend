package ch.ffhs.comfypark.model;

public class AtGateResponse {
    int gateAction;

	public int getGateAction() {
		return gateAction;
	}

	public void setGateAction(int gateAction) {
		this.gateAction = gateAction;
	}

	public AtGateResponse(int gateAction) {
		super();
		this.gateAction = gateAction;
	}

}