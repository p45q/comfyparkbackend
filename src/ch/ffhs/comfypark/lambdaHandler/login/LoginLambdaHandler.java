package ch.ffhs.comfypark.lambdaHandler.login;

import ch.ffhs.comfypark.lambdaHandler.LambdaHandler;
import ch.ffhs.comfypark.lambdaHandler.login.models.LoginRequest;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LoginLambdaHandler extends LambdaHandler implements RequestHandler<LoginRequest, BasicResponse> {

	public LoginLambdaHandler() {
		super();
	}

	public BasicResponse handleRequest(LoginRequest request, Context context) {
		return getComfyPark().processLoginRequest(request);
	}
}