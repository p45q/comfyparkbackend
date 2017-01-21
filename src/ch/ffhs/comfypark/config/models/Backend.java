package ch.ffhs.comfypark.config.models;

public class Backend {
	private Requests requests;
	private String apiKey;
	private String hashSalt;

	public Backend() {
		setRequests(new Requests());
	}

	/**
	 * @return requests
	 */
	public Requests getRequests() {
		return requests;
	}

	/**
	 * @param requests
	 */
	public void setRequests(Requests requests) {
		this.requests = requests;
	}

	/**
	 * @return apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return hashSalt
	 */
	public String getHashSalt() {
		return hashSalt;
	}

	/**
	 * @param hashSalt
	 */
	public void setHashSalt(String hashSalt) {
		this.hashSalt = hashSalt;
	}
}