package ch.ffhs.comfypark.model;

public class AtGateRequest {
	private int gateUUID;
	private int customerID;
	private String cmd;

	public AtGateRequest(int gateUUID, int customerID, String cmd) {
		this.gateUUID = gateUUID;
		this.customerID = customerID;
		this.cmd = cmd;
	}

	public AtGateRequest() {
		this(0, 0, "");
	}

	public int getGateUUID() {
		return gateUUID;
	}

	public void setGateUUID(int firstName) {
		this.gateUUID = firstName;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
}