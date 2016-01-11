package base;
import java.io.Serializable;

import org.usfirst.frc.team834.robot.VisualRobot;

/**
 * Interface representing the actions that a robot can perform.
 * Should provide for editing and viewing as well.
 */
public interface Command extends Serializable {
	public abstract void edit();
	public abstract void execute() throws NullPointerException;
	public void setRobot(VisualRobot r);
}
