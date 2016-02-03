// Authors: 
// Last Edited: 1/15/2016
// Description: Command used for moving the robot straight forward.

package commands;

import org.usfirst.frc.team834.robot.VisualRobot;
import base.Command;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MoveStraightCommand implements Command {
	//Variable to represent the robot.
	private VisualRobot robot;
	//Left and right encoders used to get distance travelled by the wheels.
	private Encoder LEncoder, REncoder;
	//Gyro variable used for getting the rotation of the robot.
	private AnalogGyro gyro;
	
	//The speed the robot should move, the distance it should travel, and a c factor value.
	private double speed, distance, cFactor;
	
	public void edit() {
		//Set labels and textbox default values for the command edit menu.
		String[] labels = {"Distance", "Speed"};
		String[] values = {Double.toString(distance), Double.toString(speed)};
		EditDialog f = new EditDialog(labels, values);
		
		//Set distance to the first textboxe's value.
		distance = Double.parseDouble(values[0]);
		
		//If speed is less than or equal to one, set speed to the second textboxe's value.
		if(Math.abs(Double.parseDouble(values[1])) <= 1.0)
			speed = Double.parseDouble(values[1]);
	}

	public void execute() throws NullPointerException {
		//Reset encoders and gyro.
		REncoder.reset();
		LEncoder.reset();
		gyro.reset();
		cFactor = speed / 45.0;

		//While loop to end once the desired distance is travelled.
		while((REncoder.getDistance() + LEncoder.getDistance()) / 2 < distance && robot.isAutonomous() && !robot.isDisabled()) {
			//Speed of left and right wheels.
			double lspeed = speed, rspeed = speed;
			
			//If the gyro's angle is less than zero, change the right wheel's speed.
			if(gyro.getAngle() < 0){
				rspeed -= Math.abs(gyro.getAngle()) * cFactor;
			//If the gyro's angle is more than zero, change the left wheel's speed.
				SmartDashboard.putString("DB/String 8", Double.toString(gyro.getAngle() * cFactor));

				if(rspeed < 0)
					rspeed = 0;

			}
			else if(gyro.getAngle() > 0) {
				lspeed -= Math.abs(gyro.getAngle()) * cFactor;
				SmartDashboard.putString("DB/String 8", Double.toString(gyro.getAngle() * cFactor));

				if(lspeed < 0)
					lspeed = 0;
			}
			//Set the left and right wheel speeds.
			robot.setLeftSide(lspeed);
			robot.setRightSide(rspeed);
			
			SmartDashboard.putString("DB/String 0", Double.toString(REncoder.getDistance()));
			SmartDashboard.putString("DB/String 1", Double.toString(LEncoder.getDistance()));
			SmartDashboard.putString("DB/String 2", Double.toString(gyro.getAngle()));

		}
	}
	
	public void setRobot(VisualRobot r) {
		//Initialize robot variable, gyro variable, and encoder variables.
		robot = r;
		gyro = (AnalogGyro) robot.getSensors().get("gyro");
		LEncoder = (Encoder) robot.getSensors().get("leftEncoder");
		REncoder = (Encoder) robot.getSensors().get("rightEncoder");
	}
	
	public MoveStraightCommand(double s, double d) {
		speed = s;
		distance = d;
	}
	
	public MoveStraightCommand(double s, double d, VisualRobot r) {
		this(s, d);
		setRobot(r);
	}
}
