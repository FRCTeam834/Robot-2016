package Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import base.Command;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TestBoard extends VisualRobot{
	
	public AnalogGyro gyro = new AnalogGyro(0);
	//public Ultrasonic distanceSensor = new UltraSonic();
	public Encoder rightEncoder = new Encoder(0,1);
	public Encoder leftEncoder = new Encoder(2,3);
	
	Relay lights = new Relay(0); //turns on LEDs

	Talon motor1 = new Talon(0); //Left back
	Talon motor2 = new Talon(1); //left forward
	Talon motor3 = new Talon(2); //right back
	Talon motor4 = new Talon(3); //right forward 
	Talon motor5 = new Talon(4); //other
	TalonSRX motor6 = new TalonSRX(2); //Extra
	
	RobotDrive robot = new RobotDrive(motor2, motor1, motor4, motor3);
	
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);
	Joystick xbox = new Joystick(2);
	
	Solenoid open = new Solenoid(1);
	Solenoid close = new Solenoid(0);
	
	HashMap<String, SensorBase> sensors = new HashMap<>();
	ArrayList<Command> commands = new ArrayList<Command>();

	public TestBoard() {
		sensors.put("rightEncoder", rightEncoder);
		sensors.put("leftEncoder", leftEncoder);
		sensors.put("gyro", gyro);
		//sensors.put("ultrasonic", distanceSensor);
		
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

		gyro.initGyro();
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
		motor5.set(speed);
		
	}


	public void autonomous() {
		
	}

	

	
	public void teleOpInit() {
	}

	public void teleOpPeriodic() {
		robot.tankDrive(leftJoystick, rightJoystick);
		SmartDashboard.putString("DB/String 1", Double.toHexString(rightEncoder.get()));
		SmartDashboard.putString("DB/String 1", Double.toHexString(leftEncoder.get()));
		SmartDashboard.putString("DB/String 1", Double.toHexString(gyro.getAngle()));
		if(xbox.getRawButton(1));
		
	}

}
