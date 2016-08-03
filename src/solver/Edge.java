package solver;

import enums.ColorsEnum;
import enums.FacesEnum;

/**
 * Class that represents an edge of the Rubik's Cube.
 */
public class Edge {
	
	/**
	 * First color of the edge.
	 */
	private ColorsEnum color1;
	
	/**
	 * Second color of the edge.
	 */
	private ColorsEnum color2;
	
	/**
	 * Fist face that the edge is on.
	 */
	private FacesEnum face1;
	
	/**
	 * Second face that the edge is on.
	 */
	private FacesEnum face2;

	/**
	 * Constructor that reads 
	 * Constructor that reads faces of the edge from parameters and colors of the edge from the elements array of the Rubik's Cube.
	 * @param cube Elements array of the Rubik's Cube.
	 * @param face1 First face that the edge is on.
	 * @param index1 Number of element on face1 that belongs to edge.
	 * @param face2 Second face that the edge is on.
	 * @param index2 Number of element on face2 that belongs to edge.
	 */
	Edge(ColorsEnum[][] cube, FacesEnum face1, int index1, FacesEnum face2, int index2) {
		this.color1 = cube[face1.ordinal()][index1];
		this.color2 = cube[face2.ordinal()][index2];
		this.face1 = face1;
		this.face2 = face2;
	}
	
	/**
	 * Checks if edge has an element with given color.
	 * @param color Color of element
	 * @return Boolean; true if element of this color belongs to edge.
	 */
	public boolean hasColor(ColorsEnum color) {
		return (this.color1 == color || this.color2 == color);
	}
	
	/**
	 * Checks if edge is on given face.
	 * @param face Face of the Rubik's Cube.
	 * @return Boolean; true if edge is on face
	 */
	public boolean onFace(FacesEnum face) {
		return (this.face1 == face || this.face2 == face);
	}
}
