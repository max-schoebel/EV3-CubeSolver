package enums;

/**
 * Enumeration class for identifying the colors of Rubik's Cube's elements.
 * Raw and mean RGB-values are saved with every element for debugging.
 */
public enum ColorsEnum {
	Y, W , G, O, B, R, N;
	
	/**
	 * Array that contains mean RGB-values from seven raw samples.
	 */
	float[] rgbColors = new float[3];
	
	/**
	 * Array that contains seven samples of raw RGB-values.
	 */
	float[][] rawColors = new float[7][3];
	
	/**
	 * Constructor that initializes color arrays with 0.
	 */
	private ColorsEnum() {
		for (int i = 0; i < 3; i++) {
			rgbColors[i] = 0;
			for (int j = 0; j < 7; j++) {
				rawColors[j][i] = 0;
			}
		}
	}

	/**
	 * Write given RGB sample into mean value array.
	 * @param rgbColors RGB values
	 */
	public void setrgbColors(float[] rgbColors) {
		for (int i = 0; i < 3; i++) {
			this.rgbColors[i] = rgbColors[i];
		} 
	}

	/**
	 * Return mean RGB-sample.
	 * @return Mean RGB-sample.
	 */
	public float[] getrgbColors() {
		return this.rgbColors;
	}

	/**
	 * Write seven RGB samples into raw value array.
	 * @param rawColors Seven RGB samples
	 */
	public void setRawColors(float[][] rawColors) {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 3; j++) {
				this.rawColors[i][j] = rawColors[i][j];
			}
		}
	}
	
	/**
	 * Return seven raw RGB-sample.
	 * @return Seven RGB samples.
	 */
	public float[][] getRawColors() {
		return this.rawColors;
	}
}
