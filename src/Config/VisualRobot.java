package Config;

import edu.wpi.first.wpilibj.*;

public abstract class VisualRobot extends RobotBase{
	public abstract void setLeftSide(double speed);
	public abstract void setRightSide(double speed);
	public abstract void setLights(boolean on);
	public abstract void pneumatics1(boolean on);
	public abstract void shift(boolean on);
	public abstract void stop();

	public abstract Gyro getGyro();
	
	
}
