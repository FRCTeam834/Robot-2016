package commands;

import org.usfirst.frc.team834.robot.*;

import base.Command;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MoveFeederArmCommand implements Command {

	private static final long serialVersionUID = 3082587226008573873L;

	private Robot robot;
	private AnalogGyro gyro;
	private boolean direction; //true is up, false is down
	private int timeout;
	private double angle;
	private double speed;
	
	public void edit() {
		String[] labels = {"Direction", "Angle", "Speed", "Timeout"};
		String[] values = {direction ? "up" : "down", Double.toString(angle), Double.toString(speed), Integer.toString(timeout)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = values[0].equals("up");
		angle = Double.parseDouble(values[1]);
		speed = Double.parseDouble(values[2]);
		timeout = Integer.parseInt(values[3]);
	}

	public void execute() throws NullPointerException {
		long startTime = System.currentTimeMillis();
		if(direction) {
			SmartDashboard.putString("DB/String 9", "Going up");
			SmartDashboard.putString("DB/String 8", "Gyro and angle: " + (gyro.getAngle() < angle));
			SmartDashboard.putString("DB/String 7", "" + (gyro == null));
			SmartDashboard.putString("DB/String 6", "angle: " + gyro.getAngle());
			while(gyro.getAngle() < angle && gyro.getAngle() < 150 && System.currentTimeMillis() - startTime < timeout) {
				robot.setFeederArm(speed);
				Timer.delay(.005);
			}
		}
		else {
			while(gyro.getAngle() > angle && gyro.getAngle() > 0 && System.currentTimeMillis() - startTime > timeout) {
				robot.setFeederArm(-speed);
				Timer.delay(.005);
			}
		}
		
	}

	public void setRobot(VisualRobot r) {
		robot = (Robot) r;
		gyro = (AnalogGyro)robot.getSensors().get("feederArmGyro");
	}
	
	public MoveFeederArmCommand() {
	}
	/**
	 * 
	 * @param dir The direction in which to move the arm.
	 * @param ang The angle to move the arm to.
	 * @param r The robot.
	 */
	public MoveFeederArmCommand(boolean dir, double ang, double spd,VisualRobot r) {
		direction = dir;
		speed = spd;
		angle = ang;
		timeout = 5000;
		setRobot(r);
	}
}