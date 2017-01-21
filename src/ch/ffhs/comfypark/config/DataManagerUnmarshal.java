package ch.ffhs.comfypark.config;

import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ch.ffhs.comfypark.config.models.Backend;
import ch.ffhs.comfypark.config.models.ComfyPark;
import ch.ffhs.comfypark.config.models.MySQL;

public class DataManagerUnmarshal {
	private final String inputXml;

	/**
	 * @param inputXml
	 */
	public DataManagerUnmarshal(String inputXml) {
		this.inputXml = inputXml;
	}

	/**
	 * @return xml
	 */
	private ComfyPark unmarshalData() {
		try {
			// JAXB context erstellen und initialisierung des marshaller
			JAXBContext jaxbContext = JAXBContext.newInstance(ComfyPark.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			return (ComfyPark) unmarshaller.unmarshal(new FileReader(inputXml));

		} catch (JAXBException je) {
			je.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	/**
	 * @return backend
	 */
	public Backend getBackend() {
		ComfyPark settings = unmarshalData();
		return settings.getBackend();
	}

	/**
	 * @return mysql
	 */
	public MySQL getMysql() {
		ComfyPark settings = unmarshalData();
		return settings.getMysql();
	}

}