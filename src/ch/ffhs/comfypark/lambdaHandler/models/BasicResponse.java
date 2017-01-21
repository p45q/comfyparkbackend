package ch.ffhs.comfypark.lambdaHandler.models;

public class BasicResponse {
	private Boolean success;
	private String message;

	public BasicResponse(Boolean success, String message) {
		setSuccess(success);
		setMessage(message);
	}

	public BasicResponse() {
		this(true, "");
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
