package org.usfirst.frc.team1710.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;


import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



// Hello friends.

public class Drive {
	
	static boolean navxReset = false;
	static double setPoint;
	static double lastAngle, angleIntegral, output;

	// Cyrus: Here I initalize the constant rate of change we want to use;
	public static final double dRateOfChange = 0.05;

	// Cyrus: I removed goal values since it will be coming in as a parameter (see 'Approach' method)
	// Cyrus: I updated current values for x (left-right) and y (forward-backward)
	public static double dCurrentX, dCurrentY;

	public static void initializeDrive () {
		RobotMap.R1 = new TalonSRX (Constants.rightLeaderid);
		RobotMap.R2 = new VictorSPX (Constants.rightFollowerid);
		RobotMap.R3 = new VictorSPX (Constants.rightFollowerid2);
		RobotMap.L1 = new TalonSRX (Constants.leftLeaderid);
		RobotMap.L2 = new VictorSPX (Constants.leftFollowerid);
		RobotMap.L3 = new VictorSPX (Constants.leftFollowerid2);
		
		RobotMap.climber = new Talon(2);
		
		RobotMap.R2.follow (RobotMap.R1);
		RobotMap.R3.follow (RobotMap.R1);
		RobotMap.L2.follow (RobotMap.L1);
		RobotMap.L3.follow (RobotMap.L1);
		//these need to be inverted with robot 1 as of now... hopefully matt will fix that
		RobotMap.R2.setInverted(false);
		RobotMap.L3.setInverted(false);
		
		RobotMap.shifter = new DoubleSolenoid(Constants.shifterForward,Constants.shifterReverse);
		
		RobotMap.navx = new AHRS(SPI.Port.kMXP);
		Drive.setBrakeMode();
	}
	
	public static void setShifters(boolean isShifted) {
		if(isShifted == true) {
			RobotMap.shifter.set(Value.kReverse);
		} else {
			RobotMap.shifter.set(Value.kForward);
		}
	}
	public static double rotationsToInches(double rotations) {
		return rotations  * (Constants.wheelDiameter * Math.PI);
	}
	private static double inchesToRotations(double inches) {
        return inches / (Constants.wheelDiameter* Math.PI);
    }
	private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    public double getRightDistanceInches() {
        return rotationsToInches(RobotMap.R1.getSelectedSensorPosition(8));
    }

    public double getLeftDistanceInches() {
        return rotationsToInches(RobotMap.L1.getSelectedSensorPosition(6));
    }
	/*public static double rpmToInchesPerSecond(double rpm) {
		return rotationsToInches(rpm) / 60;
	}
	public static double getRightVelocity1() {
		return rpmToInchesPerSecond(RobotMap.R1.getSelectedSensorVelocity(8));
	}
	public static double getLeftVelocity1() {
		return rpmToInchesPerSecond(RobotMap.L1.getSelectedSensorVelocity(6));
	}
	public static void autoShift(double velocityL, double lowThreshold, double highThreshold) {
		
		velocityL = getLeftVelocity(); 
		lowThreshold = Constants.shiftLowThreshold;
		highThreshold = Constants.shiftHighThreshold;
		
		if(getLeftVelocity() > highThreshold) {
			setShifters(true);
		}else if( getLeftVelocity() < lowThreshold) {
			setShifters(false);
		}
	}*/

	/**
	 * Cyrus: I changed the parameters to be our goal values.
	 * Since the current values are stored in the class, we don't need to be passing them in as parameters.
	 * We also don't return any values, we'll set the motor values directly after calculating.
	 */
	public static void Approach(double goalX, double goalY){
		// Cyrus: since we have both a forward-backwards and left-right to care about we need to calculate two differences
		double differenceX = goalX - dCurrentX;
		double differenceY = goalY - dCurrentY;

		/**
		 * Cyrus: So for both X(left-right) and Y(forward-directions) directions I determine 
		 * if I need to add or subtract the rate of change constant. 
		 */

		// Cyrus: This figures out if our x is greater than or less than our goal.
		if (differenceX <= dRateOfChange) { 
			/**
			 * If our difference is less than our rate of change value
			 * We are close enough to the goal, so do nothing we should not add our subtract
			 * our rate of change constant
			 */
		} else if (differenceX > 0) {
			/**
			 * Else If our difference positive
			 * Our goal is greater than our current value
			 * So we want to ADD our rate of change constant to get it closer to our goal
			 */
			dCurrentX += dRateOfChange;
		} else {
			/**
			 * Else our difference negative
			 * Our goal is less than our current value
			 * So want to SUBTRACT our rate of change constant to get it closer to our goal
			 */
			dCurrentX -= dRateOfChange;
		}

		// Cyrus: The same is for the Y (forward-backward) direction
		if (differenceY <= dRateOfChange) { 
			// Do nothing
		} else if (differenceY > 0) {
			dCurrentY += dRateOfChange; // dGoal > dCurrent - So add rate of change constant
		} else {
			dCurrentY -= dRateOfChange; // dGoal < dCurrent - So subtract rate of change constant
		}

		// Cyrus: Finally we'll set our current values to the motor output.
		RobotMap.R1.set(ControlMode.PercentOutput, dCurrentX - dCurrentY);
		RobotMap.L1.set(ControlMode.PercentOutput, dCurrentX + dCurrentY);
	}

	public static double getMetersPerSecToFtPerSec(double meters){
		return ftPerSec(meters)/60;
	}
	private static double ftPerSec(double meters) {
		return 0;
	}
	public static double getLRVelocity(){ //get Left Right Velocity
		return getMetersPerSecToFtPerSec(RobotMap.navx.getVelocityX());
	}
	public static double getFBVelocity(){ //get forward backward velocity
		return getMetersPerSecToFtPerSec(RobotMap.navx.getVelocityY());
	}
	public static void arcadeDrive (double side, double forward, boolean shift) {
		if (shift == true) {
			if(navxReset == false) {
				RobotMap.navx.reset();
				navxReset = true;
			}
			//high gear (added approach for smooth driving)
			setShifters(true);
			//side is forward for some reason
			// Cyrus: I commented these out since we'll set them in our 'Approach' method
			// RobotMap.R1.set(ControlMode.PercentOutput, side - forward);
			// RobotMap.L1.set(ControlMode.PercentOutput, side + forward);
			Approach(side, forward);
		} else {
			// Cyrus: I commented these out since we'll set them in our 'Approach' method
			// RobotMap.R1.set(ControlMode.PercentOutput, side - forward);
			// RobotMap.L1.set(ControlMode.PercentOutput, side + forward);
			Approach(side, forward);
			//low gear ( added approach for smooth driving)
			setShifters(false);
			navxReset = false;
		}
		
	}
	
	public static void leftDrive(double power) {
		RobotMap.L1.set(ControlMode.PercentOutput, power);
	}
	
	public static void rightDrive (double power) {
		RobotMap.R1.set(ControlMode.PercentOutput, power);
	}
	
	public static void straightDriveAuto (double power) {
		double error = RobotMap.navx.getAngle();
		rightDrive(error *Constants.kpStraight + power);
		leftDrive(error*Constants.kpStraight - power);
	}
	
	public static void straightDriveTele (double power, double heading, boolean high) {
		double currentAngle = Drive.getNavxAngle();
		double error = (currentAngle - heading);
		angleIntegral += error;
		double angleDeriv = currentAngle - lastAngle;
		
		if(high == true) {
			output = (error * Constants.kpStraightHi) + (angleDeriv * Constants.kdStraightHi);
			
			if(Constants.kiStraightHi * angleIntegral > 1) {
				angleIntegral = 1/Constants.kiStraightHi;
			} else if(Constants.kiStraightHi * angleIntegral < -1){
				angleIntegral = -1/Constants.kiStraightHi;
			}
			
			output += (angleIntegral * Constants.kiStraightHi);
		} else {
			output = (error * Constants.kpStraight) + (angleDeriv * Constants.kdStraight);
			
			if(Constants.kiStraight * angleIntegral > 1) {
				angleIntegral = 1/Constants.kiStraight;
			} else if(Constants.kiStraight * angleIntegral < -1){
				angleIntegral = -1/Constants.kiStraight;
			}
			
			output += (angleIntegral * Constants.kiStraight);
		}
		rightDrive(output + power);
		leftDrive(output - power);
		lastAngle = Drive.getNavxAngle();
		SmartDashboard.putNumber("Auto Drive Output", output);
		SmartDashboard.putNumber("Auto Drive Angle", currentAngle);
	}
	
	public static void setRobotHeading(double heading) {
		double error = (RobotMap.navx.getAngle() - heading);
		rightDrive(error*Constants.kpTurn);
		leftDrive(error*Constants.kpTurn);
	}
	
	public static double getLeftVelocity() {
		return RobotMap.L1.getSelectedSensorVelocity(0);
	}
	
	public static double getRightVelocity() {
		return RobotMap.R1.getSelectedSensorVelocity(0);
	}
	
	public static void stopDriving() {
		RobotMap.R1.set(ControlMode.PercentOutput, 0);
		RobotMap.L1.set(ControlMode.PercentOutput, 0);
	}
	
	public static double getNavxAngle() {
		return RobotMap.navx.getAngle();
	}
	
	public static double getLeftPosition() {
		return RobotMap.L1.getSelectedSensorPosition(0);
	}
	
	public static double getRightPosition() {
		return RobotMap.R1.getSelectedSensorPosition(0);
	}
	public static void setBrakeMode() {
		RobotMap.R1.setNeutralMode(NeutralMode.Brake);
		RobotMap.R2.setNeutralMode(NeutralMode.Brake);
		RobotMap.R3.setNeutralMode(NeutralMode.Brake);
		RobotMap.L3.setNeutralMode(NeutralMode.Brake);
		RobotMap.L2.setNeutralMode(NeutralMode.Brake);
		RobotMap.L1.setNeutralMode(NeutralMode.Brake);	
	}
	public static void setCoastMode() {
		RobotMap.R1.setNeutralMode(NeutralMode.Coast);
		RobotMap.R2.setNeutralMode(NeutralMode.Coast);
		RobotMap.R3.setNeutralMode(NeutralMode.Coast);
		RobotMap.L3.setNeutralMode(NeutralMode.Coast);
		RobotMap.L2.setNeutralMode(NeutralMode.Coast);
		RobotMap.L1.setNeutralMode(NeutralMode.Coast);		
	}

	public static void autoShift(double leftVelocity, double shiftLowThreshold, double shiftHighThreshold) {
	}
	
}
