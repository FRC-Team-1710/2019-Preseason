package commandGroups;

import org.usfirst.frc.team1710.robot.Constants;

import commands.ChangeLiftSetpoint;
import commands.DriveToPosition;
import commands.PitchIntake;
import commands.RunIntake;
import commands.TurnToAngle;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class LeftStartLeftScaleLeftSwitch extends CommandGroup {

    public LeftStartLeftScaleLeftSwitch() {
    	addParallel(new PitchIntake(false));
    	addSequential(new DriveToPosition(200, .75, true,0,false,false));
    	addParallel(new DriveToPosition(30,.35,true,20, true,false));
    	addSequential(new ChangeLiftSetpoint(Constants.scaleHigh));
    	addSequential(new RunIntake(true));
    	addParallel(new ChangeLiftSetpoint(Constants.intake));
    	addSequential(new DriveToPosition(-40,.35,true,45, true,true));
    	addSequential(new DriveToPosition(-40,.35,true,90, true,true));
    	addSequential(new DriveToPosition(20,.4,true,145, false,false));
    	addSequential(new DriveToPosition(25,.4,true,155, false,false));
    	addSequential(new RunIntake(false));
    	addSequential(new ChangeLiftSetpoint(Constants.switchPosition));
    	addSequential(new DriveToPosition(8,.4,false,125, false,false));
    	addSequential(new RunIntake(true));
    }
}
