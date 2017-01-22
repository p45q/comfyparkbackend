package ch.ffhs.comfypark.lambdaHandler.models;

import java.lang.reflect.Field;

public class SignedRequest {
	public String userToken;
	public String requestToken;

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	public String buildRequestTokenValue() {
		StringBuilder requestHash = new StringBuilder();

		Class<?> objClass = this.getClass();

		Field[] fields = objClass.getFields();

		for (Field field : fields) {
			String name = field.getName();
			if (name.equals("requestToken")) {
				continue;
			}

			Object value = null;

			try {
				value = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// e.printStackTrace();
			}

			if (value == null) {
				value = "";
			}

			requestHash.append(name).append(value.toString());
		}

		return requestHash.toString();
	}
}