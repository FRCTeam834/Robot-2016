package org.usfirst.frc.team834.robot;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import base.Command;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Robot extends VisualRobot{
	
	private AnalogGyro gyro = new AnalogGyro(0);
	private AnalogInput distanceSensor = new AnalogInput(2);
	private Encoder rightEncoder = new Encoder(0,1);
	private Encoder leftEncoder = new Encoder(2,3);
	
	Relay lights = new Relay(0); //turns on LEDs

	Compressor compressor = new Compressor(1);
	
	Talon motor1 = new Talon(0); //Left back
	Talon motor2 = new Talon(1); //left forward
	Talon motor3 = new Talon(2); //right back
	Talon motor4 = new Talon(3); //right forward 
	Talon motor5 = new Talon(4); //other
	//TalonSRX motor6 = new TalonSRX(2); //Extra
	
	RobotDrive robot = new RobotDrive(motor2, motor1, motor4, motor3);
	
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);
	Joystick xbox = new Joystick(2);
	
	Solenoid open = new Solenoid(1, 1);
	Solenoid close = new Solenoid(1, 0);
	
	DigitalInput camera = new DigitalInput(9);
	
	Image image;
	int session1;
	int session2;
	
	HashMap<String, SensorBase> sensors = new HashMap<>();
	ArrayList<Command> commands = new ArrayList<Command>();

	boolean togglePneumatics = true;
	public Robot() {
		super();
		sensors.put("rightEncoder", rightEncoder);
		sensors.put("leftEncoder", leftEncoder);
		sensors.put("gyro", gyro);
		sensors.put("ultrasonic", distanceSensor);
		
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		
        session1 = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        session2 = NIVision.IMAQdxOpenCamera("cam1",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);

        NIVision.IMAQdxConfigureGrab(session1);
        NIVision.IMAQdxConfigureGrab(session2);

        
		File f = new File("/home/lvuser/auton.aut");
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

		compressor.start();
		rightEncoder.setDistancePerPulse(1.0/400.0);
		leftEncoder.setDistancePerPulse(1.0/400.0);
		
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
		int i = 0;
		while(isAutonomous() && !isDisabled() && i < commands.size()) {
			commands.get(i).execute();
			i++;
		}
	}
	
	public void teleOpInit() {
		close.set(true);
		open.set(false);
        NIVision.IMAQdxStartAcquisition(session1);
        NIVision.IMAQdxStartAcquisition(session2);

	}

	public void teleOpPeriodic() {
		robot.tankDrive(leftJoystick, rightJoystick);
		
		SmartDashboard.putString("DB/String 0", Double.toString(rightEncoder.getDistance()));
		SmartDashboard.putString("DB/String 1", Double.toString(leftEncoder.getDistance()));
		SmartDashboard.putString("DB/String 2", Double.toString(gyro.getAngle()));
		SmartDashboard.putString("DB/String 3", Double.toString((512/5)*distanceSensor.getVoltage()) + "Inches");
        
		
		
		NIVision.IMAQdxGrab(camera.get() ? session1 : session2, image, 1);
		CameraServer.getInstance().setImage(image);

		if(leftJoystick.getRawButton(1)) {
			SmartDashboard.putString("DB/String 4", "light on");

			lights.set(Relay.Value.kForward);
		}			
		else {
			SmartDashboard.putString("DB/String 4", "lights off");
			lights.set(Relay.Value.kOff);

		}

		if(rightJoystick.getRawButton(1)) {
			if(togglePneumatics) {
			open.set(!open.get());
			close.set(!close.get());
			}
			togglePneumatics = false;
		}			
		else{
			togglePneumatics = true;
		}
	}

}
