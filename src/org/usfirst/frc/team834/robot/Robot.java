//Robot for 2016 Stronghold.
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
import com.ni.vision.VisionException;

import base.Command;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Robot extends VisualRobot{
	
	private ADXRS450_Gyro robotGyro;
	private AnalogGyro backArmGyro;
	private AnalogGyro feederArmGyro;
	
	private Encoder rightEncoder;
	private Encoder leftEncoder;
	private DigitalInput lightSensor;

	
	private Relay lights1; //turns on LEDs
	private Relay ligths2; 
	
	CANTalon[] motors = new CANTalon[8];
	/* 0: Front Left
	 * 1: Rear Left
	 * 2: Front Right
	 * 3: Rear Right
	 * 4: Intake
	 * 5: feeder arm
	 * 6: Back arm
	 * 7: Scissor
	 * 8: Winch (not there)
	 */
	Talon winch;
	
	RobotDrive robot;
	
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);
	Joystick xbox = new Joystick(2);
	Joystick buttons = new Joystick(3);
	Image image;
	int session;
	
	HashMap<String, SensorBase> sensors = new HashMap<>();

	boolean cam = false;
	boolean toggleCam = false;
	
	
	
	
	public void robotInit() {
		sensors.put("rightEncoder", rightEncoder);
		sensors.put("leftEncoder", leftEncoder);
		sensors.put("gyro", robotGyro);
		sensors.put("backArmGyro", backArmGyro);
		sensors.put("tripwire", lightSensor);
		
//		backArmGyro = new AnalogGyro(0);
//		feederArmGyro = new AnalogGyro(1);
//		
//		rightEncoder = new Encoder(0,1);
//		leftEncoder = new Encoder(2,3);
//		lightSensor = new DigitalInput(5);
//
//		lights1 = new Relay(0); //turns on LEDs
//		ligths2 = new Relay(1); 
		
		winch = new Talon(0);
		
		for(int i = 0; i < motors.length; i++) {
			motors[i] = new CANTalon(i);
		}
		
		robot = new RobotDrive(motors[0], motors[1], motors[2], motors[3]);

//		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
//		
//        session = NIVision.IMAQdxOpenCamera("cam0",
//                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//        NIVision.IMAQdxConfigureGrab(session);

//        
//		File f = new File("/home/lvuser/auton.autr");
//		try {
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
//			commands = (ArrayList<Command>) ois.readObject();
//			ois.close();
//			for(Command c:commands) {
//				c.setRobot(this);
//			}
//		}  
//		catch(IOException e) {} 
//		catch (ClassNotFoundException e) {}

//		rightEncoder.setDistancePerPulse(3.02*Math.PI); //inches
//		leftEncoder.setDistancePerPulse(3.02*Math.PI);
//		
//		robotGyro.initGyro();
//		backArmGyro.initGyro();
//		feederArmGyro.initGyro();
	}	

	public void setLeftSide(double speed) {
		if(speed < -1.0 || speed > 1.0)
			return;
		motors[0].set(speed);
		motors[1].set(speed);
	}
	
	public void setRightSide(double speed) {
		if(speed < -1.0 || speed > 1.0)
			return;
		motors[2].set(speed);
		motors[3].set(speed);
	}
	
	//public void setLights(boolean on) {
	//	lights.set(on ? Relay.Value.kForward : Relay.Value.kOff);
	//}
	
	public void shift(boolean on) {
	}
	
	public void stop() {
	}
	
	public HashMap<String, SensorBase> getSensors() {
		return sensors;
	}

	
	public void autonomous() {
		try {
			File f = new File("/home/lvuser/blah.autr"); //Select file
			ObjectInputStream ois;
			ois = new ObjectInputStream(new FileInputStream(f));
			int numThreads = ois.readInt();
			int[] threadStarts = new int[numThreads];
			Thread[] threads = new Thread[numThreads];

			
			threadStarts[0] = ois.readInt();
			ArrayList<Command> main = (ArrayList<Command>) ois.readObject();

			
			for(int thread = 1; thread < numThreads; thread++ ) {
				threadStarts[thread] = ois.readInt();
				ArrayList<Command> commands= (ArrayList<Command>) ois.readObject();
				for(Command c: commands)
					c.setRobot(this);
				threads[thread] = new Thread(new RunCommands(commands));
			}
			
			for(Command c: main)
				c.setRobot(this);


			
			
			int i = 0;
			while(isAutonomous() && !isDisabled() && i < main.size()) {
				try {
					for(int start = 1; start < threadStarts.length; start++)
						if (threadStarts[start] == i)
							threads[i].start();
					main.get(i).execute();
					i++;
				}
				catch(NullPointerException e) {}
			}
			
			//Starts any other threads
			for(int start = 1; start < threadStarts.length; start++) {
				if (threadStarts[start] >= i){
					threads[i].start();
				}
					
			}

		} 
		catch (IOException e) {SmartDashboard.putString("DB/String 0", e.toString());} 
		catch (ClassNotFoundException e) {} 
		
		
	}
	
	public void teleOpInit() {
//		robotGyro.reset();
//		NIVision.IMAQdxStartAcquisition(session);

	}

	public void teleOpPeriodic() {
		robot.tankDrive(leftJoystick, rightJoystick);

		if(xbox.getRawButton(1)) 
			motors[4].set(1);
		else if(xbox.getRawButton(2)) 
			motors[4].set(-1);
		else{
			motors[4].set(0);
		}
		
		if(xbox.getRawButton(3)) 
			motors[5].set(.2);
		else if(xbox.getRawButton(4)) 
			motors[5].set(-.2);
		else{
			motors[5].set(0);
		}
		
		
		if(xbox.getRawButton(5)) 
			motors[6].set(.8);
		else if(xbox.getRawButton(6)) 
			motors[6].set(-.8);
		else{
			motors[6].set(0);
		}
		
		if(rightJoystick.getRawButton(1)) 
			motors[7].set(1);
		else if(rightJoystick.getRawButton(2)) 
			motors[7].set(1);
		else{
			motors[7].set(0);
		}

		
//		SmartDashboard.putString("DB/String 0", Double.toString(rightEncoder.getDistance()));
//		SmartDashboard.putString("DB/String 1", Double.toString(leftEncoder.getDistance()));
//		SmartDashboard.putString("DB/String 2", Double.toString(robotGyro.getAngle()));
//		SmartDashboard.putString("DB/String 5", Boolean.toString(lightSensor.get()));
		
//		try{
//			NIVision.IMAQdxGrab(session, image, 1);
//		}
//		catch(VisionException e){
//		}
//		CameraServer.getInstance().setImage(image);

//		if(rightJoystick.getRawButton(2)) {
//			if(toggleCam) {
//				if(cam) {	
//					Thread t = new Thread(new Runnable() {
//						public void run() {		
//							NIVision.IMAQdxCloseCamera(session);
//					        session = NIVision.IMAQdxOpenCamera("cam1",
//					                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//					        NIVision.IMAQdxConfigureGrab(session);
//						}
//						
//					});
//			        t.start();
//				}
//				else {
//					Thread t = new Thread(new Runnable() {
//						public void run() {		
//							NIVision.IMAQdxCloseCamera(session);
//					        session = NIVision.IMAQdxOpenCamera("cam0",
//					                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//					        NIVision.IMAQdxConfigureGrab(session);
//						}
//						
//					});
//			        t.start();
//				}
//				cam = !cam;
//			}
//			toggleCam = false;
//		}
//		else {
//			toggleCam = true;
//		}
		
	}

	public void setIntake(double speed)
	{
		motors[4].set(speed);
	}
    public void setFeederArm(double speed)
	{
		motors[5].set(speed);
	}
	public void setBackArm(double speed)
	{
		motors[6].set(speed);
	}
	public void setScissors(double speed)
	{
		motors[7].set(speed);
	}
	public void setWinch(double speed)
	{
		motors[8].set(speed);
	}

	public void setBlueLights(boolean on) {
		lights1.set(on ? Relay.Value.kForward : Relay.Value.kOff);
	}
	public void setWhiteLights(boolean on) {
		lights1.set(on ? Relay.Value.kForward : Relay.Value.kOff);
	}
	public void checkIntake() {
		if(lightSensor.get()) {
			motors[4].set(0.0);
		}
		if(!lightSensor.get()) {
			motors[4].set(1.0);
		}

	}
}
