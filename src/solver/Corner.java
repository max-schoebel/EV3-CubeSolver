package solver;

import enums.ColorsEnum;
import enums.FacesEnum;

/**
 * Class that represents a corner of the Rubik's Cube.
 */
public class Corner {
	
	/**
	 * First color of the corner.
	 */
	private ColorsEnum color1;
	
	/**
	 * Second color of the corner.
	 */
	private ColorsEnum color2;
	
	/**
	 * Third color of the corner.
	 */
	private ColorsEnum color3;

	/**
	 * First face that the corner is on.
	 */
	private FacesEnum face1;
	
	/**
	 * Second face that the corner is on.
	 */
	private FacesEnum face2;
	
	/**
	 * Third face that the corner is on.
	 */
	private FacesEnum face3;

	/**
	 * Constructor that reads faces of the corner from parameters and colors of the corner from the elements array of the Rubik's Cube.
	 * @param cube Elements array of the Rubik's Cube.
	 * @param face1 First face that the corner is on.
	 * @param index1 Number of element on face1 that belongs to corner.
	 * @param face2 Second face that the corner is on.
	 * @param index2 Number of element on face2 that belongs to corner.
	 * @param face3 Third face that the corner is on.
	 * @param index3 Number of element on face3 that belongs to corner.
	 */
	Corner(ColorsEnum[][] cube, FacesEnum face1, int index1, FacesEnum face2, int index2, FacesEnum face3, int index3) {
		this.color1 = cube[face1.ordinal()][index1];
		this.color2 = cube[face2.ordinal()][index2];
		this.color3 = cube[face3.ordinal()][index3];
		this.face1 = face1;
		this.face2 = face2;
		this.face3 = face3;
	}

	/**
	 * Checks if corner has an element with given color.
	 * @param color Color of element
	 * @return Boolean; true if element of this color belongs to corner.
	 */
	public boolean hasColor(ColorsEnum color) {
		return (this.color1 == color || this.color2 == color || this.color3 == color);
	}
	
	/**
	 * Checks if corner is on given face.
	 * @param face Face of the Rubik's Cube.
	 * @return Boolean; true if corner is on face
	 */
	public boolean onFace(FacesEnum face) {
		return (this.face1 == face || this.face2 == face || this.face3 == face);
	}
}
