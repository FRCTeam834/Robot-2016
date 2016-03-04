//Robot for 2016 Stronghold.
package org.usfirst.frc.team834.robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import org.usfirst.frc.team834.robot.Robot.ParticleReport;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Range;
import com.ni.vision.VisionException;

import base.Command;
import commands.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Robot extends VisualRobot{
	
	public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport>{
		double PercentAreaToImageArea;
		double Area;
		double ConvexHullArea;
		double BoundingRectLeft;
		double BoundingRectTop;
		double BoundingRectRight;
		double BoundingRectBottom;
		
		public int compareTo(ParticleReport r)
		{
			return (int)(r.Area - this.Area);
		}
		
		public int compare(ParticleReport r1, ParticleReport r2)
		{
			return (int)(r1.Area - r2.Area);
		}
	};
	
	private ADXRS450_Gyro robotGyro = new ADXRS450_Gyro();
	private AnalogGyro backArmGyro = new AnalogGyro(0);
	private AnalogGyro feederArmGyro = new AnalogGyro(1);
	
	private Encoder rightEncoder = new Encoder(0, 1);
	private Encoder leftEncoder = new Encoder(2,3);
	private DigitalInput lightSensor = new DigitalInput(4);
	private Encoder scissorsEncoder = new Encoder(5, 6);

	
	private Relay lights1; //turns on LEDs
	private Relay lights2; 
	
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
	Joystick buttons = new Joystick(3);
	
	HashMap<String, SensorBase> sensors = new HashMap<>();

	
	//Vision Stuff added
	boolean cam = false;
	boolean toggleCam = false;
	Image image;
	Image binaryImage;
	int session;

	NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);

	NIVision.Range HUE_RANGE = new NIVision.Range(128, 170);
	NIVision.Range SAT_RANGE = new NIVision.Range(128, 256);
	NIVision.Range VAL_RANGE = new NIVision.Range(128, 256);

	public void robotInit() {
		
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
		robot = new RobotDrive(motors[0], motors[1], motors[2], motors[3]);
		robot.setSafetyEnabled(false);


		rightEncoder.setDistancePerPulse(1.0/(3.02*Math.PI * 4.0)); //inches
		leftEncoder.setDistancePerPulse(1.0/(3.02*Math.PI * 4.0));
		scissorsEncoder.setDistancePerPulse(1.0/32.0);
		
		rightEncoder.reset();
		leftEncoder.reset();
		scissorsEncoder.reset();
		
		robotGyro.calibrate();
		backArmGyro.calibrate();
		feederArmGyro.calibrate();
		
		backArmGyro.initGyro();
		feederArmGyro.initGyro();
		
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		binaryImage = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 0);
		
        session = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
		criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, .5, 100.0, 0, 0);

		NIVision.Range HUE_RANGE = new NIVision.Range(250, 10);
		
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
  //			ObjectInputStream ois;
//			ois = new ObjectInputStream(new FileInputStream(f));
//			int numThreads = ois.readInt();
//			int[] threadStarts = new int[numThreads];
//			Thread[] threads = new Thread[numThreads];
//
//			
//			threadStarts[0] = ois.readInt();
//			ArrayList<Command> main = (ArrayList<Command>) ois.readObject();
//
//			
//			for(int thread = 1; thread < numThreads; thread++ ) {
//				threadStarts[thread] = ois.readInt();
//				ArrayList<Command> commands= (ArrayList<Command>) ois.readObject();
//				for(Command c: commands)
//					c.setRobot(this);
//				threads[thread] = new Thread(new RunCommands(commands));
//			}
//			
//			for(Command c: main)
//				c.setRobot(this);

			ArrayList<Command> main = new ArrayList<>();
			main.add(new DelayCommand(5));
			main.add(new MoveStraightCommand(80, .3, this));
			main.add(new TurnCommand(90, .3, this));
			main.add(new MoveToPointCommand(-40, 80, .3, this));
			
//			ArrayList<Command> arms = new ArrayList<>();
//			arms.add(new MoveBackArmCommand(true, 90, 1.0, this));
//			arms.add(new MoveFeederArmCommand(true, 90, .4, this));
//			arms.add(new MoveBackArmCommand(false, 0, 1.0, this));
//			arms.add(new MoveFeederArmCommand(false, 0, .4, this));
//
//			ArrayList<Command> feederAndLights = new ArrayList<>();
//			feederAndLights.add(new FeederCommand(6, this));
// 			feederAndLights.add(new LightsCommand(true, this));
// 			feederAndLights.add(new DelayCommand(1));
// 			feederAndLights.add(new LightsCommand(false, this));
// 			feederAndLights.add(new DelayCommand(1));
// 			feederAndLights.add(new LightsCommand(true, this));
// 			feederAndLights.add(new DelayCommand(1));
// 			feederAndLights.add(new LightsCommand(false, this));
// 			feederAndLights.add(new DelayCommand(1));
// 			feederAndLights.add(new LightsCommand(true, this));
// 			feederAndLights.add(new DelayCommand(1));
// 			feederAndLights.add(new LightsCommand(false, this));


//			
//			int[] threadStarts = {0, 2, 1};
//			Thread[] threads = {null, new Thread(new RunCommands(arms)), new Thread(new RunCommands(feederAndLights))};
//			
			int i = 0;
			while(isAutonomous() && !isDisabled() && i < main.size()) {
//				try {
//					for(int start = 1; start < threadStarts.length; start++)
//						if (threadStarts[start] == i)
//							threads[i].start();
//					main.get(i).execute();
//					i++;
//				}
//				catch(NullPointerException e) {}
				main.get(i).execute();
				i++;
			}
			
//			//Starts any other threads
//			for(int start = 1; start < threadStarts.length; start++) {
//				if (threadStarts[start] >= i){
//					threads[i].start();
//				}
//					
//			}

		
		
	}
	
	public void teleOpInit() {
		robotGyro.reset();;
		feederArmGyro.reset();
		backArmGyro.reset();

	}

	public void teleOpPeriodic() {
		Timer.delay(.005);

		
		robot.tankDrive(leftJoystick, rightJoystick);

		if(xbox.getRawButton(6) || xbox.getRawButton(5)) 
			motors[4].set(-1);
		else if(!lightSensor.get()) 
			motors[4].set(0);
		else
			motors[4].set(1);

		
		
		if(xbox.getRawButton(3)) 
			motors[5].set(.4);
		else if(xbox.getRawButton(4)) 
			motors[5].set(-.4);
		else
			motors[5].set(0);
		
		
		
		if(xbox.getRawButton(2)) 
			motors[6].set(.3);
		else if(xbox.getRawButton(1)) 
			motors[6].set(-.3);
		else
			motors[6].set(0);
		
		
		if(xbox.getRawButton(7) && scissorsEncoder.getDistance() >= -1) 
			motors[7].set(1);
		else if(xbox.getRawButton(8) && scissorsEncoder.getDistance() <= 400) 
			motors[7].set(-1);
		else{
			motors[7].set(0);
		}

		if(rightJoystick.getRawButton(10)) {
			motors[8].set(-1);
		}
		else if(rightJoystick.getRawButton(11)) {
			motors[8].set(1);
		}
		else {
			motors[8].set(0);
		}
		
		
		SmartDashboard.putString("DB/String 0", Double.toString(rightEncoder.getDistance()));
		SmartDashboard.putString("DB/String 1", Double.toString(leftEncoder.getDistance()));
		SmartDashboard.putString("DB/String 2", Double.toString(robotGyro.getAngle()));
		SmartDashboard.putString("DB/String 3", Double.toString(feederArmGyro.getAngle()));
		SmartDashboard.putString("DB/String 4", Double.toString(backArmGyro.getAngle()));
		SmartDashboard.putString("DB/String 5", "Light Sensor: " + Boolean.toString(lightSensor.get()));	
		SmartDashboard.putString("DB/String 6", Double.toString(scissorsEncoder.getDistance()));

		try{
			NIVision.IMAQdxGrab(session, image, 1);
		}
		catch(VisionException e){
		}
		NIVision.imaqColorThreshold(binaryImage, image, 255, NIVision.ColorMode.HSV, HUE_RANGE, SAT_RANGE, VAL_RANGE);

		int numParticles = NIVision.imaqCountParticles(binaryImage, 1);
		SmartDashboard.putNumber("Masked particles", numParticles);
		
		
		float areaMin = (float)SmartDashboard.getNumber("Area min %", .5);
		criteria[0].lower = areaMin;
		numParticles = NIVision.imaqCountParticles(binaryImage, 1);
		SmartDashboard.putNumber("Filtered particles", numParticles);
		
		Vector<ParticleReport> particles = new Vector<ParticleReport>();
		for(int particleIndex = 0; particleIndex < numParticles; particleIndex++)
		{
			ParticleReport par = new ParticleReport();
			par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
			par.Area = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
			par.ConvexHullArea = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
			par.BoundingRectTop = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
			par.BoundingRectLeft = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
			par.BoundingRectBottom = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
			par.BoundingRectRight = NIVision.imaqMeasureParticle(binaryImage, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
			particles.add(par);
		}
		particles.sort(null);
		double areaToConvexHullArea = ConvexHullAreaScore(particles.elementAt(0));
		SmartDashboard.putNumber("Convex Hull Area", areaToConvexHullArea);

		
		
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
	
	double ratioToScore(double ratio)
	{
		return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
	}

	double ConvexHullAreaScore(ParticleReport report)
	{
		return ratioToScore((report.Area/report.ConvexHullArea)*1.18);
	}

}
