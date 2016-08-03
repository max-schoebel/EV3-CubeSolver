package solver;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Stopwatch;
import enums.AllMoves;
import enums.ColorsEnum;
import enums.FacesEnum;

/**
 * This class contains the main program and all methods that solve the Rubik's Cube step by step.
 */
public class CubeAlgorithm {

	/**
	 * Searches through all edges of given cube-representation and returns the Edge object with the two specified colors.
	 * This is used to locate an edge (Cube object contains position information)
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @param color1 First color of searched edge.
	 * @param color2 Second color of searched edge.
	 * @return Edge object
	 */
	private Edge findEdge(RubiksCube cube, ColorsEnum color1, ColorsEnum color2) {
		Edge edge;
		for (int i = 0; i < 12; i++) {
			edge = cube.getEdge(i);
			if (edge.hasColor(color1) && edge.hasColor(color2)) {return edge;}
		}
		return new Edge(cube.elements, FacesEnum.R, 0, FacesEnum.R, 0);
	}

	/**
	 * Applies maneuver to cube-representation, after which given edge is correctly oriented on front-top-up position.	
	 * @param cube Internal representation of scanned Rubik's Cube
	 * @param edge edge to move
	 */
	private void moveEdge(RubiksCube cube, Edge edge) {
		if (!(edge.onFace(FacesEnum.U) && edge.onFace(FacesEnum.F) && cube.elements[0][5] == cube.upColor())) {
			
			if (edge.onFace(FacesEnum.F)) {
				if (edge.onFace(FacesEnum.U)) {
						AllMoves[] moves = {AllMoves.F, AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R, AllMoves.SF};
						cube.applyMoves(moves);
				}
				else if (edge.onFace(FacesEnum.L)) {
					if (cube.elements[2][3] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.F};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.R, AllMoves.F, AllMoves.D, AllMoves.FI, AllMoves.RI, AllMoves.SF};
						cube.applyMoves(moves);
					}
				}
				else if (edge.onFace(FacesEnum.R)) {
					if (cube.elements[4][7] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.FI};
						cube.applyMoves(moves);
					}
					else{
						AllMoves[] moves = {AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R, AllMoves.SF};
						cube.applyMoves(moves);
					}
				}
				else {
					if (cube.elements[1][1] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R, AllMoves.SF};
						cube.applyMoves(moves);
					}
				}
				if (!cube.ftEdgeOriented()) {
					System.out.println("Front");
				}
			}
			
			else if (edge.onFace(FacesEnum.D)) {
				if (edge.onFace(FacesEnum.L)) {
					if (cube.elements[1][7] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.D, AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {// further optimization: checking for correctness of top left edge (-3 turns)
						AllMoves[] moves = {AllMoves.R, AllMoves.FI, AllMoves.RI, AllMoves.F, AllMoves.R, AllMoves.F, AllMoves.RI};
						cube.applyMoves(moves);
					}
				}
				else if (edge.onFace(FacesEnum.R)) {
					if (cube.elements[1][3] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.DI, AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {// further optimization: checking for correctness of top right edge (-3 turns)
						AllMoves[] moves = {AllMoves.RI, AllMoves.F, AllMoves.R, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
				else if (edge.onFace(FacesEnum.B)) {
					if (cube.elements[1][5] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.SD, AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {// DI + same moves as on right side
						AllMoves[] moves = {AllMoves.DI, AllMoves.RI, AllMoves.F, AllMoves.R, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
			}
			
			else if (edge.onFace(FacesEnum.B)) {
				if (edge.onFace(FacesEnum.L)) {
					if (cube.elements[5][3] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.R, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.RI, AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {//further optimization: check for top edge (-3 turns)
						AllMoves[] moves = {AllMoves.R, AllMoves.SF, AllMoves.RI, AllMoves.F, AllMoves.R, AllMoves.SF, AllMoves.RI};
						cube.applyMoves(moves);
					}
				}
				else if (edge.onFace(FacesEnum.R)) {
					if (cube.elements[5][7] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.R, AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {//further optimization: check for top edge (-3 turns)
						AllMoves[] moves = {AllMoves.RI, AllMoves.SF, AllMoves.R, AllMoves.FI, AllMoves.RI, AllMoves.SF, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
				else if (edge.onFace(FacesEnum.U)) {
					if (cube.elements[0][1] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.SR, AllMoves.SF, AllMoves.SR, AllMoves.SD, AllMoves.SF};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.SR, AllMoves.FI, AllMoves.R, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.R, AllMoves.SF};
						cube.applyMoves(moves);
					}
				}
			}
			
			else if (edge.onFace(FacesEnum.U)) {
				if (edge.onFace(FacesEnum.L)) {
					if (cube.elements[2][1] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.R, AllMoves.F, AllMoves.RI, AllMoves.F};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.R, AllMoves.SF, AllMoves.D, AllMoves.RI, AllMoves.SF};
						cube.applyMoves(moves);
					}
				}
				else if (edge.onFace(FacesEnum.R)) {
					if (cube.elements[4][1] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.FI, AllMoves.R, AllMoves.FI};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.RI, AllMoves.SF, AllMoves.DI, AllMoves.R, AllMoves.SF};
						cube.applyMoves(moves);
					}
				}
			}
		}
	}
	
	/**
	 * Locates the four edges of first level (top layer of the Rubik's Cube) and moves them correctly oriented onto their positions.
	 * After this, the yellow cross could be seen on top face.
	 * @param cube Internal representation of scanned Rubik's Cube
	 */
	private void firstLevelEdges(RubiksCube cube) {
		Edge edge;
		for (int i = 0; i < 4; i++) {
			edge = findEdge(cube, cube.upColor(), cube.frontColor());
			moveEdge(cube, edge);
			cube.rotate();
		}
	}
	
	/**
	 * Searches through all corners of given cube-representation and returns the Corner object with the three specified colors. 
	 * This is used to locate a Corner (Corner object contains position information)
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @param color1 First color of searched corner.
	 * @param color2 Second color of searched corner.
	 * @param color3 Third color of searched corner.
	 * @return Corner object
	 */
	private Corner findCorner(RubiksCube cube, ColorsEnum color1, ColorsEnum color2, ColorsEnum color3) {
		Corner corner;
		for (int i = 0; i < 8; i++) {
			corner = cube.getCorner(i);
			if (corner.hasColor(color1) && corner.hasColor(color2) && corner.hasColor(color3)) {return corner;}
		}
		return new Corner(cube.elements, FacesEnum.R, 0, FacesEnum.R, 0, FacesEnum.R, 0);
	}
	
	/**
	 * Applies maneuver to cube-representation, after which given corner is correctly oriented on front-right-up position.
	 * @param cube Internal representation of scanned Rubik's Cube
	 * @param corner corner to move
	 */
	private void moveCorner(RubiksCube cube, Corner corner) {
		if (!(corner.onFace(FacesEnum.F) && corner.onFace(FacesEnum.R) && corner.onFace(FacesEnum.U) && cube.elements[0][4] == cube.upColor())) {
			
			if (corner.onFace(FacesEnum.F)) {
				if (corner.onFace(FacesEnum.R) && corner.onFace(FacesEnum.U)) {
					
					if (cube.elements[4][0] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R, AllMoves.F, AllMoves.SD, AllMoves.FI};
						cube.applyMoves(moves);	
					}
					else {
						AllMoves[] moves = {AllMoves.F, AllMoves.SD, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
				
				else if (corner.onFace(FacesEnum.L) && corner.onFace(FacesEnum.U)) {
					if (cube.elements[0][6] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.R, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.SR, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[3][0] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.SD, AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.R, AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.RI};
						cube.applyMoves(moves);
					}
				}
				else if (corner.onFace(FacesEnum.D) && corner.onFace(FacesEnum.L)) {
					if (cube.elements[3][6] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.D, AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[2][4] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[1][0] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.D, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}					
				}
				else if (corner.onFace(FacesEnum.D) && corner.onFace(FacesEnum.R)) {
					if (cube.elements[3][4] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.DI, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[4][6] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.D, AllMoves.F, AllMoves.DI, AllMoves.FI};
						cube.applyMoves(moves);
					}
					else /*if (cube.elements[1][2] == cube.upColor())*/ {
						AllMoves[] moves = {AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
			}
			
			else if (corner.onFace(FacesEnum.B)) {
				if (corner.onFace(FacesEnum.L) && corner.onFace(FacesEnum.U)) {
					if (cube.elements[0][0] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.R, AllMoves.FI, AllMoves.SR, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.SR, AllMoves.F, AllMoves.RI};
						cube.applyMoves(moves);
					}
					else if (cube.elements[2][0] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.R, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else /*if (cube.elements[5][2] == cube.upColor())*/ {
						AllMoves[] moves = {AllMoves.SR, AllMoves.F, AllMoves.D, AllMoves.FI, AllMoves.R, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
				else if (corner.onFace(FacesEnum.R) && corner.onFace(FacesEnum.U)) {
					if (cube.elements[0][2] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.SR, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[4][2] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.F, AllMoves.SD, AllMoves.SF, AllMoves.D, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else { 
						AllMoves[] moves = {AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.SR, AllMoves.FI};
						cube.applyMoves(moves);
					}
				}
				else if (corner.onFace(FacesEnum.L) && corner.onFace(FacesEnum.D)) {
					if (cube.elements[2][6] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.SD, AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[5][4] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else {
						AllMoves[] moves = {AllMoves.SD, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
				else if (corner.onFace(FacesEnum.R) && corner.onFace(FacesEnum.D)){
					if (cube.elements[5][6] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.DI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else if (cube.elements[4][4] == cube.upColor()) {
						AllMoves[] moves = {AllMoves.RI, AllMoves.D, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
					else /*if (cube.elements[1][4] == cube.upColor())*/ {
						AllMoves[] moves = {AllMoves.DI, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.RI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.R};
						cube.applyMoves(moves);
					}
				}
			}
		}
	}
		
	/**
	 * Locates the four corners of first level (top layer of the Rubik's Cube) and moves them correctly oriented onto their positions.
	 * After this, complete first level of cube is correctly colored.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 */
	private void firstLevelCorners(RubiksCube cube) {
		Corner corner;
		for (int i = 0; i < 4; i++) {
			corner = findCorner(cube, cube.upColor(), cube.frontColor(), cube.rightColor());
			moveCorner(cube, corner);
			cube.rotate();
		}
	}
	
	//2L edges
	/**
	 * Checks if edge that needs to be moved to second layer is correctly oriented so that it can be moved.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @param color Second color of edge (first color is front color).
	 * @return Boolean 
	 */
	private boolean downEdgeOriented(RubiksCube cube, ColorsEnum color) {
		for (int i = 2; i <= 5; i++) {
			if (cube.elements[i][5] == cube.frontColor()) {
				 if (i == 2 && cube.elements[1][7] == color) return true;
				 else if (i == 3 && cube.elements[1][1] == color) return true;
				 else if (i == 4 && cube.elements[1][3] == color) return true;
				 else if (i == 5 && cube.elements[1][5] == color) return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if there are edges in third layer that need to be moved to second layer.
	 * Otherwise all edges of second layer are already in second layer (but possibly not correctly oriented)
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @return Boolean
	 */
	private boolean edgesInDownLayer(RubiksCube cube) {
		for (int i = 2; i <= 5; i++) {
			if (cube.elements[i][5] != cube.downColor()) {
				 if (i == 2 && cube.elements[1][7] != cube.downColor()) return true;
				 else if (i == 3 && cube.elements[1][1] != cube.downColor()) return true;
				 else if (i == 4 && cube.elements[1][3] != cube.downColor()) return true;
				 else if (i == 5 && cube.elements[1][5] != cube.downColor()) return true;
			}
		}
		return false;
	}
	
	/**
	 * Moves edges of second layer onto their correct positions.
	 * After this the first two layers are correctly colored, because middle elements in second layer are static.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 */
	private void secondLevelEdges(RubiksCube cube) {
		AllMoves[] toLeft = {AllMoves.D, AllMoves.R, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.RI, AllMoves.DI, AllMoves.FI, AllMoves.D, AllMoves.F};
		AllMoves[] toRight = {AllMoves.DI, AllMoves.RI, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.R, AllMoves.D, AllMoves.F, AllMoves.DI, AllMoves.FI};
		
		while (!cube.allEdgesOriented()) {
			while (edgesInDownLayer(cube)) {
				if (!cube.leftEdgeOriented()) {
					Edge edge = findEdge(cube, cube.frontColor(), cube.leftColor());
					if (edge.onFace(FacesEnum.D) && downEdgeOriented(cube, cube.leftColor())) {
						while (!(cube.elements[FacesEnum.F.ordinal()][5] == cube.frontColor() && cube.elements[FacesEnum.D.ordinal()][1] == cube.leftColor())) {cube.down();}
						cube.applyMoves(toLeft);
					}
				}
				if (!cube.rightEdgeOriented()) {
					Edge edge = findEdge(cube, cube.frontColor(), cube.rightColor());
					if (edge.onFace(FacesEnum.D) && downEdgeOriented(cube, cube.rightColor())) {
						while (!(cube.elements[FacesEnum.F.ordinal()][5] == cube.frontColor() && cube.elements[1][1] == cube.rightColor())) {cube.down();}
						cube.applyMoves(toRight);
					}
				}
				cube.rotate();
			} 
			
			boolean mooved = false;
			while (!mooved && !cube.allEdgesOriented()) {
				if (!cube.leftEdgeOriented()) {
					cube.applyMoves(toLeft);
					mooved = true;
				}
				else if (!cube.rightEdgeOriented()) {
					cube.applyMoves(toRight);
					mooved = true;
				}
				else cube.rotate();
			}
		}
	}
	

	//LL: orient edges
	/**
	 * Counts how many edges of last layer are correctly oriented (they don't need to be on the correct position yet).
	 * Correct orientation is given, when color of edge on down face is white.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @return Integer (0...4)
	 */
	private int llEdgeCount(RubiksCube cube) {
		int count = 0;
		for (int i = 1; i <= 7; i += 2) {
			if (cube.elements[FacesEnum.D.ordinal()][i] == ColorsEnum.W) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Orients edges on last layer. Calls llEdgeCount to do so.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 */
	private void orientLLEdges(RubiksCube cube) {
		AllMoves[] maneuver1 = {AllMoves.F, AllMoves.D, AllMoves.R, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.RI, AllMoves.FI};
		AllMoves[] maneuver2 = {AllMoves.F, AllMoves.R, AllMoves.F, AllMoves.D, AllMoves.FI, AllMoves.RI, AllMoves.DI, AllMoves.FI};		
		
		int count;
		while ((count = llEdgeCount(cube)) != 4) {
			if (count == 0) {
				cube.applyMoves(maneuver1);
			}
			else if (cube.elements[FacesEnum.D.ordinal()][3] == ColorsEnum.W && cube.elements[FacesEnum.D.ordinal()][5] == ColorsEnum.W) {
				cube.applyMoves(maneuver1);
			}
			else if (cube.elements[FacesEnum.D.ordinal()][3] == ColorsEnum.W && cube.elements[FacesEnum.D.ordinal()][7] == ColorsEnum.W) {
				cube.applyMoves(maneuver2);
			}
			else cube.rotate();
		}
	}
	
	/**
	 * Checks if corners of last layer are on their correct positions (they don't need to be oriented correctly yet).
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @return Boolean true, if corners of last layer are correctly placed.
	 */
	private boolean llCornersPlaced(RubiksCube cube) {
		Corner corner1 = findCorner(cube, cube.frontColor(), cube.leftColor(), cube.downColor());
		Corner corner2 = findCorner(cube, cube.frontColor(), cube.rightColor(), cube.downColor());
		Corner corner3 = findCorner(cube, cube.backColor(), cube.leftColor(), cube.downColor());
		Corner corner4 = findCorner(cube, cube.backColor(), cube.rightColor(), cube.downColor());	
		return corner1.onFace(FacesEnum.F) && corner1.onFace(FacesEnum.L) && corner2.onFace(FacesEnum.F) && corner2.onFace(FacesEnum.R) && corner3.onFace(FacesEnum.B) && corner3.onFace(FacesEnum.L) && corner4.onFace(FacesEnum.B) && corner4.onFace(FacesEnum.R);
		
	}

	/**
	 * Permutes corners on last layer until all corners are correctly positioned.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 */
	private void permuteLLCorners(RubiksCube cube) {
		AllMoves[] cornerSwap = {AllMoves.R, AllMoves.F, AllMoves.DI, AllMoves.SR, AllMoves.FI, AllMoves.D, AllMoves.SR, AllMoves.FI, AllMoves.DI, AllMoves.SR, AllMoves.F, AllMoves.R, AllMoves.SD};
		
		Corner corner, cornerLeft;
		while(!(llCornersPlaced(cube))) {
			corner = findCorner(cube, cube.frontColor(), cube.rightColor(), cube.downColor());
			cornerLeft = findCorner(cube, cube.frontColor(), cube.leftColor(), cube.downColor());
			if (corner.onFace(FacesEnum.F) && corner.onFace(FacesEnum.L)) {
				AllMoves[] move = {AllMoves.RI};
				cube.rotate();
				cube.applyMoves(cornerSwap);
				cube.applyMoves(move);
			}
			else if (corner.onFace(FacesEnum.B) && corner.onFace(FacesEnum.R)) {
				cube.applyMoves(cornerSwap);
			}
			else if ((corner.onFace(FacesEnum.B) && corner.onFace(FacesEnum.L)) && !(cornerLeft.onFace(FacesEnum.F) && cornerLeft.onFace(FacesEnum.R))) {
				AllMoves[] move = {AllMoves.RI};
				cube.applyMoves(move);
				cube.applyMoves(cornerSwap);
				cube.rotate();
				cube.applyMoves(cornerSwap);
			}
			AllMoves[] move = {AllMoves.RI};
			cube.applyMoves(move);
		}
	}
	
//	working:
//	private void permuteLLCorners(RubiksCube cube) {
//		AllMoves[] cornerSwap = {AllMoves.R, AllMoves.F, AllMoves.DI, AllMoves.SR, AllMoves.FI, AllMoves.D, AllMoves.SR, AllMoves.FI, AllMoves.DI, AllMoves.SR, AllMoves.F, AllMoves.R, AllMoves.SD};
//		
//		Corner corner;
//		for (int i = 0; i < 3; i++) {
//			corner = findCorner(cube, cube.frontColor(), cube.rightColor(), cube.downColor());
//			if (corner.onFace(FacesEnum.F) && corner.onFace(FacesEnum.L)) {
//				AllMoves[] move = {AllMoves.RI};
//				cube.rotate();
//				cube.applyMoves(cornerSwap);
//				cube.applyMoves(move);
//			}
//			else if (corner.onFace(FacesEnum.B) && corner.onFace(FacesEnum.R)) {
//				cube.applyMoves(cornerSwap);
//			}
//			else if (corner.onFace(FacesEnum.B) && corner.onFace(FacesEnum.L)) {
//				AllMoves[] move = {AllMoves.RI};
//				cube.applyMoves(move);
//				cube.applyMoves(cornerSwap);
//				cube.rotate();
//				cube.applyMoves(cornerSwap);
//			}
//			AllMoves[] move = {AllMoves.RI};
//			cube.applyMoves(move);
//		}
//	}
	
	//LL: orient corners
	/**
	 * Counts how many corners of last layer are incorrectly oriented.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @return Number of incorrectly oriented corners.
	 */
	private int llCornerCount(RubiksCube cube) {
		int count = 0;
		for (int i = 0; i <= 6; i += 2) {
			if (cube.elements[FacesEnum.D.ordinal()][i] != ColorsEnum.W) count++;
		}
		return count;
	}
	
	/**
	 * Orients (rotates) corners of last layer.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 */
	private void orientLLCorners(RubiksCube cube) {
		AllMoves[] maneuver1 = {AllMoves.RI, AllMoves.FI, AllMoves.DI, AllMoves.F, AllMoves.DI, AllMoves.FI, AllMoves.SD, AllMoves.F, AllMoves.SD, AllMoves.R};
		AllMoves[] maneuver2 = {AllMoves.RI, AllMoves.F, AllMoves.D, AllMoves.FI, AllMoves.D, AllMoves.F, AllMoves.SD, AllMoves.FI, AllMoves.SD, AllMoves.R};
		
		int count;
		while ((count = llCornerCount(cube)) != 0) {
			if (count == 4) {
				if (cube.elements[FacesEnum.L.ordinal()][4] == ColorsEnum.W && cube.elements[FacesEnum.L.ordinal()][6] == ColorsEnum.W &&
					cube.elements[FacesEnum.R.ordinal()][4] == ColorsEnum.W && cube.elements[FacesEnum.R.ordinal()][6] == ColorsEnum.W) {
					cube.applyMoves(maneuver1);
				}
				else if (cube.elements[FacesEnum.L.ordinal()][4] == ColorsEnum.W && cube.elements[FacesEnum.L.ordinal()][6] == ColorsEnum.W &&
						cube.elements[FacesEnum.F.ordinal()][4] == ColorsEnum.W && cube.elements[FacesEnum.B.ordinal()][6] == ColorsEnum.W) {
					cube.applyMoves(maneuver1);
				}
				else cube.rotate();
			}
			if (count == 2) {
				if (cube.elements[FacesEnum.F.ordinal()][6] == ColorsEnum.W && cube.elements[FacesEnum.R.ordinal()][4] == ColorsEnum.W) {
					cube.applyMoves(maneuver1);
				}
				else if (cube.elements[FacesEnum.F.ordinal()][6] == ColorsEnum.W && cube.elements[FacesEnum.B.ordinal()][4] == ColorsEnum.W) {
					cube.applyMoves(maneuver1);
				}
				else if (cube.elements[FacesEnum.B.ordinal()][4] == ColorsEnum.W && cube.elements[FacesEnum.B.ordinal()][6] == ColorsEnum.W) {
					cube.applyMoves(maneuver2);
				}
				else cube.rotate();
			}
			if (count == 3) {
				if (cube.elements[FacesEnum.F.ordinal()][4] == ColorsEnum.W && cube.elements[FacesEnum.R.ordinal()][4] == ColorsEnum.W &&
					cube.elements[FacesEnum.B.ordinal()][4] == ColorsEnum.W) {
					cube.applyMoves(maneuver1);
				}
				else if (cube.elements[FacesEnum.F.ordinal()][6] == ColorsEnum.W && cube.elements[FacesEnum.R.ordinal()][6] == ColorsEnum.W &&
					cube.elements[FacesEnum.B.ordinal()][6] == ColorsEnum.W) {
					cube.applyMoves(maneuver2);
				}
				else cube.rotate();	
			}
		}
	}
		
	//LL: permute edges
	/**
	 * Counts how many edges are correctly positioned.
	 * @param cube Internal representation of scanned Rubik's Cube.
	 * @return Number of correctly positioned edges on last layer.
	 */
	private int llEdgeCount2(RubiksCube cube) {
		int count = 0;
		if (cube.elements[2][5] == cube.leftColor()) {count++;}
		if (cube.elements[3][5] == cube.frontColor()) {count++;}
		if (cube.elements[4][5] == cube.rightColor()) {count++;}
		if (cube.elements[5][5] == cube.backColor()) {count++;}
		return count;
	}
	
	/**
	 * Permutes edges of last layer until all elements on last layer are correctly colored.
	 * After this cube should be solved (unless cube was manually altered to unsolvable state).
	 * @param cube Internal representation of scanned Rubik's Cube.
	 */
	private void permuteLLedges(RubiksCube cube) {
		AllMoves[] maneuver1 = {AllMoves.RI, AllMoves.SF, AllMoves.RI, AllMoves.D, AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.RI, AllMoves.SF, AllMoves.RI, AllMoves.FI, AllMoves.SR, AllMoves.F, AllMoves.D, AllMoves.RI, AllMoves.SF, AllMoves.R};
		AllMoves[] maneuver2 = {AllMoves.RI, AllMoves.SF, AllMoves.RI, AllMoves.DI, AllMoves.F, AllMoves.SR, AllMoves.FI, AllMoves.RI, AllMoves.SF, AllMoves.RI, AllMoves.FI, AllMoves.SR, AllMoves.F, AllMoves.DI, AllMoves.RI, AllMoves.SF, AllMoves.R};
		
		int count = 0;
		while ((count = llEdgeCount2(cube)) < 4) {
//			LCD.drawString("Count: "+count, 7, 7);
//			Button.ENTER.waitForPress();
			if (count == 0) {
				if ((cube.elements[3][5] == cube.rightColor()) || (cube.elements[4][5] == cube.backColor()) || (cube.elements[5][5] == cube.frontColor())) {cube.applyMoves(maneuver1);}
				else if ((cube.elements[5][5] == cube.rightColor()) || (cube.elements[3][5] == cube.backColor()) || (cube.elements[4][5] == cube.frontColor())) {cube.applyMoves(maneuver2);}
				else cube.rotate();
			}
			else if (count == 2) {
				LCD.clear();
				LCD.drawString("Unsolvable Scramble", 0, 0);
				LCD.drawString("Exit on ENTER", 0, 1);
				Button.ENTER.waitForPress();
				System.exit(1);
			}
			else{
				while (cube.elements[2][5] != cube.leftColor()) {cube.rotate();}
				if (cube.elements[5][5] == cube.frontColor()) {
					cube.applyMoves(maneuver1);
				}
				else cube.applyMoves(maneuver2);
			}
		}
	}
	
	/**
	 * Shows main menu and returns number of selected entry.
	 * Entries are selected with UP- and DOWN-Button. Selected entry is highlighted.
	 * After pressing ENTER, number of selected menu entry is returned.
	 * @return Number of selected menu entry.
	 */
	public int getMenuChoice() {
		int choice = 0;
		int button;
		LCD.clear();
		do {
			LCD.drawString("Scan and solve", 0, 0, (choice == 0));
			LCD.drawString("Calibrate motors", 0, 1, (choice == 1));
			LCD.drawString("Init motors", 0, 2, (choice == 2));
			LCD.drawString("Exit program", 0, 3, (choice == 3));
			
			button = Button.waitForAnyPress();
			if (button == Button.ID_UP) {
				choice = (choice == 0) ? 3 : (choice - 1);
			}
			else if (button == Button.ID_DOWN) {
				choice = (choice + 1) % 4;
			}
		} while (button != Button.ID_ENTER);
		LCD.clear();
		return choice;
	}
	
	/**
	 * Main-method, that represent the Roberta-EV3CubeSolvers main program.
	 * In the beginning motors are initialized, solution is searched and applied. Times are measured.
	 * For furher information of program flow see RobertaEV3-CubeSolver Manual.
	 * @param args
	 */
	public static void main (String[] args) {
		int scanTime, searchTime, applyTime;
		int button;
		
		boolean interrupted = false;
		
		MotorController motors = new MotorController();
		CubeScanner scanner = new CubeScanner(motors);
		RubiksCube cube = new RubiksCube();
		CubeAlgorithm solver = new CubeAlgorithm();
		Stopwatch timer = new Stopwatch();
		motors.init();
		
		outer: while (!interrupted) {
			
			switch (solver.getMenuChoice()) {
			
			case 0: timer.reset();
					cube.resetNull();
					scanner.scanCube(cube);
					while (cube.completeIntegrity() == false) {
						LCD.clear();
						LCD.drawString("Scan Failure", 0, 0);
						LCD.drawString("ENTER: Rescan", 0, 6);
						LCD.drawString("ESCAPE: Menu", 0, 7);
						do {
							button = Button.waitForAnyPress();
						} while (!(button == Button.ID_ENTER || button == Button.ID_ESCAPE));
						if (button == Button.ID_ESCAPE) {motors.rotate(); continue outer;}
						timer.reset();
						motors.rotate();
						cube.resetNull();
						scanner.scanCube(cube);
					}
					scanTime = timer.elapsed();	
					
					cube.printEV3();
					LCD.drawString("ENTER: Search", 0, 6);
					LCD.drawString("ESCAPE: Menu", 0, 7);
					do {
						button = Button.waitForAnyPress();
					} while (!(button == Button.ID_ENTER || button == Button.ID_ESCAPE));
					if (button == Button.ID_ESCAPE) {motors.rotateInverted(); continue outer;}
					
					cube.setRecording(true);
					timer.reset();
					solver.firstLevelEdges(cube);
					cube.shortenSolution();
					solver.firstLevelCorners(cube);
					cube.shortenSolution();
					solver.secondLevelEdges(cube);
					cube.shortenSolution();
					solver.orientLLEdges(cube);
					cube.shortenSolution();
					solver.permuteLLCorners(cube);
					cube.shortenSolution();
					solver.orientLLCorners(cube);
					cube.shortenSolution();
					solver.permuteLLedges(cube);
					cube.shortenSolution();
					searchTime = timer.elapsed();
							
					LCD.clear();
					LCD.drawString("Solution found", 0, 0);
					LCD.drawString(""+cube.solutionIndex, 0, 1);
					LCD.drawString("Apply?", 0, 2);
					
					//debug
					cube.solutionToFile("overshoots");
					
					Button.ENTER.waitForPress();
					
					timer.reset();
					motors.applyMoves(cube.solution, cube.solutionIndex, false);
					applyTime = timer.elapsed();
					
					LCD.clear();
					LCD.drawString("Scan: "+ scanTime/1000.0F +" s", 0, 0);
					LCD.drawString("Search: "+ searchTime/1000.0F +" s", 0, 1);
					LCD.drawString("Apply: "+ applyTime/1000.0F +" s", 0, 2);
					LCD.drawString(cube.solutionIndex/(applyTime/1000.0F) + " Moves/s", 0, 3);
					
					LCD.drawString("ENTER: Menu", 0, 7);
					Button.ENTER.waitForPress(); continue;
			case 1: motors.calibrate(); continue;
			case 2:	motors.init(); continue;
			case 3:	interrupted = true; continue;
			}
		}
	}
	
//	/* alternate main-method used for algorithm evaluation */
//	public static void main (String[] args) {
//		
//		RubiksCube cube = new RubiksCube();
//		CubeAlgorithm solver = new CubeAlgorithm();
//
//		int sum, sum1, sum2, sum3, sum4, sum5, sum6, sum7;
//		sum = 0; sum1 = 0; sum2 = 0; sum3 = 0; sum4 = 0; sum5 = 0; sum6 = 0; sum7 = 0;
//		int a,b,c,d,e,f,g,k;
//	
//		for (k = 0; k < 20000; k++) {
//			
//			cube.resetScrambled(5000);
//			cube.setRecording(true);
//	
//			solver.firstLevelEdges(cube);
//			cube.shortenSolution();
//			a = cube.solutionIndex;
//			
//			solver.firstLevelCorners(cube);
//			cube.shortenSolution();
//			b = cube.solutionIndex;			
//			
//			solver.secondLevelEdges(cube);
//			cube.shortenSolution();
//			c = cube.solutionIndex;
//			
//			solver.orientLLEdges(cube);
//			cube.shortenSolution();
//			d = cube.solutionIndex;
//			
//			solver.permuteLLCorners(cube);
//			cube.shortenSolution();
//			e = cube.solutionIndex;
//			
//			solver.orientLLCorners(cube);
//			cube.shortenSolution();
//			f = cube.solutionIndex;
//			
//			solver.permuteLLedges(cube);
//			cube.shortenSolution();
//			g = cube.solutionIndex;
//			
//			System.out.println(k+". "+cube.solutionIndex+": "+a+" "+(b-a)+" "+(c-b)+" "+(d-c)+" "+(e-d)+" "+(f-e)+" "+(g-f));
//
//			sum += cube.solutionIndex; sum1 += a; sum2 += (b-a); sum3 += (c-b); sum4 += (d-c); sum5 += (e-d); sum6 += (f-e); sum7 += (g-f);
//		}
//		System.out.println("Average "+sum/k+": "+sum1/k+" "+sum2/k+" "+sum3/k+" "+sum4/k+" "+sum5/k+" "+sum6/k+" "+sum7/k);
//	}
}
