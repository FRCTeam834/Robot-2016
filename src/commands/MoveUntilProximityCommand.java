//Authors: 
//Last Edited: 1/15/2016
// Description: Command to move until a proximity is reached.

package commands;

import org.usfirst.frc.team834.robot.VisualRobot;
import base.*;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MoveUntilProximityCommand implements Command {
	//Variable to represent the robot.
	private VisualRobot robot;
	//The speed at which the robot should travel, the distane the robot should travel, and the c factor.
	private double speed, distance, cFactor;
	//Variable to represent the ultrasonic.
	private AnalogInput ultrasonic; 
	//Variable to represent the gyroscope.
	private AnalogGyro gyro;
	
	@Override
	public void edit() {
		//Labels and textbox default values to be used in the command edit window.
		String[] labels = {"Distance", "Speed"};
		String[] values = {Double.toString(distance), Double.toString(speed)};
		EditDialog f = new EditDialog(labels, values);
		
		//Set distance to first textboxe's value.
		distance = Double.parseDouble(values[0]);
		
		//If the speed is less than or equal to one, set the speed to the second textboxe's value.
		if(Math.abs(Double.parseDouble(values[1])) <= 1.0)
			speed = Double.parseDouble(values[1]);
	}

	@Override
	public void execute() throws NullPointerException {
		//Set the c factor.
		cFactor = speed/20.0;

		//Reset the gyro.
		gyro.reset();
		//Loop until the desired distance is travelled.
		while(/*voltageToDistance(ultrasonic.getVoltage()) < distance &&*/ !robot.isDisabled() && robot.isAutonomous()) {
			//Speed of the left and right wheels.
			double lspeed = speed, rspeed = speed;
			
			//If the gyro's angle is less than zero, change the speed of the right wheel.
			if(gyro.getAngle() < 0) {
				rspeed -= Math.abs(gyro.getAngle()) * cFactor;
				if(rspeed < 0)
					rspeed = 0;

			}
			//If the gyro's angle is more than zero, change the speed of the left wheel.
			else if(gyro.getAngle() > 0) {
				lspeed -= Math.abs(gyro.getAngle()) * cFactor;
				if(rspeed < 0)
					rspeed = 0;

			}
			//Set the wheel speeds.
			robot.setLeftSide(lspeed);
			robot.setRightSide(rspeed);
			
			SmartDashboard.putString("DB/String 6", Double.toString(voltageToDistance(ultrasonic.getVoltage())));
			SmartDashboard.putString("DB/String 5", Double.toString(ultrasonic.getVoltage()));

		}
	}
	
	public double voltageToDistance(double voltage) {
		//Convert voltage returned by the ultrasonic to distance in inches.
		//http://www.maxbotix.com/documents/LV-MaxSonar-EZ_Datasheet.pdf
		//http://www.maxbotix.com/documents/MB7001_Datasheet.pdf
		return voltage / 5.0 / 512.0;
		//IN CASE THIS IS WRONG: http://www.chiefdelphi.com/forums/showthread.php?t=103028
	}

	@Override
	public void setRobot(VisualRobot r) {
		//Set the robot, ultrasonic, and gyro variables.
		robot = r;
		ultrasonic = (AnalogInput)r.getSensors().get("ultrasonic");
		gyro = (AnalogGyro) robot.getSensors().get("gyro");
		SmartDashboard.putString("DB/String 9", Boolean.toString((gyro != null)));
		SmartDashboard.putString("DB/String 8", Boolean.toString((ultrasonic != null)));

	}

	public MoveUntilProximityCommand() {
	}
	
	public MoveUntilProximityCommand(double s, double d) {
		speed = s;
		distance = d;
	}
	
	public MoveUntilProximityCommand(double s, double d, VisualRobot r) {
		this(s, d);
		setRobot(r);
	}
	
}
