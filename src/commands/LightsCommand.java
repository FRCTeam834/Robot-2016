// Authors:
// Last Edited: 1/15/2016
// Description: Command for toggling the lights in the robot.

package commands;

import org.usfirst.frc.team834.robot.*;
import base.Command;
import edu.wpi.first.wpilibj.Relay;

public class LightsCommand implements Command {
	//Variable to represent the robot.
	private Robot robot;
	//Boolean to determine whether or not the lights should be on.
	private boolean on;
	
	public void edit() {
		//Set values of labels and default values of textboxes in the command edit menu.
		String[] labels = {"Turn 'on' or 'off'"};
		String[] values = {on ? "on" : "off"};
		EditDialog d = new EditDialog(labels,values);		
		
		//If the value of the first textbox is "on", set the "on" boolean to true. If it is "off", turn the "on" boolean to false.
		if(values[0].equals("on"))
			on = true;
		else if(values[0].equals("off"))
			on = false;
	}

	public void execute() throws NullPointerException {
		//Set the lights to the "on" boolean upon executing.
		robot.setBlueLights(on);
		robot.setWhiteLights(on);

	}

	public void setRobot(VisualRobot r) {
		//Set the robot variable.
		robot = (Robot) r;
	}

	/**
	 * 
	 * @param o whether the lights will turn on or off
	 * @param r robot
	 */
	public LightsCommand(boolean o, VisualRobot r) {
		on = o;
		setRobot(r);
	}

}
