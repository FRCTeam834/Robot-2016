package commands;

import org.usfirst.frc.team834.robot.VisualRobot;
import base.*;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnCommand implements Command {
	private double angle, speed;
	private VisualRobot robot;
	private AnalogGyro gyro;
	
	public void setRobot(VisualRobot r) {
		robot = r;
		gyro = (AnalogGyro) robot.getSensors().get("gyro");
	}
	
	public void edit() {
		String[] labels = {"Angle", "Speed"};
		String[] values = {Double.toString(angle), Double.toString(speed)};
		EditDialog f = new EditDialog(labels, values);
		
		angle = Double.parseDouble(values[0]);
		
		if(Math.abs(Double.parseDouble(values[1])) <= 1.0) {
			speed = Double.parseDouble(values[1]);
		}
		
	}
	public void execute() throws NullPointerException {
		gyro.reset();
		
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
		
		
	}
	
	
	
}
