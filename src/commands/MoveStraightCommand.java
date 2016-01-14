package commands;

import org.usfirst.frc.team834.robot.VisualRobot;
import base.Command;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;

public class MoveStraightCommand implements Command {
	private VisualRobot robot;
	private Encoder REncoder;
	private Encoder LEncoder;
	private AnalogGyro gyro;

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
		REncoder.reset();
		LEncoder.reset();
		gyro.reset();
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
			robot.setRightSide(rspeed);

		}
	}

	
	public void setRobot(VisualRobot r) {
		robot = r;
		gyro = (AnalogGyro) robot.getSensors().get("gyro");
		LEncoder = (Encoder) robot.getSensors().get("leftEncoder");
		REncoder = (Encoder) robot.getSensors().get("rightEncoder");

	}

}
