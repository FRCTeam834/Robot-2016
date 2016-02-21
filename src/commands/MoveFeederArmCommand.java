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
	private double speed = 0.3;
	
	public void edit() {
		String[] labels = {"Direction", "Angle", "Speed"};
		String[] values = {Boolean.toString(direction), Double.toString(angle), Double.toString(speed)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = values[0] == "up";
		angle = Double.parseDouble(values[1]);
		speed = Double.parseDouble(values[2]);
	}

	public void execute() throws NullPointerException {
		if(direction)
			while(gyro.getAngle() < angle)
				robot.setBackArm(speed);
		else
			while(gyro.getAngle() > angle)
				robot.setBackArm(-speed);
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