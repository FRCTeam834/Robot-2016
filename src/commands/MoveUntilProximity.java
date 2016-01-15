package commands;

import org.usfirst.frc.team834.robot.VisualRobot;
import base.*;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MoveUntilProximity implements Command {
	private VisualRobot robot;
	private double speed, distance, cFactor;
	private AnalogInput ultrasonic; 
	private AnalogGyro gyro;
	
	@Override
	public void edit() {
		String[] labels = {"Distance", "Speed"};
		String[] values = {Double.toString(distance), Double.toString(speed)};
		EditDialog f = new EditDialog(labels, values);
		
		distance = Double.parseDouble(values[0]);
		
		if(Math.abs(Double.parseDouble(values[1])) <= 1.0) {
			speed = Double.parseDouble(values[1]);
		}
	}

	@Override
	public void execute() throws NullPointerException {
		cFactor = speed / 45.00000001;
		
		gyro.reset();
		
		while(voltageToDistance(ultrasonic.getVoltage()) < distance && !robot.isDisabled() && robot.isAutonomous()) {
			double rspeed = speed;
			double lspeed = speed;
			
			if(gyro.getAngle() < 0)
				rspeed -= Math.abs(gyro.getAngle()) / cFactor;
			else if(gyro.getAngle() > 0)
				lspeed -= Math.abs(gyro.getAngle()) / cFactor;
			
			robot.setLeftSide(lspeed);
			robot.setRightSide(rspeed);
		}
	}
	
	public double voltageToDistance(double voltage) {
		//http://www.maxbotix.com/documents/LV-MaxSonar-EZ_Datasheet.pdf
		return ((5 / 512) * distance) * voltage;
	}

	@Override
	public void setRobot(VisualRobot r) {
		robot = r;
		ultrasonic = (AnalogInput)r.getSensors().get("ultrasonic");
		gyro = (AnalogGyro) robot.getSensors().get("gyro");
	}

}
