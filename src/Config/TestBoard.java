package Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import base.Command;
import edu.wpi.first.wpilibj.*;

public class TestBoard extends VisualRobot{
	
	public Gyro gyro = new Gyro();
	public Ultrasonic distanceSensor = new UltraSonic();
	public Encoder rightEncoder = new Encoder(,);
	public Encoder leftEncoder = new Encoder(,);

	Relay lights = new Relay(); //turns on LEDs

	Talon motor1 = new Talon(); //Left back
	Talon motor2 = new Talon(); //left forward
	Talon motor3 = new Talon(); //right back
	Talon motor4 = new Talon(); //right forward 
	Talon motor5 = new Talon(); //Extra Motor
	
	Joystick leftJoystick = new Joystick(1);
	Joystick rightJoystick = new Joystick(2);
	
	Solenoid open = new Solenoid();
	Solenoid close = new Solenoid();
	
	public TestBoard() {
		
	}
	public void startCompetition() {
		
	}

	public void operatorControl() {
		
	}
	
	public void autonomous() {
		ArrayList<Command> commands = new ArrayList<Command>();
		
		File f = new File("auton");
		try {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		commands = (ArrayList<Command>) ois.readObject();
		ois.close();
		} 
		catch(IOException e) {} 
		catch (ClassNotFoundException e) {}
		
		
		
	}
	
	public void setLeftSide(double speed) {
		// TODO Auto-generated method stub
		
	}
	
	public void setRightSide(double speed) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLights(boolean on) {
		// TODO Auto-generated method stub
		
	}
	
	public void pneumatics1(boolean on) {
		// TODO Auto-generated method stub
		
	}
	
	public void shift(boolean on) {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	public Gyro getGyro() {
		// TODO Auto-generated method stub
		return null;
	}
}
