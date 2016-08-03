package enums;

/**
 * Enumeration class for identifying moves that can be executed on Rubik's Cube.
 * Class contains information and method to reduce redundancy in solution array of RubiksCube class.
 */
public enum AllMoves {
	F(0,1), FI(0,-1), SF(0,2), D(1,1), DI(1,-1), SD(1,2), R(2,1), RI(2,-1), SR(2,2), N(-1,0);
	
	/**
	 * Group the move belongs to.
	 * There are three groups: 1. Rotations of front face; 2. Rotations of down face; 3. Rotations of whole cube; 
	 */
	private int group;
	
	/**
	 * Rotation value that is used to calculate the resulting move, if there are two move of the same group following each other in the solution array.
	 * 1 corresponds to positive 90 degrees.
	 */
	private int rotation;
	
	/**
	 * Constructor that initializes group- and rotation- value;
	 * @param i Group of the move
	 * @param rot Move's rotational value.
	 */
	AllMoves(int i, int rot) {
		this.group = i;
		this.rotation = rot;
	}
	
	/**
	 * Returns the group of current move. 
	 * @return Group
	 */
	public int getGroup() {return group;}
	
	/**
	 * Returns rotational value current move. 
	 * @return Rotational value
	 */
	public int getRotation() {return rotation;}
	
	/**
	 * Calculates the resulting move of two moves, that are in the same group and executed after another.
	 * @param move1 First move.
	 * @param move2 Second move.
	 * @return Resulting move.
	 */
	public static AllMoves resultingMove(AllMoves move1, AllMoves move2) {
		int totalRot = move1.getRotation() + move2.getRotation();
		switch (move1.getGroup()) {
		case 0: {
			if (totalRot == 0 || totalRot == 4) return AllMoves.N;
			else if (totalRot == 1) return AllMoves.F;
			else if (totalRot == 2 || totalRot == -2) return AllMoves.SF;
			else if (totalRot == 3) return AllMoves.FI;
		}
		case 1: {
			if (totalRot == 0 || totalRot == 4) return AllMoves.N;
			else if (totalRot == 1) return AllMoves.D;
			else if (totalRot == 2 || totalRot == -2) return AllMoves.SD;
			else if (totalRot == 3) return AllMoves.DI;
		}
		case 2: {
			if (totalRot == 0 || totalRot == 4) return AllMoves.N;
			else if (totalRot == 1) return AllMoves.R;
			else if (totalRot == 2 || totalRot == -2) return AllMoves.SR;
			else if (totalRot == 3) return AllMoves.RI;
		}
		default: return AllMoves.N;
		}
	}
	
	/**
	 * Returns the name of the move as String.
	 * @return Name of the move.
	 */
	public String toString() {
		switch (this) {
		case F: return "F";
		case FI: return "FI";
		case SF: return "SF";
		case D: return "D";
		case DI: return "DI";
		case SD: return "SD";
		case R: return "R";
		case RI: return "RI";
		case SR: return "SR";
		case N: return "N";
		default: return "N";
		}
	}
}
