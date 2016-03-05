package org.usfirst.frc.team834.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import base.Command;
import commands.DelayCommand;
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
			main.add(new MoveStraightCommand(240, .8, robot));
		}
		if(id > 1) {
			ArrayList<Command> moveArms = new ArrayList<>();
			moveArms.add(new MoveFeederArmCommand(true, 120, .6, robot));
			threads = Arrays.copyOf(threads, 2);
			threads[1] = new Thread(new RunCommands(moveArms));
			threadStarts = Arrays.copyOf(threadStarts, 2);
			threadStarts[1] = 0;
			
			main.add(0, new DelayCommand(2));
		}
		
		if(id > 2) {
			main.add(new MoveToPointCommand(140, 64, .6, robot));
			main.add(new MoveFeederArmCommand(true, 120, .6, robot));
			main.add(new ShootCommand(4.0, robot));
			
			ArrayList<Command> moveArms = new ArrayList<>();
			moveArms.add(new MoveFeederArmCommand(false, 100, .6, robot));

			threads = Arrays.copyOf(threads, 3);
			threads[2] = new Thread(new RunCommands(moveArms));
			threadStarts = Arrays.copyOf(threadStarts, 3);
			threadStarts[2] = 1;

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
	 * 3: Ramparts
	 * 4: Moat
	 * 5: Rock Wall
	 * 6: Rough Terrain
	 * 
	 * For Position: Lowbar is one, towards center add one until 5
	 */
	public void chooseAuton(int obstacleID, int positionID) {
		switch(obstacleID) {
		case 1:
			main.add(new MoveBackArmCommand(true, 210, .6, robot));
			main.add(new MoveStraightCommand(140, -.6, robot));
			main.add(new TurnCommand(180, .5, robot));
			break;
		case 2:
			main.add(new MoveStraightCommand(70, -.6, robot));
			ArrayList<Command> moveArms = new ArrayList<>();
			moveArms.add(new MoveBackArmCommand(true, 150, .6, robot));
			threads[1] = new Thread(new RunCommands(moveArms));
			threadStarts = Arrays.copyOf(threadStarts, 2);
			threadStarts[1] = 0;
			
			main.add(new DelayCommand(1));
			main.add(new MoveStraightCommand(70, -.6, robot));
			break;
		default:
			main.add(new MoveStraightCommand(140, .8, robot));
		}
		
		switch(positionID) {
			
		}
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
