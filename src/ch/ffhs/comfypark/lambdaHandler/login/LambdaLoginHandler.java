package ch.ffhs.comfypark.lambdaHandler.login;

import ch.ffhs.comfypark.lambdaHandler.LambdaHandler;
import ch.ffhs.comfypark.lambdaHandler.login.models.LoginRequest;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaLoginHandler extends LambdaHandler implements RequestHandler<LoginRequest, BasicResponse> {

	public LambdaLoginHandler() {
		super();
	}

	@Override
	public BasicResponse handleRequest(LoginRequest request, Context context) {
		// check required data
		if (request == null || request.getUsername() == null || request.getUsername().length() == 0
				|| request.getPassword() == null || request.getPassword().length() == 0) {
			return new BasicResponse(false, "Error required data missing");
		}

		return getComfyPark().processLoginRequest(request);
	}
}