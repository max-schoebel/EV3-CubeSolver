package solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import enums.AllMoves;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

/**
 * This class contains methods to control Roberta-EV3CubeSolver's Motors.
 * Execution of moves on the physical Rubik's Cub is implemented here. 
 */
class MotorController {

	/**
	 * Large motor, that rotates the table.
	 * The table is the part of the CubeSolver where the cube rests in.
	 */
	private final EV3LargeRegulatedMotor tableMotor = new EV3LargeRegulatedMotor(MotorPort.A);
	
	/**
	 * Large motor, that rotates the fork.
	 * The fork is used to rotate a face of the cube by 90 degrees.
	 */
	private final EV3LargeRegulatedMotor forkMotor = new EV3LargeRegulatedMotor(MotorPort.B);

	/**
	 * Medium motor, that moves the fork back and forth.
	 */
	private final EV3MediumRegulatedMotor moverMotor = new EV3MediumRegulatedMotor(MotorPort.C);
	
	/**
	 * forkMover needs to rotate by this angle to push fork against cube. 
	 */
	private final int FORKMOVERANGLE = 85;
	
	/**
	 * Speed of forkMotor.
	 */
	private final int FORKSPEED = 800;
	
	/**
	 * Speed of tableMotor.
	 */
	private final int TABLESPEED = 800;
	
	/**
	 * Speed of moverMotor.
	 */
	private final int MOVERSPEED = 800;
	
	/**
	 * Additional angle to prevent mechanical movement failure. Not used at the moment.
	 */
	private final static int OVERSHOOT = 2;
	
	/**
	 * Angle from fork's idle position to finish clockwise rotation of cube's front face.
	 * This value is read from forkcalibration-file upon startup.
	 */
	private int frontTurnAngleRight = 25; 
	
	/**
	 * Angle from fork's idle position to finish counter clockwise rotation of cube's front face.
	 * This value is read from forkcalibration-file upon startup.
	 */
	private int frontTurnAngleLeft = 25; //Angle from idle position to finish Cube Rotation
	
	/**
	 * Correction that needs to be added (or subtracted) to execute down()-move precisely.
	 * This value is read from tablecalibration-file upon startup.
	 */
	private int downCorrection = 0;
	
	/**
	 * Correction that needs to be added (or subtracted) to execute downInverted()-move precisely.
	 * This value is read from tablecalibration-file upon startup.
	 */
	private int downInvertedCorrection = 0;

	/**
	 * Correction that needs to be added (or subtracted) to execute switchDown()-move precisely.
	 * This value is read from tablecalibration-file upon startup.
	 */
	private int switchDownCorrection = 0;
	
	/**
	 * Fork's idle position.
	 * This value is written during initialization of Roberta-EV3CubeSolver's motors.
	 */
	private int forkIdleAngle = 0;
	
	/**
	 * Current desired position of tableMotor.
	 * 
	 */
	private int tableAngle = 0;
	
	/**
	 * tableMotor's angle before scanning rotation is executed.
	 * Used to return relative table angle while scanning of top face.
	 */
	private int startScanningAngle = 0;

	/**
	 * Indicates fork position.
	 * True equals closed fork (fork is pushed towards Rubik's cube). False equals opened fork (fork is pulled from Rubik's cube).
	 */
	private boolean closed = false;

	/**
	 * Initializes CubeSolver's motors.
	 * Idle- and starting positions are searched by rotating to physical end positions.
	 * Calibration angles are read from configuration files. If files don't exist, calibration is executed manually.
	 */
	protected void init() {
		LCD.drawString("Lock Table", 0, 0);
		LCD.drawString("Pull back Fork", 0, 1);
		LCD.drawString("ENTER: Continue", 0, 6);
		Button.ENTER.waitForPress();
		LCD.clear();
		initFork();
		initForkMover();
		initTable();
		LCD.clear();
		LCD.drawString("Unlock Table", 0, 0);
		LCD.drawString("ENTER: Continue", 0, 6);
		Button.ENTER.waitForPress();
		LCD.clear();
		try {
			BufferedReader fconfigFile = new BufferedReader(new FileReader("forkcalibration"));
			frontTurnAngleRight = Integer.parseInt(fconfigFile.readLine());
			frontTurnAngleLeft = Integer.parseInt(fconfigFile.readLine());
			fconfigFile.close();
			LCD.drawString("forkcalibration", 0, 0);
			LCD.drawString("read", 0, 1);
			Delay.msDelay(1000);
			LCD.clear();
		}
		catch (IOException e) {
			LCD.clear();
			LCD.drawString("Could not read", 0, 0);
			LCD.drawString("forkcalibration", 0, 1);
			LCD.drawString("Calibr. manually:", 0, 2);		
			this.calibrateFork();
		}
		try {
			BufferedReader tconfigFile = new BufferedReader(new FileReader("tablecalibration"));
			downCorrection = Integer.parseInt(tconfigFile.readLine());
			downInvertedCorrection = Integer.parseInt(tconfigFile.readLine());
			switchDownCorrection = Integer.parseInt(tconfigFile.readLine());
			tconfigFile.close();
			LCD.drawString("tablecalibration", 0, 0);
			LCD.drawString("read", 0, 1);
			Delay.msDelay(1000);
			LCD.clear();
		}
		catch (IOException e) {
			LCD.clear();
			LCD.drawString("Could not read", 0, 0);
			LCD.drawString("tablecalibration", 0, 1);
			LCD.drawString("Calibr. manually:", 0, 2);
			this.calibrateTable();
		}
	}
	
	/**
	 * Manual calibration method.
	 * Calls calibration of table and fork.
	 */
	protected void calibrate() {
		LCD.drawString("Calibration", 0, 0);
		this.calibrateFork();
		this.calibrateTable();
	}
	
	/**
	 * Initialization of forkMotor.
	 * @see init()
	 */
	private void initFork() {
		int forkLeftAngle = 0;
		int forkRightAngle = 0;
		
		forkMotor.setSpeed(20);
		forkMotor.setStallThreshold(10,1);
		
		forkMotor.backward();
		forkMotor.waitComplete();
		forkLeftAngle = forkMotor.getTachoCount();
		forkMotor.forward();
		forkMotor.waitComplete();
		forkRightAngle = forkMotor.getTachoCount();
		
		forkIdleAngle = (forkLeftAngle + forkRightAngle) / 2;
		forkMotor.setStallThreshold(25,5);
		forkMotor.setSpeed(FORKSPEED);
		forkMotor.rotateTo(forkIdleAngle);
	}
	
	/**
	 * Manual calibration of forkMotor.
	 */
	private void calibrateFork() {
		int button;
		LCD.drawString("Front", 0, 3);
		LCD.drawString("ENTER: Front", 0, 5);
		LCD.drawString("UP: More", 0, 6);
		LCD.drawString("DOWN: Less", 0, 7);
		while ((button = Button.waitForAnyPress()) != Button.ID_ESCAPE) {
			if (button == Button.ID_UP) {
				frontTurnAngleRight += 1;
			}
			else if (button == Button.ID_DOWN) {
				frontTurnAngleRight -= 1;
			}
			else if (button == Button.ID_ENTER) {
				this.front(0);
			}
		}
		LCD.drawString("FrontInverted", 0, 3);
		LCD.drawString("ENTER: FrontInverted", 0, 5);
		while ((button = Button.waitForAnyPress()) != Button.ID_ESCAPE) {
			if (button == Button.ID_UP) {
				frontTurnAngleLeft += 1;
			}
			else if (button == Button.ID_DOWN) {
				frontTurnAngleLeft -= 1;
			}
			else if (button == Button.ID_ENTER) {
				this.frontInverted(0);
			}
		}
		try {
			FileWriter fconfigFile = new FileWriter("forkcalibration");
			fconfigFile.write(frontTurnAngleRight + "\n");
			fconfigFile.write(frontTurnAngleLeft + "\n");
			fconfigFile.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		LCD.clear();
	}

	/**
	 * Initialization of moverMotor.
	 * Just sets motor's speed. Starting angle is set manually.
	 */
	private void initForkMover() {
		moverMotor.setSpeed(MOVERSPEED);
	}
	
	/**
	 * Initialization of tableMotor.
	 * Table needs to be locked so that exact starting position can be found by rotating to the left and right.
	 */
	private void initTable() {
		int tableLeftAngle = 0;
		int tableRightAngle = 0;
		
		tableMotor.setSpeed(20);
		tableMotor.setStallThreshold(10,1);
		
		tableMotor.backward();
		tableMotor.waitComplete();
		tableLeftAngle = tableMotor.getTachoCount();
		tableMotor.forward();
		tableMotor.waitComplete();
		tableRightAngle = tableMotor.getTachoCount();
		
		tableMotor.setStallThreshold(25,5);
		tableMotor.setSpeed(TABLESPEED);
		tableMotor.rotateTo((tableLeftAngle + tableRightAngle) / 2);
		
		tableMotor.resetTachoCount();
		tableAngle = 0;
	}

	/**
	 * Manual Calibration of tableMotor.
	 */
	private void calibrateTable() {
		int button;
		LCD.drawString("Down", 0, 3);
		LCD.drawString("ENTER: Down", 0, 5);
		LCD.drawString("UP: More", 0, 6);
		LCD.drawString("DOWN: Less", 0, 7);
		while ((button = Button.waitForAnyPress()) != Button.ID_ESCAPE) {
			if (button == Button.ID_UP) {
				downCorrection += 1;
			}
			else if (button == Button.ID_DOWN) {
				downCorrection -= 1;
			}
			else if (button == Button.ID_ENTER) {
				this.down(false, 0);
			}
		}
		LCD.drawString("DownInverted", 0, 3);
		LCD.drawString("ENTER: DownInverted", 0, 5);
		while ((button = Button.waitForAnyPress()) != Button.ID_ESCAPE) {
			if (button == Button.ID_UP) {
				downInvertedCorrection -= 1;
			}
			else if (button == Button.ID_DOWN) {
				downInvertedCorrection += 1;
			}
			else if (button == Button.ID_ENTER) {
				this.downInverted(false, 0);
			}
		}
		LCD.clear(3);
		LCD.drawString("SwitchDown", 0, 3);
		LCD.clear(5);
		LCD.drawString("ENTER: SwitchDown", 0, 5);
		while ((button = Button.waitForAnyPress()) != Button.ID_ESCAPE) {
			if (button == Button.ID_UP) {
				switchDownCorrection += 1;
			}
			else if (button == Button.ID_DOWN) {
				switchDownCorrection -= 1;
			}
			else if (button == Button.ID_ENTER) {
				this.switchDown(false, 0);
			}
		}
		try {
			FileWriter tconfigFile = new FileWriter("tablecalibration");
			tconfigFile.write(downCorrection + "\n");
			tconfigFile.write(downInvertedCorrection +"\n");
			tconfigFile.write(switchDownCorrection +"\n");
			tconfigFile.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		LCD.clear();
	}

	/**
	 * Pushes fork towards cube or pulls is back.
	 * Executed movement depends on table position indicated by closed-variable.
	 * @param immediateReturn true: method returns immediately, movement is finished in background. false: method returns after movement is finished.
	 */
	private void moveFork(boolean immediateReturn) {
		if (!closed) {moverMotor.rotateTo(FORKMOVERANGLE, immediateReturn);} 
		else moverMotor.rotateTo(0, immediateReturn);
		closed = !closed;
	}

	/**
	 * Pushes fork towards cube or pulls is back.
	 * Executed movement depends on table position indicated by closed-variable.
	 */
	private void moveFork() {
		this.moveFork(false);
	}	
	
	/**
	 * Rotate table by 450 degrees.
	 * Used to scan all top elements.
	 */
	protected void tableTurnScan() {
		this.startScanningAngle = tableMotor.getTachoCount();
		tableAngle = tableAngle + 450;
		tableMotor.rotateTo(tableAngle, true);
	}
	
	/**
	 * Used to determine which element is located under EV3ColorSensor while tableTurnScan() is executed.
	 * @return Current table angle relative to starting position.
	 */
	protected int getTableTachoCount() {
		return (tableMotor.getTachoCount() - this.startScanningAngle);
	}
	
	/**
	 * Wait until tableMotor completes movement.
	 */
	protected void waitCompleteTable() {
		tableMotor.waitComplete();
	}

	/**
	 * Rotate table to current desired tableAngle.
	 */
	protected void reallignTable() {
		tableMotor.rotateTo(tableAngle);
	}

	/**
	 * Rotates cube's front face by 90 degrees clockwise.
	 * @param overshoot Additional rotation angle, that prevents mechanical failures. Not used at the moment!
	 */
	protected void front(int overshoot) {
		forkMotor.rotate(-75, true);
		Delay.msDelay(75);
		moveFork(true);
		Delay.msDelay(75);
		forkMotor.rotateTo(forkIdleAngle + frontTurnAngleRight + overshoot, true);
		Delay.msDelay(400);
		moveFork(true);
		forkMotor.rotateTo(forkIdleAngle);
	}

	/**
	 * Rotates cube's front face by 90 degrees counter clockwise.
	 * @param overshoot Additional rotation angle, that prevents mechanical failures. Not used at the moment!
	 */
	protected void frontInverted(int overshoot) {
		forkMotor.rotate(75, true);
		Delay.msDelay(75);
		moveFork(true);
		Delay.msDelay(75);
		forkMotor.rotateTo(forkIdleAngle - frontTurnAngleLeft - overshoot, true);
		Delay.msDelay(400);
		moveFork(true);
		forkMotor.rotateTo(forkIdleAngle);
	}
	
	/**
	 * Rotates cube's front face by 180 degrees clockwise.
	 * @param overshoot Additional rotation angle, that prevents mechanical failures. Not used at the moment!
	 */
	protected void switchFront(int overshoot) {
		forkMotor.rotate(-75, true);
		Delay.msDelay(75);
		moveFork(true);
		Delay.msDelay(75);
		forkMotor.rotateTo(forkIdleAngle + frontTurnAngleRight, true);
		Delay.msDelay(400);
		moveFork(true);
		Delay.msDelay(75);
		forkMotor.rotateTo(forkIdleAngle, true);
		Delay.msDelay(25);
		forkMotor.rotateTo(-75, true);
		Delay.msDelay(75);
		moveFork(true);
		Delay.msDelay(75);
		forkMotor.rotateTo(forkIdleAngle + frontTurnAngleRight + overshoot, true);
		Delay.msDelay(400);
		moveFork(true);
		forkMotor.rotateTo(forkIdleAngle);
	}
	
	/**
	 * Rotates cube's down face by 90 degrees clockwise (looking at the bottom of the cube).
	 * @param nextRotate indicates if next move is a rotation of whole cube.
	 * @param overshoot Additional rotation angle, that prevents mechanical failures. Not used at the moment!
	 */
	protected void down(boolean nextRotate, int overshoot) {
		tableAngle += 90;
		moveFork(true);
		Delay.msDelay(200);
		tableMotor.rotateTo(tableAngle + downCorrection + 2*overshoot,true);
		Delay.msDelay(400);
		if (nextRotate) {
			moveFork(true);
			Delay.msDelay(100);
		}
		else {
			tableMotor.rotateTo(tableAngle,true);
			moveFork(true);
			Delay.msDelay(200);
		}
	}
	
	/**
	 * Rotates cube's down face by 90 degrees counter clockwise (looking at the bottom of the cube). 
	 * @param nextRotate indicates if next move is a rotation of whole cube.
	 * @param overshoot Additional rotation angle, that prevents mechanical failures. Not used at the moment!
	 */
	protected void downInverted(boolean nextRotate, int overshoot) {
		tableAngle -= 90;
		moveFork(true);
		Delay.msDelay(200);
		tableMotor.rotateTo(tableAngle + downInvertedCorrection - 2*overshoot,true);
		Delay.msDelay(400);
		if (nextRotate) {
			moveFork(true);
		}
		else {
			tableMotor.rotateTo(tableAngle,true);
			moveFork(true);
			Delay.msDelay(200);
		}
	}
	
	/**
	 * Rotates cube's down face by 180 degrees clockwise (looking at the bottom of the cube).
	 * @param nextRotate indicates if next move is a rotation of whole cube.
	 * @param overshoot Additional rotation angle, that prevents mechanical failures. Not used at the moment!
	 */
	protected void switchDown(boolean nextRotate, int overshoot) {
		tableAngle += 90;
		moveFork(true);
		Delay.msDelay(200);
		tableMotor.rotateTo(tableAngle,true);
		Delay.msDelay(300);
		tableAngle += 90;
		tableMotor.rotateTo(tableAngle + switchDownCorrection + 2*overshoot,true);
		Delay.msDelay(300);
		if (nextRotate) {
			moveFork(true);
			Delay.msDelay(100);
		}
		else {
			tableMotor.rotateTo(tableAngle,true);
			moveFork(true);
			Delay.msDelay(200);
		}
	}
	
	/**
	 * Rotates cube by 90 degrees clockwise (looking at the bottom of the cube).
	 */
	protected void rotate() {
		tableAngle = (tableAngle + 90);
		tableMotor.rotateTo(tableAngle - 15);
		tableMotor.rotateTo(tableAngle,true);
	}

	/**
	 * Rotates cube by 90 degrees counter clockwise (looking at the bottom of the cube).
	 */
	protected void rotateInverted() {
		tableAngle = (tableAngle - 90);
		tableMotor.rotateTo(tableAngle + 15);
		tableMotor.rotateTo(tableAngle,true);
	}
	
	/**
	 * Rotates cube by 180 degrees clockwise (looking at the bottom of the cube).
	 */
	protected void switchRotate() {
		tableAngle = (tableAngle + 180);
		tableMotor.rotateTo(tableAngle - 30);
		tableMotor.rotateTo(tableAngle,true);
	}
	
	/**
	 * Calculates additional angle to prevent mechanical failure. Not used at the moment.
	 */
	/*private int overshoot(AllMoves[] maneuver, int thismove, int manIndex) {
		if (thismove + 1 < manIndex) {
			if (maneuver[thismove +1].getGroup() != 2) {
				return ((maneuver[thismove].getRotation() * maneuver[thismove +1].getRotation()) > 0) ? OVERSHOOT : -OVERSHOOT;
			}
			else if ((maneuver[thismove +1] == AllMoves.R) || (maneuver[thismove +1] == AllMoves.RI)) {
				if (thismove + 2 < manIndex) {
					return ((maneuver[thismove].getRotation() * maneuver[thismove +2].getRotation()) > 0) ? OVERSHOOT : -OVERSHOOT;
				}
			}
			else if (maneuver[thismove +1] == AllMoves.SR) {
				
				if ((thismove + 2 < manIndex) && (maneuver[thismove +2].getGroup() == 1)) {
					return ((maneuver[thismove].getRotation() * maneuver[thismove +2].getRotation()) > 0) ? OVERSHOOT : -OVERSHOOT; 
				}
				else if (thismove + 3 < manIndex){
					int i = 3;
					while (maneuver[thismove +i].getGroup() == 3) {if ((thismove + ++i) == manIndex) return 0;}
					return ((maneuver[thismove].getRotation() * maneuver[thismove +i].getRotation()) > 0) ? OVERSHOOT : -OVERSHOOT;
				}
			}
		}
		return 0;
	}*/
	public static int overshoot(AllMoves[] maneuver, int thisMove, int manIndex) {
		try {
			if ((thisMove < manIndex -1) && (maneuver[thisMove].getGroup() != 2)){
				int i;
				if (maneuver[thisMove +1].getGroup() != 2) {i = 1;}
				else if (maneuver[thisMove +1] != AllMoves.SR) {i = 2;}
				else {
					if ((maneuver[thisMove].getGroup() == 0) && (maneuver[thisMove +2].getGroup() == 0)) {
						if (maneuver[thisMove + 3].getGroup() == 2) {i = 4;} // shiat
						else {i = 3;}
					}
					else {i = 2;}
				}
				return ((maneuver[thisMove].getRotation() * maneuver[thisMove +i].getRotation()) > 0) ? OVERSHOOT : -OVERSHOOT; 
			}
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {return 0;}
	}


	/**
	 * Apply given moves to physical cube. 
	 * Applies moves until manIndex is reached.
	 * @param maneuver Array that contains moves.
	 * @param manIndex manIndex -1 is last move that is executed.
	 * @param scanning shows if method is called during scanning process
	 */
	protected void applyMoves(AllMoves[] maneuver, int manIndex, boolean scanning) {
		boolean nextRotate;
		int overshoot;
		for (int i = 0; i < manIndex; i++) {
			nextRotate = (i == (manIndex -1)) ? false : (maneuver[i+1].getGroup() == 2);
			if (!scanning) {overshoot = this.overshoot(maneuver, i, manIndex);}
			else {overshoot = 0;}
			switch (maneuver[i]) {
			case F: front(overshoot); break;
			case FI: frontInverted(overshoot); break;
			case SF: switchFront(overshoot); break;
			case D: down(nextRotate, overshoot); break;
			case DI: downInverted(nextRotate, overshoot); break;
			case SD: switchDown(nextRotate, overshoot); break;
			case R: rotate(); break;
			case RI: rotateInverted(); break;
			case SR: switchRotate(); break;
			case N: LCD.drawString("Null-Move parsed",0,0); break;
			}
		}
	}
	
	/**
	 * Apply given moves to physical cube.
	 * All moves in given array are applied.
	 * @param maneuver Array that contains moves.
	 * @param scanning shows if method is called during scanning process
	 */
	protected void applyMoves(AllMoves[] maneuver, boolean scanning) {
		this.applyMoves(maneuver,maneuver.length, scanning);
	}

//	/* main function for testing purposes */	
//	public static void main(String[] args) {
//		RubiksMachine machine = new RubiksMachine();
//		
//		machine.init();
//		
//		LCD.drawString(""+machine.tableAngle, 0, 0);
//		LCD.drawString(machine.tableMotor.getTachoCount() + "", 0, 1);
//		
//		int button;
//		while ((button = Button.waitForAnyPress()) != Button.ID_ESCAPE) {
//			if (button == Button.ID_ENTER) {
//				machine.rotate();
//			}
//			else if (button == Button.ID_RIGHT) {
//				machine.rotateInverted();
//			}
//			else if (button == Button.ID_LEFT) {
//				machine.switchRotate();
//			}
//			else if (button == Button.ID_DOWN) {
//				machine.switchRotate();
//			}
//			LCD.drawString(""+machine.tableAngle, 0, 2);
//			LCD.drawString(machine.tableMotor.getTachoCount() + "", 0, 3);
//		}
//	}
}