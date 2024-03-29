//Robot for 2016 Stronghold.
package org.usfirst.frc.team834.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.VisionException;

import base.Command;
import commands.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Robot extends VisualRobot{
	
	private ADXRS450_Gyro robotGyro = new ADXRS450_Gyro();
	private AnalogGyro backArmGyro = new AnalogGyro(0);
	private AnalogGyro feederArmGyro = new AnalogGyro(1);
	
	private Encoder rightEncoder = new Encoder(0, 1);
	private Encoder leftEncoder = new Encoder(2,3);
	private DigitalInput lightSensor = new DigitalInput(4);
	private Encoder scissorsEncoder = new Encoder(5, 6);

	public KinectVision vision = KinectVision.INSTANCE;
	private Relay lights1; //turns on LEDs
	private Relay lights2; 
	

	int LEDCounter = 0;
	
	
	CANTalon[] motors = new CANTalon[9];
	/* 0: Front Left
	 * 1: Rear Left
	 * 2: Front Right
	 * 3: Rear Right
	 * 4: Intake
	 * 5: feeder arm
	 * 6: Back arm
	 * 7: Scissor
	 * 8: Winch
	 */
	
	RobotDrive robot;
	
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);
	Joystick xbox = new Joystick(2);
	Joystick switches = new Joystick(3);
	int[] topBtnIDs = {9, 15 };
	int[] midBtnIDs = {3, 13, 16 };
	int[] botBtnIDs = {11, 12 };
	
	HashMap<String, SensorBase> sensors = new HashMap<>();
	
//	boolean cam = false;
//	boolean toggleCam = false;

	boolean toggleFeeder = true;
	boolean feederOn = true;
			
	public void robotInit() {
		vision.init();
		vision.startDashboardFeed();
		
		
		
		sensors.put("rightEncoder", rightEncoder);
		sensors.put("leftEncoder", leftEncoder);
		sensors.put("gyro", robotGyro);
		sensors.put("backArmGyro", backArmGyro);
		sensors.put("feederArmGyro", feederArmGyro);
		sensors.put("tripwire", lightSensor);
		sensors.put("scissorsEncoder", scissorsEncoder);
		
		
		lights1 = new Relay(0); //turns on LEDs
		lights2 = new Relay(1); 
		
		for(int i = 0; i < motors.length; i++) {
			
			motors[i] = new CANTalon(i);
			if(i <= 3){
				motors[i].setInverted(true);
			}
		}
		motors[5].setInverted(true);
		motors[4].setInverted(true);
		robot = new RobotDrive(motors[0], motors[1], motors[2], motors[3]);
		robot.setSafetyEnabled(false);

		rightEncoder.setDistancePerPulse(1.0/(3.02*Math.PI * 4.0) * 313/240 * 6.4/7.0); //inches
		leftEncoder.setDistancePerPulse(1.0/(3.02*Math.PI * 4.0) * 313/240 * 6.4/7.0);
		scissorsEncoder.setDistancePerPulse(1.0/32.0);
		
		rightEncoder.reset();
		leftEncoder.reset();
		scissorsEncoder.reset();
		
		robotGyro.calibrate();
		backArmGyro.calibrate();
		feederArmGyro.calibrate();
		
		backArmGyro.initGyro();
		feederArmGyro.initGyro();
		
//		criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, .5, 100.0, 0, 0);

		
	}	

	public void setLeftSide(double speed) {
		if(speed < -1.0 || speed > 1.0)
			return;
		motors[0].set(-speed);
		motors[1].set(-speed);
	}
	
	public void setRightSide(double speed) {
		if(speed < -1.0 || speed > 1.0)
			return;
		motors[2].set(speed);
		motors[3].set(speed);
	}
	
	
	public void shift(boolean on) {
	}
	
	public void stop() {
	}
	
	public HashMap<String, SensorBase> getSensors() {
		return sensors;
	}

	
	public void autonomous() {	
		
		robotGyro.reset();
		feederArmGyro.reset();
		backArmGyro.reset();
		
		this.setFeederArm(0.0);
		
		int obstacleID = 0;
		int positionID = 0;
//		boolean leftOrRight = false;
		
		String temp1 = SmartDashboard.getString("DB/String 8", "0");
		String temp2 = SmartDashboard.getString("DB/String 9", "0");
				
		try {
			obstacleID = Integer.parseInt(temp1);
			positionID = Integer.parseInt(temp2);
		} 
		catch(NumberFormatException e) {}
		
		
		ChooseAuton c = new ChooseAuton(this);
		c.chooseAuton(obstacleID, positionID);
		
		ArrayList<Command> main = c.getMain();
		int[] threadStarts = c.getThreadStarts();
		Thread[] threads = c.getThreads();
		
		robotGyro.reset();
		feederArmGyro.reset();
		backArmGyro.reset();
		
		int i = 0;
		while(isAutonomous() && !isDisabled() && i < main.size()) {
			try {
				for(int start = 1; start < threadStarts.length; start++)
					if (threadStarts[start] == i)
						threads[start].start();
				main.get(i).execute();
			}
			catch(NullPointerException e) {SmartDashboard.putString("DB/String 5", e.getLocalizedMessage());}
			finally {
				i++;
			}
		}	
		
	}
	
	public void teleOpInit() {

	}

	public void teleOpPeriodic() {
		Timer.delay(.05);
		
		robot.tankDrive(leftJoystick, rightJoystick);


		if(leftJoystick.getMagnitude() >= .2 || rightJoystick.getMagnitude() >= .2)
			setBlueLights(true);
		else 
			setBlueLights(false);
		
		if(!lightSensor.get()) {
			setWhiteLights(true);
			LEDCounter = 0;
		}
		else {
			if(LEDCounter >= 8) {
				setWhiteLights(false);
			}
			if(LEDCounter >= 16) {
				setWhiteLights(true);
				LEDCounter = 0;
			}
			LEDCounter++;

		}
		
		
		
		if(feederOn) {
			if(xbox.getRawButton(6)) {
				motors[4].set(-.6);
			}
			else if(!lightSensor.get()) {
				motors[4].set(0.08);

			}
			else {
				motors[4].set(.8);

			}
		}
		else {
			motors[4].set(0);
		}
		
		if(xbox.getRawButton(5)) {
			if(toggleFeeder) {
				feederOn = !feederOn;
				toggleFeeder = false;
			}
		}
		else {
			toggleFeeder = true;

		}
		
		
		if(xbox.getRawButton(3)) 
			motors[5].set(.6);
		else if(xbox.getRawButton(4)) 
			motors[5].set(-.6);
		else
			motors[5].set(0);
		
		
		
		if(xbox.getRawButton(2)) 
			motors[6].set(.4);
		else if(xbox.getRawButton(1)) 
			motors[6].set(-.4);
		else
			motors[6].set(0);
		
		
		if(xbox.getRawButton(8)/* && scissorsEncoder.getDistance() >= -100*/) 
			motors[7].set(-1);
		else if(xbox.getRawButton(7) /*&& scissorsEncoder.getDistance() <= 500*/) {
			motors[7].set(1);
		}
		else{
			motors[7].set(0);
		}

//		WINCH
		if(rightJoystick.getRawButton(10)) {
			motors[8].set(-1);
		}
		else if(rightJoystick.getRawButton(11)) {
			motors[8].set(1);
		}
		else {
			motors[8].set(0);
		}
		
		SmartDashboard.putString("DB/String 0", Double.toString(scissorsEncoder.get()));
		SmartDashboard.putString("DB/String 1", Double.toString(rightEncoder.getDistance()));
		SmartDashboard.putString("DB/String 2", Double.toString(leftEncoder.getDistance()));
		SmartDashboard.putString("DB/String 3", Boolean.toString(toggleFeeder));
		System.out.println(feederArmGyro.getAngle());

//		
//		CameraServer.getInstance().setImage(image);
//
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
//		}
//		catch(VisionException e){
//		}
//		
//
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

	public void setBlueLights(boolean on) {
		lights1.set(on ? Relay.Value.kForward : Relay.Value.kOff);
	}
	public void setWhiteLights(boolean on) {
		lights2.set(on ? Relay.Value.kForward : Relay.Value.kOff);
	}
}
