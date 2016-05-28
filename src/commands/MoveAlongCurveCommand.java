package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import base.Command;
import edu.wpi.first.wpilibj.GyroBase;

public class MoveAlongCurveCommand implements Command {
	private static final long serialVersionUID = 6241191079543041229L;

	private Robot robot;
	private double radius, speed, angle;
	private final double WIDTH = 24.0;
	private GyroBase gyro;
	
	public void edit() {
		String[] labels = {"Radius", "Speed", "Angle"};
		String[] values = {Double.toString(radius), Double.toString(speed), Double.toString(angle)};
		EditDialog d = new EditDialog(labels,values);		
		
		radius = Double.parseDouble(values[0]);
		speed = Double.parseDouble(values[1]);
		angle = Double.parseDouble(values[2]);

	}

	public void execute() throws NullPointerException {
		gyro.reset();
		if(angle < 0)
			while(gyro.getAngle() > angle)
			{
				robot.setLeftSide(speed * (radius / (radius + WIDTH)));
				robot.setRightSide(speed);
			}
		else if(angle > 0)
			while(gyro.getAngle() < angle )
			{
				robot.setLeftSide(speed);
				robot.setRightSide(speed * (radius / (radius + WIDTH)));
			}
		
		robot.setLeftSide(0.0);
		robot.setRightSide(0.0);

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
	public MoveAlongCurveCommand(double rad, double s, double ang, VisualRobot r) {
		radius = rad;
		speed = s;
		angle = ang;
		if(r!=null) setRobot(r);
	}

}
