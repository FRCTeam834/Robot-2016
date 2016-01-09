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
	
	public void edit() {
		
	}

	public void execute() throws NullPointerException{

	}

	
	public void setRobot(VisualRobot r) {
		
	}

}
