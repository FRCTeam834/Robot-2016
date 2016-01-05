package commands;
import Config.*;
import base.*;

public class TurnCommand implements Command {
	private double angle;
	private double speed;
	private VisualRobot robot;
	 
	
	public TurnCommand() {
		
	}
	
	public void setRobot(VisualRobot r) {
		robot = r;
	}
	
	public void edit() {
		
		
	}
	public void execute() {
		if(angle > 0) {
			robot.setRightSide(-speed);
			robot.setLeftSide(speed);
			while (robot.getGyro().getAngle() < angle) {
				
			}
			robot.stop();
		}
		else if(angle < 0) {
			robot.setRightSide(speed);
			robot.setLeftSide(-speed);
			while (robot.getGyro().getAngle() > angle) {
				
			}
			robot.stop();
		}
		
		
		
	}
	
}
