package ch.ffhs.comfypark.config;

import java.io.File;

import ch.ffhs.comfypark.config.models.Backend;
import ch.ffhs.comfypark.config.models.MySQL;

public class ComfyParkConfig {
	private static final String XML_CONFIG_AWS = "./src/ch/ffhs/comfypark/config.xml";
	private static final String XML_CONFIG_LOCAL = "./ch/ffhs/comfypark/config.xml";

	private Backend backend;
	private MySQL mysql;

	public ComfyParkConfig() {
		initConfig();
	}

	private void initConfig() {

		if (!readConfig(XML_CONFIG_AWS)) {
			if (!readConfig(XML_CONFIG_LOCAL)) {
				setBackend(new Backend());
				setMysql(new MySQL());
			}
		}
	}

	private Boolean readConfig(String configPath) {
		File f = new File(configPath);

		if (f.exists()) {
			DataManagerUnmarshal unmashaler = new DataManagerUnmarshal(configPath);

			setBackend(unmashaler.getBackend());
			setMysql(unmashaler.getMysql());

			return true;
		}

		return false;
	}

	public Backend getBackend() {
		return backend;
	}

	public void setBackend(Backend backend) {
		this.backend = backend;
	}

	public MySQL getMysql() {
		return mysql;
	}

	public void setMysql(MySQL mysql) {
		this.mysql = mysql;
	}

}