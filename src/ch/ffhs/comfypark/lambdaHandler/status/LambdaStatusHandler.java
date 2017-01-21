package ch.ffhs.comfypark.lambdaHandler.status;

import ch.ffhs.comfypark.lambdaHandler.LambdaHandler;
import ch.ffhs.comfypark.lambdaHandler.models.BasicRequest;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaStatusHandler extends LambdaHandler implements RequestHandler<BasicRequest, BasicResponse> {

	public LambdaStatusHandler() {
		super();
	}

	@Override
	public BasicResponse handleRequest(BasicRequest request, Context context) {
		// authenticate
		BasicResponse authenticationResponse = getComfyPark().processAuthentication(request);
		if (!authenticationResponse.getSuccess()) {
			return authenticationResponse;
		}

		return getComfyPark().processStatusRequest(request);
	}
}