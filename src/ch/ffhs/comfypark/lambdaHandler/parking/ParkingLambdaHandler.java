package ch.ffhs.comfypark.lambdaHandler.parking;

import ch.ffhs.comfypark.lambdaHandler.LambdaHandlerSigned;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;
import ch.ffhs.comfypark.lambdaHandler.parking.models.ParkingRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ParkingLambdaHandler extends LambdaHandlerSigned implements RequestHandler<ParkingRequest, BasicResponse> {

	public ParkingLambdaHandler() {
		super();
	}

	public BasicResponse handleRequest(ParkingRequest request, Context context) {
		BasicResponse authenticationResponse = handleSignedRequest(request, context);

		if (!authenticationResponse.getSuccess()) {
			return authenticationResponse;
		}

		return getComfyPark().processParkingRequest(request);
	}
}