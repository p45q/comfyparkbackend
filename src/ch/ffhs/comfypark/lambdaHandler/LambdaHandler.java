package ch.ffhs.comfypark.lambdaHandler;

import ch.ffhs.comfypark.ComfyPark;

public class LambdaHandler {
	private ComfyPark comfyPark;

	public LambdaHandler() {
		setComfyPark(new ComfyPark());
	}

	protected ComfyPark getComfyPark() {
		return comfyPark;
	}

	protected void setComfyPark(ComfyPark comfyPark) {
		this.comfyPark = comfyPark;
	}
}