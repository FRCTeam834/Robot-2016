package commands;

import org.usfirst.frc.team834.robot.VisualRobot;

import edu.wpi.first.wpilibj.DigitalInput;

public class MoveAlongCurveCommand {
	private VisualRobot robot;
	private boolean direction; //true is cw, false is ccw
	private double radius, speed, angle;
	private final double WIDTH = 69696969;
	
	public void edit() {
		String[] labels = {"Direction", "Radius", "Speed", "Angle"};
		String[] values = {Boolean.toString(direction), Double.toString(radius), Double.toString(speed), Double.toString(angle)};
		EditDialog d = new EditDialog(labels,values);		
		
		direction = Boolean.parseBoolean(values[0]);
		radius = Double.parseDouble(values[1]);
		speed = Double.parseDouble(values[2]);
		angle = Double.parseDouble(values[3]);
	}

	public void execute() throws NullPointerException {
		
	}

	public void setRobot(VisualRobot r) {
		robot = r;
	}
}
