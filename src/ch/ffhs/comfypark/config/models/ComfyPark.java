package ch.ffhs.comfypark.config.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "storage")
public class ComfyPark {
	private Backend backend;
	private MySQL mysql;

	/**
	 * @return backend
	 */
	public Backend getBackend() {
		return backend;
	}

	/**
	 * @param backend
	 */
	public void setBackend(Backend backend) {
		this.backend = backend;
	}

	/**
	 * @return mysql
	 */
	public MySQL getMysql() {
		return mysql;
	}

	/**
	 * @param mysql
	 */
	public void setMysql(MySQL mysql) {
		this.mysql = mysql;
	}
}