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
	
	private AnalogGyro gyro = new AnalogGyro(0);
	private AnalogInput distanceSensor = new AnalogInput(2);
	private Encoder rightEncoder = new Encoder(0,1);
	private Encoder leftEncoder = new Encoder(2,3);
	private Encoder backArmEncoder = new Encoder(4, 5);
	private DigitalInput topArmInput = new DigitalInput(6);
	private DigitalInput bottomArmInput = new DigitalInput(7);
	
	Relay lights1 = new Relay(0); //turns on LEDs
	Relay ligths2 = new Relay(1); 
	
	CANTalon[] motors = new CANTalon[9];
	/* 0: Front Left
	 * 1: Rear Left
	 * 2: Front Right
	 * 3: Rear Right
	 * 4: Intake treads
	 * 5: Intake arm
	 * 6: Back arm
	 * 7: Scissor
	 * 8: Winch
	 */
	
	RobotDrive robot = new RobotDrive(motors[0], motors[1], motors[2], motors[3]);
	
	Joystick leftJoystick = new Joystick(0);
	Joystick rightJoystick = new Joystick(1);
	Joystick xbox = new Joystick(2);
		
	Image image;
	int session;
	
	DigitalInput lightSensor = new DigitalInput(8);

	HashMap<String, SensorBase> sensors = new HashMap<>();
	ArrayList<Command> commands = new ArrayList<Command>();

	boolean cam = false;
	
	boolean toggleCam = false;
	
	public Robot() {
		super();
		sensors.put("rightEncoder", rightEncoder);
		sensors.put("leftEncoder", leftEncoder);
		sensors.put("gyro", gyro);
		sensors.put("ultrasonic", distanceSensor);
		sensors.put("topArmInput", topArmInput);
		sensors.put("bottomArmInput", bottomArmInput);
		
		for(int i = 0; i < motors.length; i++)
			motors[i] = new CANTalon(i);
		
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		
        session = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);

        
		File f = new File("/home/lvuser/auton.autr");
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

		rightEncoder.setDistancePerPulse(1.0/400.0);
		leftEncoder.setDistancePerPulse(1.0/400.0);
		
		gyro.initGyro();
	}	

	public void setLeftSide(double speed) {
		if(speed < -1 || speed > 1)
			return;
		motors[0].set(speed);
		motors[1].set(speed);
	}
	
	public void setRightSide(double speed) {
		if(speed < -1 || speed > 1)
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
		for(CANTalon m: motors) {
			m.set(0.0);
		}
	}
	
	public HashMap<String, SensorBase> getSensors() {
		return sensors;
	}

	
	public void autonomous() {
		int i = 0;
		try{
			while(isAutonomous() && !isDisabled() && i < commands.size()) {
				commands.get(i).execute();
				i++;
			}
		}
		catch(NullPointerException e) {
			SmartDashboard.putString("DB/String 7", "ERROR");
		}
	}
	
	public void teleOpInit() {
		gyro.reset();
        NIVision.IMAQdxStartAcquisition(session);

	}

	public void teleOpPeriodic() {
		robot.tankDrive(leftJoystick, rightJoystick);
		
		SmartDashboard.putString("DB/String 0", Double.toString(rightEncoder.getDistance()));
		SmartDashboard.putString("DB/String 1", Double.toString(leftEncoder.getDistance()));
		SmartDashboard.putString("DB/String 2", Double.toString(gyro.getAngle()));
		SmartDashboard.putString("DB/String 3", Double.toString(distanceSensor.getVoltage() * 0.1024) + " Inches");
		SmartDashboard.putString("DB/String 5", Boolean.toString(lightSensor.get()));
		setLights(lightSensor.get());
		
		try{
		NIVision.IMAQdxGrab(session, image, 1);
		}
		catch(VisionException e){
		}
		CameraServer.getInstance().setImage(image);

		if(rightJoystick.getRawButton(2)) {
			if(toggleCam) {
				if(cam) {	
					Thread t = new Thread(new Runnable() {
						public void run() {		
							NIVision.IMAQdxCloseCamera(session);
					        session = NIVision.IMAQdxOpenCamera("cam1",
					                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
					        NIVision.IMAQdxConfigureGrab(session);
						}
						
					});
			        t.start();
				}
				else {
					Thread t = new Thread(new Runnable() {
						public void run() {		
							NIVision.IMAQdxCloseCamera(session);
					        session = NIVision.IMAQdxOpenCamera("cam0",
					                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
					        NIVision.IMAQdxConfigureGrab(session);
						}
						
					});
			        t.start();
				}
				cam = !cam;
			}
			toggleCam = false;
		}
		else {
			toggleCam = true;
		}
		
	}

	public void setTreads(double speed)
	{
		motors[4].set(speed);
	}
    public void setArm(double speed)
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
	public boolean isDisabled()
	{
		return isDisabled();
	}

	@Override
	public void setLights(boolean on) {
		// TODO Auto-generated method stub
		
	}
}
