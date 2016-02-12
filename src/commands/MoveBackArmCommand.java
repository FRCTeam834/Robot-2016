package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import edu.wpi.first.wpilibj.DigitalInput;

public class MoveBackArmCommand {
	private Robot robot;
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
				robot.setFeederArm(SPEED);
		else if(!direction)
			while(!bottomArmInput.get())
				robot.setFeederArm(-SPEED);
		robot.setFeederArm(0);
		//SmartDashboard.putString("DB/String 9", Boolean.toString(isOpen));
	}

	public void setRobot(VisualRobot r) {
		robot = (Robot) r;
		topArmInput = (DigitalInput)robot.getSensors().get("topArmInput");
		bottomArmInput = (DigitalInput)robot.getSensors().get("bottomArmInput");
	}
	
	public MoveBackArmCommand() {
	}
	
	public MoveBackArmCommand(boolean dir) {
		direction = dir;
	}
}
