package ch.ffhs.comfypark;

import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;

import ch.ffhs.comfypark.model.AtGateRequest;
import ch.ffhs.comfypark.model.AtGateResponse;

public class LambdaFunctionHandler implements RequestHandler<AtGateRequest, AtGateResponse>{   

    public AtGateResponse handleRequest(AtGateRequest request, Context context){
        return new AtGateResponse(0);
    }
}