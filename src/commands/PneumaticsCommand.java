package commands;

import org.usfirst.frc.team834.robot.*;
import base.Command;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PneumaticsCommand implements Command {
	
	private static final long serialVersionUID = 5041876206361082524L;
	private Robot robot;
	private boolean isOpen;

	public void edit() {
		String[] labels = {"'open' or 'close'"};
		String[] values = {isOpen ? "open" : "close"};
		EditDialog d = new EditDialog(labels,values);		
		
		if(values[0].equals("open")) {
			isOpen = true;
		}
		else if(values[0].equals("close")) {
			isOpen = false;
			
		}
	}

	public void execute() throws NullPointerException {
		robot.shift(isOpen);
	}

	public void setRobot(VisualRobot r) {
		robot = (Robot)r;
	}

	public PneumaticsCommand() {
	}

	/**
	 * 
	 * @param o Whether the pneumatics should be open or close.
	 * @param r The robot.
	 */
	public PneumaticsCommand(boolean o, VisualRobot r) {
		isOpen = o;
		if(r!=null) setRobot(r);
	}
}

