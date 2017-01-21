package ch.ffhs.comfypark.lambdaHandler.parking;

import ch.ffhs.comfypark.lambdaHandler.LambdaHandler;
import ch.ffhs.comfypark.lambdaHandler.models.BasicResponse;
import ch.ffhs.comfypark.lambdaHandler.parking.models.ParkingRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaParkingHandler extends LambdaHandler implements RequestHandler<ParkingRequest, BasicResponse> {

	public LambdaParkingHandler() {
		super();
	}

	@Override
	public BasicResponse handleRequest(ParkingRequest request, Context context) {
		// authenticate
		BasicResponse authenticationResponse = getComfyPark().processAuthentication(request);
		if (!authenticationResponse.getSuccess()) {
			return authenticationResponse;
		}

		// check required data
		if (request.getGateId() == 0) {
			return new BasicResponse(false, "Error required data missing");
		}

		return getComfyPark().processParkingRequest(request);
	}
}