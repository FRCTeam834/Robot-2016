package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import base.Command;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MoveToPointCommand implements Command {
	private Robot robot;
	private double displacementX, displacementY, speed;
	private GyroBase gyro;
	private Encoder LEncoder, REncoder;
	
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
		
		double angle = getAngle(), distance = getDistance();
		
		if(angle > 0) {
			while (gyro.getAngle() < angle && !robot.isDisabled() && robot.isAutonomous()) {
				robot.setRightSide(-speed);
				robot.setLeftSide(speed);
				SmartDashboard.putString("DB/String 2", Double.toString(gyro.getAngle()));

			}
			robot.stop();
		}
		else if(angle < 0) {
			while (gyro.getAngle() > angle && !robot.isDisabled() && robot.isAutonomous()) {
				robot.setRightSide(speed);
				robot.setLeftSide(-speed);
				SmartDashboard.putString("DB/String 2", Double.toString(gyro.getAngle()));

			}
			robot.stop();
		}
		
		REncoder.reset();
		LEncoder.reset();
		while(LEncoder.getDistance() < distance && REncoder.getDistance() < distance && !robot.isDisabled() && robot.isAutonomous())
		{
			robot.setLeftSide(speed);
			robot.setRightSide(speed);
		}
		robot.setLeftSide(0);
		robot.setRightSide(0);
	}


	public void setRobot(VisualRobot r) {
		robot = (Robot)r;
		gyro = (GyroBase) robot.getSensors().get("gyro");
		LEncoder = (Encoder) robot.getSensors().get("leftEncoder");
		REncoder = (Encoder) robot.getSensors().get("rightEncoder");
	}
	
	private double getDistance() {
		return Math.sqrt(displacementX * displacementX + displacementY * displacementY);
	}
	private double getAngle() {
		return 90 - Math.atan2(displacementY, displacementX) * 180 / Math.PI;
		
	}
	
	public MoveToPointCommand() {
	}

	/**
	 * 
	 * @param dx The displacement on the x axis.
	 * @param dy The displacement on the y axis.
	 * @param s The speed at which to move.
	 * @param r The robot.
	 */
	public MoveToPointCommand(double dx, double dy, double s, VisualRobot r) {
		displacementX = dx;
		displacementY = dy;
		speed = s;
		if(r!= null)
		setRobot(r);
	}
}
