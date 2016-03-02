package commands;

import org.usfirst.frc.team834.robot.Robot;
import org.usfirst.frc.team834.robot.VisualRobot;
import base.*;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnCommand implements Command {
	private double angle, speed;
	private VisualRobot robot;
	private ADXRS450_Gyro gyro;
	
	public void setRobot(VisualRobot r) {
		robot = r;
		gyro = (ADXRS450_Gyro) robot.getSensors().get("gyro");
	}
	
	public void edit() {
		String[] labels = {"Angle", "Speed"};
		String[] values = {Double.toString(angle), Double.toString(speed)};
		EditDialog f = new EditDialog(labels, values);
		
		angle = Double.parseDouble(values[0]);
		
		if(Math.abs(Double.parseDouble(values[1])) <= 1.0) {
			speed = Double.parseDouble(values[1]);
		}
		
	}
	public void execute() throws NullPointerException {
		
		gyro.reset();
		
		if(angle != 0) {
			while (angle > 0 ? gyro.getAngle() < angle : gyro.getAngle() > angle && !robot.isDisabled() && robot.isAutonomous()) {
				robot.setRightSide(angle > 0 ? -speed : speed);
				robot.setLeftSide(angle > 0 ? speed : -speed);
				SmartDashboard.putString("DB/String 2", Double.toString(gyro.getAngle()));
			}
			robot.stop();
		}
	}
	
	public TurnCommand() {
	}
	
	/**
	 * 
	 * @param ang The angle of which to turn.
	 * @param s The speed at which to turn.
	 * @param r The robot.
	 */
	public TurnCommand(double ang, double s, VisualRobot r) {
		angle = ang;
		if(s > 1.0) 
			speed = 1.0;
		else if(s < -1.0) 
			speed = -1.0;
		else
			speed = s;
		setRobot(r);
	}

	
}
