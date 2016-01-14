package commands;

import org.usfirst.frc.team834.robot.*;
import base.Command;
import edu.wpi.first.wpilibj.Solenoid;

public class PneumaticsCommand implements Command {
	private VisualRobot robot;
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
		robot = r;
	}

}

