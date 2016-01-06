package commands;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	public class EditFrame extends JFrame {
		JLabel angle = new JLabel("Angle");
		JLabel speed = new JLabel("Speed");
		JTextField txtAngle = new JTextField(angle.toString(), 10);
		JTextField txtSpeed = new JTextField(speed.toString(), 10);
		JButton done = new JButton("Done");
		{
			
		}
	}
	
}
