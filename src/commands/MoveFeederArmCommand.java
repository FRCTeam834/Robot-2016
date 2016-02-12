package commands;

import org.usfirst.frc.team834.robot.*;

import base.Command;
import edu.wpi.first.wpilibj.DigitalInput;

public class MoveFeederArmCommand implements Command {
	private VisualRobot robot;
	private DigitalInput topArmInput, bottomArmInput;
	private boolean direction; //true is up, false is down
	private final double SPEED = 0.3;
	
	public void edit() {
		String[] labels = {"Direction"};
		String[] values = {Boolean.toString(direction)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = Boolean.parseBoolean(values[0]);
	}

	public void execute() throws NullPointerException {
		
		if(direction) 
			while(!topArmInput.get())
				(Robot)robot.setArm(SPEED);
		else if(!direction)
			while(!bottomArmInput.get())
				robot.setArm(-SPEED);
		robot.setArm(0);
		//SmartDashboard.putString("DB/String 9", Boolean.toString(isOpen));
	}

	public void setRobot(VisualRobot r) {
		robot = r;
		topArmInput = (DigitalInput)robot.getSensors().get("topArmInput");
		bottomArmInput = (DigitalInput)robot.getSensors().get("bottomArmInput");
	}
	
	public MoveFeederArmCommand() {
	}
	
	public MoveFeederArmCommand(boolean dir) {
		direction = dir;
	}
}
