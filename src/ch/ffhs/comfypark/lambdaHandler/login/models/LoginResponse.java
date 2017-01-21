package ch.ffhs.comfypark.lambdaHandler.login.models;

import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;
import ch.ffhs.comfypark.mysql.models.User;

public class LoginResponse extends BasicResponse {
	private User user;
	private String userToken;

	public LoginResponse(Boolean success, String message, User user, String userToken) {
		super(success, message);

		setUser(user);
		setUserToken(userToken);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

}