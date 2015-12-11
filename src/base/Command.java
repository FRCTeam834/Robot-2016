package base;
import java.io.Serializable;

/**
 * Interface representing the actions that a robot can perform.
 * Should provide for editing and viewing as well.
 */
public interface Command extends Serializable {
	public abstract void edit();
	public abstract void execute();
}
