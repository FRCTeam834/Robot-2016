package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import edu.wpi.first.wpilibj.Timer;

public class ShootCommand {
	private Robot robot;
	private double time;
	private final double SPEED = 69;
	
	public void edit() {
		String[] labels = {"Time"};
		String[] values = {Double.toString(time)};
		EditDialog d = new EditDialog(labels,values);
		
		time = Double.parseDouble(values[0]);
	}

	public void execute() throws NullPointerException {
		robot.setIntake(SPEED);
		Timer.delay(time);
		robot.setIntake(0.0);
	}


	public void setRobot(VisualRobot r) {
		robot = (Robot)r;
	}
	
	public ShootCommand() { }
	public ShootCommand(double t, VisualRobot r)
	{
		time = t;
		setRobot(r);
	}
}
