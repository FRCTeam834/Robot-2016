package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import base.Command;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;

public class MoveBackArmCommand implements Command{
	private static final long serialVersionUID = 396817677970190459L;

	private Robot robot;
	private AnalogGyro gyro;
	private boolean direction; //true is up, false is down
	private double angle;
	private double timeout;
	private double speed = 0.3;
	
	public void edit() {
		String[] labels = {"Direction", "Angle", "Speed", "Timeout"};
		String[] values = {direction ? "up" : "down", Double.toString(angle), Double.toString(speed), Double.toString(timeout)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = values[0].equals("up");
		angle = Double.parseDouble(values[1]);
		speed = Double.parseDouble(values[2]);
		timeout = Double.parseDouble(values[3]);

	}

	public void execute() throws NullPointerException {
		long startTime = System.currentTimeMillis();

		if(direction)
			while(gyro.getAngle() < angle && gyro.getAngle() < 180 &&  System.currentTimeMillis() - startTime < timeout)
				robot.setBackArm(speed);
		else
			while(gyro.getAngle() > angle && gyro.getAngle() > 0 && System.currentTimeMillis()- startTime < timeout )
				robot.setBackArm(-speed);
	}

	public void setRobot(VisualRobot r) {
		robot = (Robot) r;
		gyro = (AnalogGyro)robot.getSensors().get("backArmGyro");
	}
	
	public MoveBackArmCommand() {
	}
	/**
	 * 
	 * @param dir The direction in which to move the arm.
	 * @param ang The angle to move the arm to.
	 * @param r The robot.
	 */
	public MoveBackArmCommand(boolean dir, double ang, double spd, VisualRobot r) {
		direction = dir;
		speed = spd;
		angle = ang;
		timeout = 5000;
		setRobot(r);
	}
}