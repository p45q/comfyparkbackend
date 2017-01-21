package ch.ffhs.comfypark.config.models;

public class MySQL {
	private String servername;
	private String username;
	private String password;
	private String dbname;

	/**
	 * @return servername
	 */
	public String getServername() {
		return servername;
	}

	/**
	 * @param servername
	 */
	public void setServername(String servername) {
		this.servername = servername;
	}

	/**
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return dbname
	 */
	public String getDbname() {
		return dbname;
	}

	/**
	 * @param dbname
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
}