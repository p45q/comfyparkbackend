package ch.ffhs.comfypark.lambdaHandler.models;

public class UnauthorizedResponse extends BasicResponse {
	private Boolean unauthorized;

	public UnauthorizedResponse(Boolean success, String message, Boolean unauthorized) {
		super(success, message);

		setUnauthorized(unauthorized);
	}
	
	public UnauthorizedResponse(Boolean success, String message) {
		this(success, message, true);
	}
	
	
	
	public Boolean getUnauthorized() {
		return unauthorized;
	}

	public void setUnauthorized(Boolean unauthorized) {
		this.unauthorized = unauthorized;
	}
}