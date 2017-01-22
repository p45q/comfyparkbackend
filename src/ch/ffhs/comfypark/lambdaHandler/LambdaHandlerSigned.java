package ch.ffhs.comfypark.lambdaHandler;

import com.amazonaws.services.lambda.runtime.Context;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;
import ch.ffhs.comfypark.lambdaHandler.models.SignedRequest;

public abstract class LambdaHandlerSigned extends LambdaHandler {
	public LambdaHandlerSigned() {
		super();
	}

	public BasicResponse handleSignedRequest(SignedRequest request, Context context) {
		// verify user token and authenticate
		BasicResponse authenticationResponse = getComfyPark().processUserTokenAuthentication(request);
		if (!authenticationResponse.getSuccess()) {
			return authenticationResponse;
		}

		// verify request token
		BasicResponse requestVerificationResponse = getComfyPark().processRequestTokenVerification(request);
		if (!requestVerificationResponse.getSuccess()) {
			return requestVerificationResponse;
		}

		return new BasicResponse("Authenticated and verified successful");
	}
}