package commands;

import Config.*;
import base.*;
import edu.wpi.first.wpilibj.AnalogGyro;

public class TurnCommand implements Command {
	private double angle;
	private double speed;
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
			robot.setRightSide(-speed);
			robot.setLeftSide(speed);
			while (gyro.getAngle() < angle && !robot.isDisabled()) {
				
			}
			robot.stop();
		}
		else if(angle < 0) {
			robot.setRightSide(speed);
			robot.setLeftSide(-speed);
			while (gyro.getAngle() > angle && !robot.isDisabled()) {
				
			}
			robot.stop();
		}
		
		
	}
	
	
	
}
