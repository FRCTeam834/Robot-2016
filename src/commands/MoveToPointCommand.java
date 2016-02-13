package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import edu.wpi.first.wpilibj.AnalogGyro;

public class MoveToPointCommand {
	private Robot robot;
	private double displacementX, displacementY, speed;
	private AnalogGyro gyro;
	
	public void edit() {
		String[] labels = {"DisplacementX", "DisplacementY", "Speed"};
		String[] values = {Double.toString(displacementX), Double.toString(displacementY), Double.toString(speed)};
		EditDialog d = new EditDialog(labels,values);		
		
		displacementX = Double.parseDouble(values[0]);
		displacementY = Double.parseDouble(values[1]);
		speed = Double.parseDouble(values[2]);
	}

	public void execute() throws NullPointerException {
		gyro.reset();
		
		if(!direction)
			while(angle > gyro.getAngle())
			{
				robot.setLeftSide(speed * (radius / (radius + WIDTH)));
				robot.setRightSide(speed);
			}
		else
			while(angle < gyro.getAngle())
			{
				robot.setLeftSide(speed);
				robot.setRightSide(speed * (radius / (radius + WIDTH)));
			}
	}


	public void setRobot(VisualRobot r) {
		robot = (Robot)r;
		gyro = (AnalogGyro)robot.getSensors().get("gyro");
	}
	
	public MoveAlongCurveCommand() {
	}

	/**
	 * 
	 * @param dir The direction in which to move.
	 * @param rad The radius of the robot.
	 * @param s The speed at which to move.
	 * @param ang The angle to move to.
	 * @param r The robot.
	 */
	public MoveAlongCurveCommand(boolean dir, double rad, double s, double ang, VisualRobot r) {
		direction = dir;
		radius = rad;
		speed = s;
		angle = ang;
		setRobot(r);
	}
}
