package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;

public class MoveBackArmCommand {
	private Robot robot;
	private AnalogGyro gyro;
	private boolean direction; //true is up, false is down
	private double angle;
	private final double SPEED = 0.3;
	
	public void edit() {
		String[] labels = {"Direction", "Angle"};
		String[] values = {Boolean.toString(direction), Double.toString(angle)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = Boolean.parseBoolean(values[0]);
		angle = Double.parseDouble(values[1]);
	}

	public void execute() throws NullPointerException {
		if(direction)
			while(gyro.getAngle() < angle)
				robot.setBackArm(SPEED);
		else
			while(gyro.getAngle() > angle)
				robot.setBackArm(-SPEED);
		//SmartDashboard.putString("DB/String 9", Boolean.toString(isOpen));
	}

	public void setRobot(VisualRobot r) {
		robot = (Robot) r;
		gyro = (AnalogGyro)robot.getSensors().get("armGyro");
	}
	
	public MoveBackArmCommand() {
	}
	
	public MoveBackArmCommand(boolean dir) {
		direction = dir;
	}
}
