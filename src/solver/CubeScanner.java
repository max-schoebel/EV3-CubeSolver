package solver;

import java.io.FileWriter;
import java.io.IOException;

import enums.AllMoves;
import enums.ColorsEnum;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;

/**
 * This class implements methods for scanning all colored elements of the cube and writing them into an RubiksCube object.
 * EV3-ColorSensor is used to take seven samples per element. Mean value of those seven samples is used to identify element's color.
 * Raw values and mean value are written to RubiksCube object (raw values are saved for debugging). 
 */
public class CubeScanner {
	/**
	 * MotorController object to control motors during scanning process.
	 */
	private MotorController scannerMotors;
	
	/**
	 * SampleProvider that fetches raw RBG-values.
	 */
	private SampleProvider rawRGB;
	
	/**
	 * Filter that fetches mean RGB-value.
	 */
	private MeanFilter meanRGB;

	/**
	 * Number of samples that are taken per element.
	 */
	private final int SAMPLES = 7;
	
	/**
	 * Constructor initializes used variables.
	 * @param motors MotorController object to control motors during scanning process
	 */
	CubeScanner(MotorController motors) {
		EV3ColorSensor sensor = new EV3ColorSensor(SensorPort.S1);
		rawRGB = sensor.getRGBMode();
		scannerMotors = motors;
		meanRGB = new MeanFilter(rawRGB, SAMPLES);
	}
	
	/**
	 * Writes raw RGB-values and mean RGB-value to file (used for debugging).
	 * @param fileName Name of the file that values are written to.
	 * @param cube RubiksCube object where values are taken from.
	 */
	private void toFile(String fileName, RubiksCube cube) {
		try {
			FileWriter colorWriter = new FileWriter(fileName);
			for (int i = 0; i < 6; i++) {
				colorWriter.write("Face " + i + "\n");
				for (int j = 0; j < 8; j++) {
					float[][] rawColors = cube.elements[i][j].getRawColors();
					float[] colors = cube.elements[i][j].getrgbColors();
					for (int k = 0; k < 7; k++) {
						colorWriter.write(rawColors[k][0] + "\t" + rawColors[k][1] + "\t" + rawColors[k][2] + "\t" + "\n");
					}
					colorWriter.write("mean: " + colors[0] + "\t" + colors[1] + "\t" + colors[2] + "\t" + cube.elements[i][j] + "\n");
				}
			}
			colorWriter.write(""+cube.completeIntegrity());
			colorWriter.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Prints given sample-array of mean values to EV3-display.
	 * @param sample sample-array of mean values.
	 */
	private void toDisplay(float[][] sample) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				LCD.drawString(Float.toString(sample[i][j]),j*5,i);
				LCD.drawChar(' ',j*5,i);
			}
			LCD.drawChar(' ',15,i);
			LCD.drawString("" + this.convertToColorsEnum(sample[i]),16,i);
			LCD.drawChar(' ',17,i);
		}
		Button.ENTER.waitForPress();
	}
	
	/**
	 * Identifies color of one element by analyzing its RGB-values.
	 * @param sample RGB-values
	 * @return Color of cube element (ColorsEnum obejct)
	 */
	private ColorsEnum convertToColorsEnum(float[] sample) {
		if (sample[0] > 0.1 && sample[1] > 0.1) {
			if (sample[2] > 0.05) return ColorsEnum.W;
			else return ColorsEnum.Y; 
		}
		else if (sample[0] < 0.1 && sample[1] > 0.1 && sample[2] < 0.1) return ColorsEnum.G;
		else if (sample[0] < 0.05 && sample[1] < 0.1) return ColorsEnum.B;
		else if (sample[1] < 0.1 && sample[2] < 0.1) {
			if (sample[1] < 0.05) return ColorsEnum.R;
			else return ColorsEnum.O;
		}
		else return ColorsEnum.N;
	}
	
	/**
	 * Rotates cube while scanning its elements on top face.
	 * Seven raw values and mean value of these raw samples are saved and written to RubiksCube object.
	 * @return Array of colors that are on top face.
	 */
	private ColorsEnum[] scanTopFace() {
		float[][] sample = new float[8][3]; 
		float[][][] rawSample = new float [8][7][3];
		ColorsEnum[] topFace = new ColorsEnum[8];

		scannerMotors.tableTurnScan();
		for (int i = 0; i < 8; i++) {
			for (int j = -3; j <= 3; j++) {
				while (scannerMotors.getTableTachoCount() < ((i+1)*45 + j)) {}
				meanRGB.fetchSample(sample[i],0);
				rawRGB.fetchSample(rawSample[i][j+3], 0);
			}
		}

//		this.toDisplay(sample); /* for debug-use */

		for (int i = 0; i < 8; i++) {
			topFace[(i+4) % 8] = convertToColorsEnum(sample[i]);
			topFace[(i+4) % 8].setrgbColors(sample[i]);
			topFace[(i+4) % 8].setRawColors(rawSample[i]);
		}
		return topFace;
	}

	/**
	 * Scans complete cube by using scanTopFace()-method and writes recognized colors as well as raw values to RubiksCube object.
	 * @param cube RubiksCube object to write colors to.
	 */
	protected void scanCube(RubiksCube cube) {
		AllMoves[] scanManeuver1 = {AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.R, AllMoves.FI, AllMoves.SR, AllMoves.F};
		AllMoves[] scanManeuver2 = {AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.R, AllMoves.F, AllMoves.SR, AllMoves.FI};
		
		for (int i = 0; i < 6; i++) {
			cube.setTopFace(scanTopFace());
			cube.rotate();
			scannerMotors.waitCompleteTable();
			scannerMotors.reallignTable();
			if (i < 5) {
				scannerMotors.applyMoves((i % 2 == 0) ? scanManeuver1 : scanManeuver2, true);
				cube.applyMoves((i % 2 == 0) ? scanManeuver1 : scanManeuver2);
			}
		}

		this.toFile("colorDebug", cube); /*for debug-use*/
	}
}
