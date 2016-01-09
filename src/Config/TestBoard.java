package Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import base.Command;
import edu.wpi.first.wpilibj.*;

public class TestBoard extends VisualRobot{
	
	public Gyro gyro = new Gyro();
	public Ultrasonic distanceSensor = new UltraSonic();
	public Encoder rightEncoder = new Encoder(,);
	public Encoder leftEncoder = new Encoder(,);

	Relay lights = new Relay(0); //turns on LEDs

	Talon motor1 = new Talon(); //Left back
	Talon motor2 = new Talon(); //left forward
	Talon motor3 = new Talon(); //right back
	Talon motor4 = new Talon(); //right forward 
	TalonSRX motor5 = new TalonSRX(); //Inner
	
	
	Joystick leftJoystick = new Joystick(1);
	Joystick rightJoystick = new Joystick(2);
	
	Solenoid open = new Solenoid();
	Solenoid close = new Solenoid();
	
	HashMap<String, SensorBase> sensors = new HashMap<>();
	ArrayList<Command> commands = new ArrayList<Command>();

	public TestBoard() {
		sensors.put("rightEncoder", rightEncoder);
		sensors.put("leftEncoder", leftEncoder);
		sensors.put("gyro", gyro);
		sensors.put("ultrasonic", distanceSensor);
		
		File f = new File("auton");
			try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			commands = (ArrayList<Command>) ois.readObject();
			ois.close();
			for(Command c:commands) {
				c.setRobot(this);
			}
		} 
		catch(IOException e) {} 
		catch (ClassNotFoundException e) {}

	}	

	public void setLeftSide(double speed) {
		motor1.set(speed);
		motor2.set(speed);
	}
	
	public void setRightSide(double speed) {
		motor3.set(speed);
		motor4.set(speed);
	}
	
	public void setInner(double speed) {
		motor5.set(speed);
	}

	public void setLights(boolean on) {
		lights.set(on ? Relay.Value.kForward : Relay.Value.kOff);
	}
	
	public void shift(boolean on) {
		open.set(on);
		close.set(!on);

	}
	
	public void stop() {
		motor1.set(0.0);
		motor2.set(0.0);
		motor3.set(0.0);
		motor4.set(0.0);
		motor5.set(0.0);
	}
	
	public HashMap<String, SensorBase> getSensors() {
		return sensors;
	}

	
	public void setOther(double speed) {

		
	}


	public void autonomous() {
		
	}

	
	public void operatorControl() {
		// TODO Auto-generated method stub
		
	}

}
