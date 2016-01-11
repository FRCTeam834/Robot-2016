package commands;

import org.usfirst.frc.team834.robot.*;
import base.Command;
import edu.wpi.first.wpilibj.Relay;

public class LightsCommand implements Command {
	private VisualRobot robot;
	private Relay lights;
	private boolean on;
	
	public void edit() {
		String[] labels = {"Turn 'on' or 'off'"};
		String[] values = {on ? "on" : "off"};
		EditDialog d = new EditDialog(labels,values);		
		
		if(values[0].equals("on")) {
			on = true;
		}
		if(values[0].equals("off")) {
			on = false;
			
		}
	}

	public void execute() throws NullPointerException {
		robot.setLights(on);
	}

	public void setRobot(VisualRobot r) {
		robot = r;
	}

}
