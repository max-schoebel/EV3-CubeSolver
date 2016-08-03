package solver;

import java.io.FileWriter;
import java.io.IOException;

import enums.AllMoves;
import enums.ColorsEnum;
import enums.FacesEnum;
import lejos.hardware.lcd.LCD;

/**
 * An object of this class represents the Rubik's Cube inside the Roberta-EV3CubeSolver.
 * All possible moves that can be applied to the cube are implemented here.
 * These moves are executed on the internal representation of the Rubik's Cube (on the elements array), not on the actual physical Rubik's cube.
 */
public class RubiksCube {
	
	/**
	 * Array that contains the colors of each element. This is the internal representation of the actual physical cube.
	 * 8 elements for 6 faces of the Rubik's Cube. Middle element is static on each face. 
	 * Front-face is headed towards fork. Upper left element has index 0. Indizes for other elements are assigned clockwise (upper middle element has index 1 and so on).
	 */
	protected ColorsEnum[][] elements = new ColorsEnum[6][8];
	
	/**
	 * Maximum length of solution that can be safed.
	 */
	public final int MAXSOLLENGTH = 1000;
	
	/**
	 * Number of 90 degrees rotations (used to determine Rubik's cube's orientation).
	 */
	private int rotations = 0;
	
	/**
	 * While recording is true every move on the cube is saved to the solution array.
	 * Cube is solved step by step. After removing redundancy from solution array, it contains the final solution.
	 */
	private boolean recording = false;
	
	/**
	 * Index of next empty element in solution array (current element that can be written to).
	 */
	public int solutionIndex = 0;
	
	/**
	 * Solution array that contains all performed moves and the final solution after removing redundancy.
	 */
	public AllMoves[] solution = new AllMoves[MAXSOLLENGTH];

	/**
	 * Setter-method for boolean variable "recording"
	 * @param rec boolean value to write in "recording"
	 */
	protected void setRecording(boolean rec) {this.recording = rec;}
	
	/**
	 * Getter-method for boolean variable "recording"
	 * @return recording
	 */
	protected boolean getRecording() {return this.recording;} 
	
	/**
	 * Constructor that initializes cube in NULL-state.
	 * Every element contains AllMoves.N.
	 */
	public RubiksCube() {
		resetNull();
	}
	
	/**
	 * Constructor that initializes cube in solved-state and applies given maneuver to cube.
	 * @param maneuver maneuver to apply to cube after initialization.
	 */
	public RubiksCube(AllMoves[] maneuver) {
		resetSolved();
		applyMoves(maneuver);
	}
	
	/**
	 * Method returns Edge object. Used for searching the position of a specifically colored edge.
	 * @param i Number of Edge to return (Rubik's cube has 12 edges).
	 * @return Edge object.
	 */
	protected Edge getEdge(int i) {
		switch (i) {
		case 0: return new Edge(elements, FacesEnum.U, 5, FacesEnum.F, 1);
		case 1: return new Edge(elements, FacesEnum.U, 7, FacesEnum.L, 1);
		case 2: return new Edge(elements, FacesEnum.U, 1, FacesEnum.B, 1);
		case 3:	return new Edge(elements, FacesEnum.U, 3, FacesEnum.R, 1);
		case 4:	return new Edge(elements, FacesEnum.F, 7, FacesEnum.L, 3);
		case 5: return new Edge(elements, FacesEnum.F, 3, FacesEnum.R, 7);
		case 6: return new Edge(elements, FacesEnum.L, 7, FacesEnum.B, 3);
		case 7:	return new Edge(elements, FacesEnum.R, 3, FacesEnum.B, 7);
		case 8: return new Edge(elements, FacesEnum.F, 5, FacesEnum.D, 1);
		case 9: return new Edge(elements, FacesEnum.L, 5, FacesEnum.D, 7);
		case 10: return new Edge(elements, FacesEnum.R, 5, FacesEnum.D, 3);
		case 11: return new Edge(elements, FacesEnum.B, 5, FacesEnum.D, 5);
		default: LCD.drawString("E-Return Failure", 0, 0); return new Edge(elements, FacesEnum.R, 0, FacesEnum.R, 0);
		}
	}
	
	/**
	 * Method returns Corner object. Used for searching the position of a specifically colored corner.
	 * @param i Number of corner to return (Rubik's Cube has 8 corners).
	 * @return Corner object.
	 */
	protected Corner getCorner(int i) {
		switch(i) {
		case 0: return new Corner(elements, FacesEnum.U, 6, FacesEnum.L, 2, FacesEnum.F, 0);
		case 1: return new Corner(elements, FacesEnum.U, 4, FacesEnum.R, 0, FacesEnum.F, 2);
		case 2: return new Corner(elements, FacesEnum.F, 6, FacesEnum.L, 4, FacesEnum.D, 0);
		case 3: return new Corner(elements, FacesEnum.F, 4, FacesEnum.R, 6, FacesEnum.D, 2);
		case 4: return new Corner(elements, FacesEnum.U, 0, FacesEnum.L, 0, FacesEnum.B, 2);
		case 5: return new Corner(elements, FacesEnum.U, 2, FacesEnum.R, 2, FacesEnum.B, 0);
		case 6: return new Corner(elements, FacesEnum.L, 6, FacesEnum.B, 4, FacesEnum.D, 6);
		case 7: return new Corner(elements, FacesEnum.R, 4, FacesEnum.B, 6, FacesEnum.D, 4);
		default: LCD.drawString("C-Return Failure", 0, 0); return new Corner(elements, FacesEnum.R, 0, FacesEnum.R, 0, FacesEnum.R, 0);
		}
	}
	
	/**
	 * Helper-method for applying counter clockwise rotations to a cube's face.
	 * @param face Face to rotate.
	 */
	private void turnFaceInverted(FacesEnum face) {
		ColorsEnum buffer;
		for (int i = 0; i < 2; i++) {
		buffer = elements[face.ordinal()][i];
		elements[face.ordinal()][i] = elements[face.ordinal()][2+i];
		elements[face.ordinal()][2+i] = elements[face.ordinal()][4+i];
		elements[face.ordinal()][4+i] = elements[face.ordinal()][6+i];
		elements[face.ordinal()][6+i] = buffer;
		}
	}
	
	/**
	 * Helper-method for applying clockwise rotations to a cube's face.
	 * @param face Face to rotate.
	 */
	private void turnFace(FacesEnum face) {
		ColorsEnum buffer;
		for (int i = 0; i < 2; i++) {
		buffer = elements[face.ordinal()][i];
		elements[face.ordinal()][i] = elements[face.ordinal()][6+i];
		elements[face.ordinal()][6+i] = elements[face.ordinal()][4+i];
		elements[face.ordinal()][4+i] = elements[face.ordinal()][2+i];
		elements[face.ordinal()][2+i] = buffer;
		}
	}
	
	/**
	 * Apply clockwise rotation to front face of cube-representation.
	 */
	private void front() {
		ColorsEnum buffer1;
		ColorsEnum buffer2;
		for (int i = 0; i < 3; i++) {
			buffer1 = elements[FacesEnum.U.ordinal()][4+i];
			buffer2 = elements[FacesEnum.D.ordinal()][i];
			elements[FacesEnum.U.ordinal()][4+i] = elements[FacesEnum.L.ordinal()][2+i];
			elements[FacesEnum.D.ordinal()][i] = elements[FacesEnum.R.ordinal()][(6+i)%8];		
			elements[FacesEnum.R.ordinal()][(6+i)%8] = buffer1;
			elements[FacesEnum.L.ordinal()][2+i] = buffer2;
		}
		turnFace(FacesEnum.F);
		if (recording) {solution[solutionIndex] = AllMoves.F; solutionIndex++;}
	}
	
	/**
	 * Apply counter clockwise rotation to front face of cube-representation.
	 */
	private void frontInverted() {
		ColorsEnum buffer1;
		ColorsEnum buffer2;
		for (int i = 0; i < 3; i++) {
			buffer1 = elements[FacesEnum.U.ordinal()][4+i];
			buffer2 = elements[FacesEnum.D.ordinal()][i];
			elements[FacesEnum.U.ordinal()][4+i] = elements[FacesEnum.R.ordinal()][(6+i)%8];
			elements[FacesEnum.D.ordinal()][i] = elements[FacesEnum.L.ordinal()][2+i];		
			elements[FacesEnum.R.ordinal()][(6+i)%8] = buffer2;
			elements[FacesEnum.L.ordinal()][2+i] = buffer1;
		}
		turnFaceInverted(FacesEnum.F);
		if (recording) {solution[solutionIndex] = AllMoves.FI; solutionIndex++;}
	}
	
	/**
	 * Apply clockwise rotation to down face of cube-representation.
	 */
	protected void down() {
		ColorsEnum buffer1;
		ColorsEnum buffer2;
		
		for (int i = 0; i < 3; i++) {
			buffer1 = elements[FacesEnum.F.ordinal()][4+i];
			buffer2 = elements[FacesEnum.B.ordinal()][4+i];			
			elements[FacesEnum.F.ordinal()][4+i] = elements[FacesEnum.L.ordinal()][4+i];
			elements[FacesEnum.B.ordinal()][4+i] = elements[FacesEnum.R.ordinal()][4+i];			
			elements[FacesEnum.R.ordinal()][4+i] = buffer1;
			elements[FacesEnum.L.ordinal()][4+i] = buffer2;
		}
		turnFace(FacesEnum.D);
		if (recording) {solution[solutionIndex] = AllMoves.D; solutionIndex++;}
	}
	
	/**
	 * Apply counter clockwise rotation to down face of cube-representation.
	 */
	private void downInverted() {
		ColorsEnum buffer1;
		ColorsEnum buffer2;
		
		for (int i = 0; i < 3; i++) {
			buffer1 = elements[FacesEnum.F.ordinal()][4+i];
			buffer2 = elements[FacesEnum.B.ordinal()][4+i];			
			elements[FacesEnum.F.ordinal()][4+i] = elements[FacesEnum.R.ordinal()][4+i];
			elements[FacesEnum.B.ordinal()][4+i] = elements[FacesEnum.L.ordinal()][4+i];			
			elements[FacesEnum.R.ordinal()][4+i] = buffer2;
			elements[FacesEnum.L.ordinal()][4+i] = buffer1;
		}
		turnFaceInverted(FacesEnum.D);
		if (recording) {solution[solutionIndex] = AllMoves.DI; solutionIndex++;}
	}
	
	/**
	 * Rotate cube-representation by 90 degrees clockwise (looking at down face).
	 */
	protected void rotate() {
		turnFaceInverted(FacesEnum.U);
		turnFace(FacesEnum.D);
		ColorsEnum buffer;
		for (int i = 0; i < 8; i++) {
			buffer = elements[FacesEnum.F.ordinal()][i];
			elements[FacesEnum.F.ordinal()][i] = elements[FacesEnum.L.ordinal()][i];
			elements[FacesEnum.L.ordinal()][i] = elements[FacesEnum.B.ordinal()][i];
			elements[FacesEnum.B.ordinal()][i] = elements[FacesEnum.R.ordinal()][i];
			elements[FacesEnum.R.ordinal()][i] = buffer;
		}
		rotations = (rotations -1) % 4;
		if (recording) {solution[solutionIndex] = AllMoves.R; solutionIndex++;}
	}
	
	/**
	 * Rotate cube-representation by 90 degrees counter clockwise (looking at down face).
	 */
	private void rotateInverted() {
		turnFaceInverted(FacesEnum.D);
		turnFace(FacesEnum.U);
		ColorsEnum buffer;
		for (int i = 0; i < 8; i++) {
			buffer = elements[FacesEnum.F.ordinal()][i];
			elements[FacesEnum.F.ordinal()][i] = elements[FacesEnum.R.ordinal()][i];
			elements[FacesEnum.R.ordinal()][i] = elements[FacesEnum.B.ordinal()][i];
			elements[FacesEnum.B.ordinal()][i] = elements[FacesEnum.L.ordinal()][i];
			elements[FacesEnum.L.ordinal()][i] = buffer;
		}
		rotations = (rotations +1) % 4;
		if (recording) {solution[solutionIndex] = AllMoves.RI; solutionIndex++;}
	}
	
	/**
	 * Reset cube-representation to solved-state.
	 */
	protected void resetSolved() {
		for (FacesEnum facecounter : FacesEnum.values()) {
			for (int i = 0; i < 8; i++) {
				elements[facecounter.ordinal()][i] = ColorsEnum.values()[facecounter.ordinal()];
			}
		}
		rotations = 0;
		solutionIndex = 0;
		for (int i = 0; i < MAXSOLLENGTH; i++) {solution[i] = AllMoves.N;}
		recording = false;
	}
	
	/**
	 * Reset cube-representation to NULL-state.
	 */
	protected void resetNull() {
		for (FacesEnum facecounter : FacesEnum.values()) {
			for (int i = 0; i < 8; i++) {
				elements[facecounter.ordinal()][i] = ColorsEnum.N;
			}
		}
		rotations = 0;
		solutionIndex = 0;
		for (int i = 0; i < MAXSOLLENGTH; i++) {solution[i] = AllMoves.N;}
		recording = false;
	}
	
	/**
	 * Reset cube-representation to randomly scrambled state.
	 * @param length Length of randomly generated maneuver that is applied to scramble the cube.
	 */
	protected void resetScrambled(int length) {
		resetSolved();
		AllMoves[] scrambleMoves = new AllMoves[length];
		for (int i = 0; i < length; i++) {
			scrambleMoves[i] = AllMoves.values()[(int)(Math.random()*9)];
		}
		applyMoves(scrambleMoves);
	}
	
	/**
	 * Writes colors from given array into top face of cube-representation (the elememts-array).
	 * @param scannedFace
	 */
	protected void setTopFace(ColorsEnum[] scannedFace) {
		for (int i = 0; i < 8; i++) {
			elements[FacesEnum.U.ordinal()][i] = scannedFace[i];
		}
	}
	
	/**
	 * Apply given moves to cube-representation.
	 * @param moves Array that contains moves to apply to cube.
	 */
	protected void applyMoves(AllMoves[] moves) {
		for (int i = 0; i < moves.length; i++) {
			switch (moves[i]) {
			case F: front(); break;
			case FI: frontInverted(); break;
			case SF: front(); front(); break;
			case D: down(); break;
			case DI: downInverted(); break;
			case SD: down(); down(); break;
			case R: rotate(); break;
			case RI: rotateInverted(); break;
			case SR: rotate(); rotate(); break;
			case N: LCD.drawString("N-Move parsed",0,0); break;
			}
		}
	}
	
	/**
	 * Print elements of cube-representation to std.out.
	 */
	protected void print() {
		for (int i = 0; i < 6; i++) {
			System.out.print(FacesEnum.values()[i] + ": ");
			for (int j = 0; j < 8; j++) {System.out.print(elements[i][j] + " ");}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	/**
	 * Print elements of cube-representation to EV3-Display.
	 */
	protected void printEV3() {
		for (int i = 0; i < 6; i++) {
			LCD.drawString(FacesEnum.values()[i] + ": ", 0, i);
			for (int j = 0; j < 8; j++) {
				LCD.drawString(elements[i][j] + " ", 3+j*2, i);
			}
		}
	}
	
	/**
	 * Returns true when cube-representation is in solved-state.
	 * @return boolean variable
	 */
	protected boolean isSolved() {
		int rotCounter = (rotations < 0) ? (rotations + 4) : rotations;
		ColorsEnum upColor = ColorsEnum.values()[FacesEnum.U.ordinal()];
		ColorsEnum downColor = ColorsEnum.values()[FacesEnum.D.ordinal()];
		ColorsEnum leftColor = ColorsEnum.values()[2 + (rotCounter % 4)];
		ColorsEnum frontColor = ColorsEnum.values()[2 + ((rotCounter + 1) % 4)];
		ColorsEnum rightColor = ColorsEnum.values()[2 + ((rotCounter + 2) % 4)];
		ColorsEnum backColor = ColorsEnum.values()[2 + ((rotCounter + 3) % 4)];
		LCD.drawString("" + upColor + downColor + leftColor + frontColor + rightColor + backColor, 0, 7);
		for (int i = 0; i < 8; i++) {
			if (elements[FacesEnum.U.ordinal()][i] != upColor) return false;
			else if (elements[FacesEnum.D.ordinal()][i] != downColor) return false;
			else if (elements[FacesEnum.L.ordinal()][i] != leftColor) return false;
			else if (elements[FacesEnum.F.ordinal()][i] != frontColor) return false;
			else if (elements[FacesEnum.R.ordinal()][i] != rightColor) return false;
			else if (elements[FacesEnum.B.ordinal()][i] != backColor) return false;
		}
		return true;
	}
	
	/**
	 * Checks if every color exists exactly 8 times in cube-representation.
	 * Used to detect scanning failures.
	 * @return Boolean variable, true if every color exists exactly 8 times.
	 */
	protected boolean completeIntegrity() {
		for (int i = 0; i < 6; i++) {
			int count = 0;
			for (int j = 0; j < 6; j++) {
				for (int k = 0; k < 8; k++) {
					if (elements[j][k] == null) return false;
					if (elements[j][k] == ColorsEnum.values()[i]) count++; 
				}	
			}
			if (count != 8) return false;
		}
		return true;
	}
	
	/**
	 * Returns current color of left face.
	 * @return ColorsEnum object
	 */
	protected ColorsEnum leftColor() {
		int rotCounter = (rotations < 0) ? (rotations + 4) : rotations;
		return ColorsEnum.values()[2 + (rotCounter % 4)];
	}
	
	/**
	 * Returns current color of front face.
	 * @return ColorsEnum object
	 */
	protected ColorsEnum frontColor() {
		int rotCounter = (rotations < 0) ? (rotations + 4) : rotations;
		return ColorsEnum.values()[2 + ((rotCounter + 1) % 4)];
	}
	
	/**
	 * Returns current color of right face.
	 * @return ColorsEnum object
	 */
	protected ColorsEnum rightColor() {
		int rotCounter = (rotations < 0) ? (rotations + 4) : rotations;
		return ColorsEnum.values()[2 + ((rotCounter + 2) % 4)];
	}
	
	/**
	 * Returns current color of back face.
	 * @return ColorsEnum object
	 */
	protected ColorsEnum backColor() {
		int rotCounter = (rotations < 0) ? (rotations + 4) : rotations;
		return ColorsEnum.values()[2 + ((rotCounter + 3) % 4)];
	}
	
	/**
	 * Returns color of up face (this is static).
	 * @return ColorsEnum.Y
	 */
	protected ColorsEnum upColor() {
		return ColorsEnum.Y;
	}
	
	/**
	 * Returns color of down face (this is static).
	 * @return ColorsEnum.W
	 */
	protected ColorsEnum downColor() {
		return ColorsEnum.W;
	}
	
	/**
	 * Returns true if front up edge is correctly oriented.
	 * @return boolean
	 */
	protected boolean ftEdgeOriented() {
		return (elements[FacesEnum.F.ordinal()][1] == frontColor() && elements[FacesEnum.U.ordinal()][5] == upColor());
	}
	
	/**
	 * Returns true if front right up corner is correctly oriented.
	 * @return boolean
	 */
	protected boolean frtCornerOriented() {
		return (elements[FacesEnum.F.ordinal()][2] == frontColor() && elements[FacesEnum.U.ordinal()][4] == upColor() && elements[FacesEnum.R.ordinal()][0] == rightColor());
	}
	
	/**
	 * Returns true if front left edge is correctly oriented.
	 * @return boolean
	 */
	protected boolean leftEdgeOriented() {
		return (elements[FacesEnum.F.ordinal()][7] == frontColor() && elements[FacesEnum.L.ordinal()][3] == leftColor());
	}
	
	/**
	 * Returns true if front right edge is correctly oriented.
	 * @return boolean
	 */
	protected boolean rightEdgeOriented() {
		return (elements[FacesEnum.F.ordinal()][3] == frontColor() && elements[FacesEnum.R.ordinal()][7] == rightColor());
	}
	
	/**
	 * Returns true if all edges of middle horizontal plane are correctly oriented.
	 * @return boolean
	 */
	protected boolean allEdgesOriented() {
		return (leftEdgeOriented() && rightEdgeOriented() &&
		(elements[FacesEnum.L.ordinal()][7] == leftColor() && elements[FacesEnum.B.ordinal()][3] == backColor()) &&
		(elements[FacesEnum.R.ordinal()][3] == rightColor() && elements[FacesEnum.B.ordinal()][7] == backColor()));
	}
	
	/**
	 * Removes redundancy from solution array.
	 * Two following moves are redundant if they don't change the cube-representation.
	 * Redundancy is found by attributes of AllMoves, that are defined in that class.
	 */
	protected void shortenSolution() {
		boolean shortened = true;
		while (shortened) {
			shortened = false;
			
			for (int i = 1; i < solutionIndex; i++) {
				
				if (i < solutionIndex-1) {
					if ((solution[i].getGroup() == 2) && (solution[i-1].getGroup() == 1) && (solution[i+1].getGroup() == 1)) {
						AllMoves dummy = solution[i];
						solution[i] = solution[i-1];
						solution[i-1] = dummy;
					}
					else if ((solution[i].getGroup() == 1) && (solution[i-1].getGroup() == 2) && (solution[i+1].getGroup() == 2)) {
						AllMoves dummy = solution[i];
						solution[i] = solution[i-1];
						solution[i-1] = dummy;
					}
				}
				
				if (solution[i].getGroup() == solution[i-1].getGroup()) {
					solution[i-1] = AllMoves.resultingMove(solution[i], solution[i-1]);
					
					for (int j = i; j < solutionIndex-1; j++) {
						solution[j] = solution[j+1];
					}
					
					solution[solutionIndex] = AllMoves.N;
					solutionIndex--;
					shortened = true;
					
					if (solution[i-1] == AllMoves.N) {
						for (int j = i-1; j < solutionIndex-1; j++) {
							solution[j] = solution[j+1];
						}
						solution[solutionIndex] = AllMoves.N;
						solutionIndex--;
					}
					
				}
			}
			
		}
	}

	public void solutionToFile(String filename){
		try {
			FileWriter solutionWriter = new FileWriter(filename);
			for (int i = 0; i < solutionIndex; i++) {
				solutionWriter.write(solution[i] + "\t");
			}
			solutionWriter.write("\n");
			for (int i = 0; i < solutionIndex; i++) {
				solutionWriter.write(MotorController.overshoot(solution, i, solutionIndex) + "\t");
			}
			solutionWriter.write("\n");
			solutionWriter.close();
		} catch (IOException e) {e.printStackTrace();}
	}

}
