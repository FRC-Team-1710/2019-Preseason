package org.usfirst.frc.team1710.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Penn
 * creates variables for a table from limelight
 * creates entries for tx value, ty value and tv value
 * 
 * The whole vision class takes the values from limelight and makes the robot spin until it sees the cube, then it drives forward to intake the cube all at the push of a button
 */
public class Vision2 {
	
	static NetworkTableInstance table = NetworkTableInstance.getDefault();
	static NetworkTable tableTwo = table.getTable("limelight");
	public static NetworkTableEntry ledEntry = tableTwo.getEntry("ledMode");
	double ledValue = ledEntry.getDouble(0);

	static NetworkTableEntry txEntry = tableTwo.getEntry("tx");
	static NetworkTableEntry tyEntry = tableTwo.getEntry("ty");
	static NetworkTableEntry tvEntry = tableTwo.getEntry("tv");
	
	if (ControllerMap.altvision() == true ) {
		if (tvEntry > 1) {
			Drive.arcadeDrive(0, -.4, false);
		} else {
			Drive.arcadeDrive(0,0, false);
		}
	} else {
		Drive.arcadeDrive(0,0, false);
	}		
	/*
	  Initializing tje table so it can be used in the rest of the cube
	  creates a tabletwo than gets a table the limelight web interface gives
	  grabs the LED's from the limelight table and gets the on and off values  0 = on, 1 = off
	 */
	public static void initializeVision() {
		table = NetworkTableInstance.getDefault();
		tableTwo = table.getTable("limelight");
		ledEntry = tableTwo.getEntry("ledMode");
		ledEntry.forceSetNumber(0);
		ledEntry.forceSetNumber(1);
	}
    
	/**
	 * the tx value is the value of the width of the screen and we use it to check if the cube is at the center of the screen
	 * @return the Tx value which is set to the double it gets from the limelight web interface
	 */

	public static double getTxValue() {
		double txValue = txEntry.getDouble(0);
		return txValue;
	}
	
	
	
	
	
}