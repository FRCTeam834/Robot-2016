package commands;

import Config.*;
import base.*;
import edu.wpi.first.wpilibj.Gyro;

public class TurnCommand implements Command {
	private double angle;
	private double speed;
	private VisualRobot robot;
	private Gyro gyro;

	public void setRobot(VisualRobot r) {
		robot = r;
		gyro = (Gyro) robot.getSensors().get("gyro");
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
		if(robot.isAutonomous()) {
		gyro.reset();
		if(angle > 0) {
			robot.setRightSide(-speed);
			robot.setLeftSide(speed);
			while (gyro.getAngle() < angle) {
				
			}
			robot.stop();
		}
		else if(angle < 0) {
			robot.setRightSide(speed);
			robot.setLeftSide(-speed);
			while (gyro.getAngle() > angle) {
				
			}
			robot.stop();
		}
		}
		
		
	}
	
	
	
}
