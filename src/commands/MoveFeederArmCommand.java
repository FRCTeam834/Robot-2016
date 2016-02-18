package commands;

import org.usfirst.frc.team834.robot.*;

import base.Command;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;

public class MoveFeederArmCommand implements Command {
	private Robot robot;
	private AnalogGyro gyro;
	private boolean direction; //true is up, false is down
	private double angle;
	private final double SPEED = 0.3;
	
	public void edit() {
		String[] labels = {"Direction", "Angle"};
		String[] values = {Boolean.toString(direction), Double.toString(angle)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = values[0] == "up";
		angle = Double.parseDouble(values[1]);
	}

	public void execute() throws NullPointerException {
		if(direction)
			while(gyro.getAngle() < angle)
				robot.setBackArm(SPEED);
		else
			while(gyro.getAngle() > angle)
				robot.setBackArm(-SPEED);
	}

	public void setRobot(VisualRobot r) {
		robot = (Robot) r;
		gyro = (AnalogGyro)robot.getSensors().get("feederArmGyro");
	}
	
	public MoveFeederArmCommand() {
	}
	/**
	 * 
	 * @param dir The direction in which to move the arm.
	 * @param ang The angle to move the arm to.
	 * @param r The robot.
	 */
	public MoveFeederArmCommand(boolean dir, double ang, VisualRobot r) {
		direction = dir;
		angle = ang;
		setRobot(r);
	}
}