package ch.ffhs.comfypark.mysql.models;

public class Parking {
	private int uid;
	private int userId;
	private int gateIn;
	private String timeIn;
	private int gateOut;
	private String timeOut;
	private double parkingFee;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGateIn() {
		return gateIn;
	}

	public void setGateIn(int gateIn) {
		this.gateIn = gateIn;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = fixDateTime(timeIn);
	}

	public int getGateOut() {
		return gateOut;
	}

	public void setGateOut(int gateOut) {
		this.gateOut = gateOut;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = fixDateTime(timeOut);
	}

	public double getParkingFee() {
		return parkingFee;
	}

	public void setParkingFee(double parkingFee) {
		this.parkingFee = parkingFee;
	}

	private String fixDateTime(String string) {
		if (string != null && string.length() > 0 && string.charAt(string.length() - 2) == '.') {
			string = string.substring(0, string.length() - 2);
		}
		return string;
	}
}
