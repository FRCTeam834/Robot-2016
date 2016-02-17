package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import base.Command;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class FeederCommand implements Command {
	private Robot robot;
	private DigitalInput lightSensor;
	private double timeout;
	private final double SPEED = 1.0;
	
	public void edit() {
		String[] labels = {"Timeout"};
		String[] values = {Double.toString(timeout)};
		EditDialog d = new EditDialog(labels,values);
		
		timeout = Double.parseDouble(values[0]);
	}

	public void execute() throws NullPointerException {
		double startTime = Timer.getMatchTime();
		robot.setIntake(SPEED);
		while(!lightSensor.get() ||  Timer.getMatchTime() - startTime < timeout) {
			
		}
		robot.setIntake(0.0);
	}


	public void setRobot(VisualRobot r) {
		robot = (Robot)r;
		lightSensor = (DigitalInput) robot.getSensors().get("tripwire");
	}
	
	public FeederCommand() { }
	public FeederCommand(double t, VisualRobot r)
	{
		timeout = t;
		setRobot(r);
	}

}
