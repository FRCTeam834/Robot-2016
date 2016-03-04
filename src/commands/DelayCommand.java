 package commands;

import org.usfirst.frc.team834.robot.VisualRobot;

import base.Command;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DelayCommand implements Command {	
	private static final long serialVersionUID = 2282893700588213588L;
	private double time;
	public void edit() {
		String[] labels = {"Time"};
		String[] values = {Double.toString(time)};
		EditDialog d = new EditDialog(labels,values);		
		
		time = Double.parseDouble(values[0]);
	}

	public void execute() throws NullPointerException {
		Timer.delay(time);
	}

	public void setRobot(VisualRobot r) {
	}
	
	public DelayCommand() {
		
	}
	
	/**
	 * 
	 * @param dir The direction in which to move.
	 * @param r The robot.
	 */
	public DelayCommand(double t) {
		time = t;
	}
}
