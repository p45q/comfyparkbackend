package ch.ffhs.comfypark.teststuff;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Obj  implements Context {
    private ClientContext clientContext;
	private String str1;
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	private String str2;
	@Override
	public String getAwsRequestId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ClientContext getClientContext() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getFunctionName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CognitoIdentity getIdentity() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getLogGroupName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getLogStreamName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LambdaLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getMemoryLimitInMB() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getRemainingTimeInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}
}
