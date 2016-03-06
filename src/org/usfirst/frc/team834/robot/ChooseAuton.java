package org.usfirst.frc.team834.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import base.Command;
import commands.DelayCommand;
import commands.MoveAlongCurveCommand;
import commands.MoveBackArmCommand;
import commands.MoveFeederArmCommand;
import commands.MoveStraightCommand;
import commands.MoveToPointCommand;
import commands.ShootCommand;
import commands.TurnCommand;

public class ChooseAuton {
	Thread[] threads = {null};
	int[] threadStarts = {0};
	ArrayList<Command> main = new ArrayList<Command>();
	VisualRobot robot;
	
	public ChooseAuton(Robot r) {
		robot = r;
	}
	
	
	/* 0: Nothing
	 * 1: Straight
	 * 2: Low Bar 
	 * 3: Low Bar Straight, Shoot
	 * 4: Cheval de Frise (?)
	 * 5: Portcullis
	 */
	public void chooseAuton(int id) {
	
		if(id > 0) {
			main.add(new MoveStraightCommand(220, .8, robot));
		}
		if(id > 1) {
			ArrayList<Command> moveArms = new ArrayList<>();
			moveArms.add(new MoveFeederArmCommand(true, 130, .6, robot));
			threads = Arrays.copyOf(threads, 2);
			threads[1] = new Thread(new RunCommands(moveArms));
			threadStarts = Arrays.copyOf(threadStarts, 2);
			threadStarts[1] = 0;
			
			main.add(0, new DelayCommand(2));
		}
		
		if(id > 2) {
			main.add(new MoveToPointCommand(140, 64, .6, robot));
//			main.add(new TurnCommand(90 - Math.atan2(140, 64) * 180 / Math.PI, .6, robot));
//			main.add(new MoveStraightCommand(Math.pow(145, .5), .6, robot));
			main.add(new MoveFeederArmCommand(true, 130, .6, robot));
			main.add(new ShootCommand(4.0, robot));
			
			ArrayList<Command> moveArms = new ArrayList<>();
//			moveArms.add(new MoveFeederArmCommand(false, 100, .6, robot));
//
//			threads = Arrays.copyOf(threads, 3);
//			threads[2] = new Thread(new RunCommands(moveArms));
//			threadStarts = Arrays.copyOf(threadStarts, 3);
//			threadStarts[2] = 3;

		}
		


	}
	
	public void chooseAuton(String fName) {
		File file = new File("/home/lvuser/" + "fName" + ".autr"); //Select file

		try {
		
			ObjectInputStream ois;
			ois = new ObjectInputStream(new FileInputStream(file));
			int numThreads = ois.readInt();
			threadStarts = new int[numThreads];
			threads = new Thread[numThreads];

			
			threadStarts[0] = ois.readInt();
			main = (ArrayList<Command>) ois.readObject();

			
			for(int thread = 1; thread < numThreads; thread++ ) {
				threadStarts[thread] = ois.readInt();
				ArrayList<Command> commands= (ArrayList<Command>) ois.readObject();
				for(Command c: commands)
					c.setRobot(robot);
				threads[thread] = new Thread(new RunCommands(commands));
			}
			
			for(Command c: main)
				c.setRobot(robot);
		}
		catch(IOException e){e.printStackTrace();} 
		catch (ClassNotFoundException e) {e.printStackTrace();}
	}
	
	/* Obstacles
	 * 1: Portcullis
	 * 2: Cheval de Frise
	 * 3: Low Bar
	 * 4: Ramparts
	 * 5: Moat
	 * 6: Rock Wall
	 * 7: Rough Terrain
	 * 8: LowBar
	 * For Position: Lowbar is one, towards center add one until 5
	 */
	public void chooseAuton(int obstacleID, int positionID) {
		switch(obstacleID) {
		case 0: 
			break;
		case 1:
			main.add(new MoveBackArmCommand(true, 150, .6, robot));
			main.add(new MoveFeederArmCommand(false, 130, .6, robot));
			main.add(new MoveStraightCommand(120, -.6, robot));
			main.add(new TurnCommand(180, .5, robot));
			break;
		case 2:
			main.add(new MoveStraightCommand(60, -.6, robot));
			ArrayList<Command> moveArms = new ArrayList<>();
			moveArms.add(new MoveBackArmCommand(true, 150, .6, robot));
			threads[1] = new Thread(new RunCommands(moveArms));
			threadStarts = Arrays.copyOf(threadStarts, 2);
			threadStarts[1] = 0;
			
			main.add(new DelayCommand(1));
			main.add(new MoveStraightCommand(60, -.6, robot));
			break;
			
		case 3: 
			main.add(new MoveFeederArmCommand(true, 150, .6, robot));
			main.add(new MoveStraightCommand(120, .6, robot));
			break;
		case 8:
			main.add(new MoveStraightCommand(220, .8, robot));

			ArrayList<Command> moveArms2 = new ArrayList<>();
			moveArms2.add(new MoveFeederArmCommand(true, 130, .6, robot));
			threads = Arrays.copyOf(threads, 2);
			threads[1] = new Thread(new RunCommands(moveArms2));
			threadStarts = Arrays.copyOf(threadStarts, 2);
			threadStarts[1] = 0;
			main.add(0, new DelayCommand(2));
			break;
		default:
			main.add(new MoveStraightCommand(120, .8, robot));
		}
		main.add(new MoveFeederArmCommand(false, 15, .6, robot));
		main.add(new MoveBackArmCommand(false, 15, .6, robot));
		main.add(new DelayCommand(.5));
		switch(positionID) {
			case 1:	
				main.add(new MoveStraightCommand(100, .8, robot));
				main.add(new MoveFeederArmCommand(true, 150, .6, robot));
				main.add(new MoveToPointCommand(140, 64, .8, robot));
				break;

			case 2:	
				main.add(new MoveStraightCommand(120, .8, robot));
				main.add(new MoveToPointCommand(70, 32, .8, robot));

				break;
			case 3:	
				main.add(new MoveStraightCommand(100, .8, robot));
				main.add(new MoveAlongCurveCommand(true, 36, .8, 330, robot));
				main.add(new MoveStraightCommand(30, .8, robot));

				break;
			case 4:	
				main.add(new MoveToPointCommand(131, 85, .8, robot));
				main.add(new TurnCommand(Math.atan(131/85) * 180/Math.PI - 90 -60, .6, robot));
				main.add(new MoveStraightCommand(72, .8, robot));
				break;
			case 5:
				main.add(new MoveStraightCommand(120, .8, robot));
				main.add(new MoveToPointCommand(35, 40, .8, robot));
		}
		
		ArrayList<Command> moveArmsDown = new ArrayList<>();
		moveArmsDown.add(new DelayCommand(2));
		moveArmsDown.add(new MoveFeederArmCommand(true, 150, .6, robot));
		threads = Arrays.copyOf(threads, threads.length + 1);
		threads[threadStarts.length-1] = new Thread(new RunCommands(moveArmsDown));
		threadStarts = Arrays.copyOf(threadStarts, threadStarts.length + 1);
		threadStarts[threadStarts.length-1] = main.size()-1;
		
		main.add(new ShootCommand(4, robot));
	}
	
	
	public int[] getThreadStarts() {
		return threadStarts;
	}
	public Thread[] getThreads() {
		return threads;
	}
	public ArrayList<Command> getMain() {
		return main;
	}
}
