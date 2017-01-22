package ch.ffhs.comfypark.lambdaHandler.status;

import ch.ffhs.comfypark.lambdaHandler.LambdaHandlerSigned;
import ch.ffhs.comfypark.lambdaHandler.models.SignedRequest;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class StatusLambdaHandler extends LambdaHandlerSigned implements RequestHandler<SignedRequest, BasicResponse> {

	public StatusLambdaHandler() {
		super();
	}

	public BasicResponse handleRequest(SignedRequest request, Context context) {
		BasicResponse authenticationResponse = handleSignedRequest(request, context);

		if (!authenticationResponse.getSuccess()) {
			return authenticationResponse;
		}

		return getComfyPark().processStatusRequest(request);
	}
}