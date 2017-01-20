package ch.ffhs.comfypark.model;

public class AtGateRequest {
	private String cmd;

	private int gateUUID;
	private String customerID;

	private String username;
	private String password;

	public AtGateRequest(String cmd, int gateUUID, String customerID, String username, String password) {
		this.cmd = cmd;

		this.gateUUID = gateUUID;
		this.customerID = customerID;

		this.username = username;
		this.password = password;
	}

	public AtGateRequest() {
		this("", 0, "", "", "");
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getGateUUID() {
		return gateUUID;
	}

	public void setGateUUID(int firstName) {
		this.gateUUID = firstName;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}