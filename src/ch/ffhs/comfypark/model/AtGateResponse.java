package ch.ffhs.comfypark.model;

public class AtGateResponse {
	private int gateUUID;
	private int gateAction;
	private String result;
	private String timeIn;
	private Boolean success;

	public AtGateResponse(String result, Boolean success, int gateUUID, int gateAction, String timeIn) {
		this.result = result;
		this.success = success;
		this.gateUUID = gateUUID;
		this.gateAction = gateAction;
		this.timeIn = timeIn;
	}

	public AtGateResponse(String result, int gateUUID, int gateAction) {
		this(result, true, gateUUID, gateAction, "");
	}

	public AtGateResponse(String result, int gateUUID, int gateAction, String timeIn) {
		this(result, true, gateUUID, gateAction, timeIn);
	}

	public AtGateResponse(String result) {
		this(result, true, 0, 0, "");
	}

	public AtGateResponse(String result, Boolean success) {
		this(result, success, 0, 0, "");
	}

	public int getGateUUID() {
		return gateUUID;
	}

	public void setGateUUID(int gateUUID) {
		this.gateUUID = gateUUID;
	}

	public int getGateAction() {
		return this.gateAction;
	}

	public void setGateAction(int gateAction) {
		this.gateAction = gateAction;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}