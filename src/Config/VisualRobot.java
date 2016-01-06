package Config;

import java.util.HashMap;

import edu.wpi.first.wpilibj.*;

public abstract class VisualRobot extends RobotBase{
	public abstract void setLeftSide(double speed);
	public abstract void setRightSide(double speed);
	public abstract void setLights(boolean on);
	public abstract void shift(boolean on);
	public abstract void stop();

	/* 
	 * IMPORTANT: Definitions for sensor keys
	 * 	"gyro"
	 * 	"rightEncoder"
	 * 	"leftEncoder"
	 * For other sensors, define by lower case name of class, followed by number
	 * ex.
	 * 	"encoder1" <- Extra encoder (perhaps for a lift) 
	 *  "ultrasonic1"
	 */
	public abstract HashMap<String, SensorBase> getSensors();
	
	
}
