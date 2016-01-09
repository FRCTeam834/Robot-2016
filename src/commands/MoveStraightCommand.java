package commands;

import Config.VisualRobot;
import base.Command;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;

public class MoveStraightCommand implements Command {
	private VisualRobot robot;
	private Encoder REncoder;
	private Encoder LEncoder;
	private Gyro gyro;

	private double speed;
	private double distance;
	private double cFactor;
	
	public MoveStraightCommand() {
		cFactor = speed/45;
	}
	
	public void edit() {
		String[] labels = {"Distance", "Speed"};
		String[] values = {Double.toString(distance), Double.toString(speed)};
		EditDialog f = new EditDialog(labels, values);
		
		distance = Double.parseDouble(values[0]);
		
		if(Math.abs(Double.parseDouble(values[1])) <= 1.0) {
			speed = Double.parseDouble(values[1]);
		}
		
	}

	public void execute() throws NullPointerException{
		gyro.reset();
		REncoder.reset();
		LEncoder.reset();
		
		while( (REncoder.getDistance() + LEncoder.getDistance())/2 < distance && robot.isAutonomous() && !robot.isDisabled()) {
			double rspeed = speed;
			double lspeed = speed;
			
			
			if(gyro.getAngle() < 0) {
				rspeed -= Math.abs(gyro.getAngle())/cFactor;
			}
			else if(gyro.getAngle() > 0) {
				lspeed -= Math.abs(gyro.getAngle())/cFactor;
				
			}
			robot.setLeftSide(lspeed);
			robot.setLeftSide(rspeed);

		}
	}

	
	public void setRobot(VisualRobot r) {
		robot = r;
		gyro = (Gyro) robot.getSensors().get("gyro");
		REncoder = (Encoder) robot.getSensors().get("leftEncoder");
		LEncoder = (Encoder) robot.getSensors().get("rightEncoder");

	}

}
