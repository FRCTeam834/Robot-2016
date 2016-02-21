package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import base.Command;
import edu.wpi.first.wpilibj.GyroBase;

public class MoveAlongCurveCommand implements Command {
	private Robot robot;
	private boolean direction; //true is cw, false is ccw
	private double radius, speed, angle;
	private final double WIDTH = 24.0;
	private GyroBase gyro;
	
	public void edit() {
		String[] labels = {"Direction", "Radius", "Speed", "Angle"};
		String[] values = {Boolean.toString(direction), Double.toString(radius), Double.toString(speed), Double.toString(angle)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = Boolean.parseBoolean(values[0]);
		radius = Double.parseDouble(values[1]);
		speed = Double.parseDouble(values[2]);
		angle = Double.parseDouble(values[3]);
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
		gyro = (GyroBase)robot.getSensors().get("gyro");
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
