package ch.ffhs.comfypark.config.models;

public class Requests {
	private Status status;
	private Login login;
	private Parking parking;

	public Requests(){
		setStatus(new Status());
		setLogin(new Login());
		setParking(new Parking());
	}
	/**
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return login
	 */
	public Login getLogin() {
		return login;
	}

	/**
	 * @param login
	 */
	public void setLogin(Login login) {
		this.login = login;
	}

	/**
	 * @return parking
	 */
	public Parking getParking() {
		return parking;
	}

	/**
	 * @param parking
	 */
	public void setParking(Parking parking) {
		this.parking = parking;
	}
}