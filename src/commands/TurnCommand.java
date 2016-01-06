package commands;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Config.*;
import base.*;
import edu.wpi.first.wpilibj.Gyro;

public class TurnCommand implements Command {
	private double angle;
	private double speed;
	private VisualRobot robot;
	private Gyro gyro;
	public TurnCommand() {
		
	}
	
	public void setRobot(VisualRobot r) {
		robot = r;
		gyro = (Gyro) robot.getSensors().get("gyro");
	}
	
	public void edit() {
		
		String[] labels = {"Angle", "Speed"};
		String[] values = {Double.toString(angle), Double.toString(speed)};
		EditFrame f = new EditFrame(labels, values);
		
		angle = Double.parseDouble(values[0]);
		speed = Double.parseDouble(values[1]);

		
		
	}
	public void execute() {
		if(angle > 0) {
			robot.setRightSide(-speed);
			robot.setLeftSide(speed);
			while (gyro.getAngle() < angle) {
				
			}
			robot.stop();
		}
		else if(angle < 0) {
			robot.setRightSide(speed);
			robot.setLeftSide(-speed);
			while (gyro.getAngle() > angle) {
				
			}
			robot.stop();
		}
		
		
		
	}
	
	
	
}
