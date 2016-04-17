package org.usfirst.frc.team834.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import base.Command;
import commands.DelayCommand;
import commands.FeederCommand;
import commands.MoveAlongCurveCommand;
import commands.MoveBackArmCommand;
import commands.MoveFeederArmCommand;
import commands.MoveStraightCommand;
import commands.MoveToPointCommand;
import commands.MoveUntilProximityCommand;
import commands.ShootCommand;
import commands.TurnCommand;
import commands.TurnToGoalCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
		main.add(new DelayCommand(1));

		main.add(new MoveStraightCommand(232.5, .9, robot));
		ArrayList<Command> moveArms = new ArrayList<>();
		moveArms.add(new MoveFeederArmCommand(true, 130, .6, robot));
		threads = Arrays.copyOf(threads, 2);
		threads[1] = new Thread(new RunCommands(moveArms));
		threadStarts = Arrays.copyOf(threadStarts, 2);
		threadStarts[1] = 0;
		
		main.add(new MoveToPointCommand(160, 70, .8, robot));
		main.add(new ShootCommand(2.0, robot));
		main.add(new MoveStraightCommand(144, -.8, robot));
		main.add(new TurnCommand(Math.atan(64.0/130.0)*(180.0*Math.PI) + 90.0, .8, robot));
		main.add(new MoveStraightCommand(180, .8, robot));
		main.add(new MoveToPointCommand(-10, 60, .8, robot));
		
		ArrayList<Command> feeder = new ArrayList<>();
		feeder.add(new FeederCommand(2, robot));
		threads = Arrays.copyOf(threads, 3);
		threads[2] = new Thread(new RunCommands(feeder));
		threadStarts = Arrays.copyOf(threadStarts, 3);
		threadStarts[2] = main.size()-1;
		
		main.add(new TurnCommand(-180, .8, robot));
		main.add(new MoveAlongCurveCommand(true, 185, .8, 72, robot));
		main.add(new MoveStraightCommand(10, .6, robot));
		main.add(new ShootCommand(2.0, robot));
		
		


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
	public void chooseAuton(int obstacleID, int positionID, boolean isLeft) {
		
		if(obstacleID==-2) {
			main.add(new TurnToGoalCommand(2, robot));
		}
		else if(obstacleID==-1) {
			chooseAuton(positionID);
			return;
		}
		
		else if(obstacleID == 0) {
			return;
		}
		else {
			switch(obstacleID) {
			case 1: //Portcullis
				main.add(new MoveBackArmCommand(true, 130, .3, robot));
				main.add(new MoveFeederArmCommand(true, 130, .5, robot));
				main.add(new MoveStraightCommand(130, -.8, robot));
				main.add(new TurnCommand(180, .5, robot));
				break;
			case 2: //Cheval de Frise DO NOT USE, Experimental
				main.add(new MoveStraightCommand(60, -.5, robot));
				ArrayList<Command> moveArms = new ArrayList<>();
				moveArms.add(new MoveBackArmCommand(true, 150, .8, robot));
				threads = Arrays.copyOf(threads, threads.length + 1);
				threads[threadStarts.length-1] = new Thread(new RunCommands(moveArms));
				threadStarts = Arrays.copyOf(threadStarts, threadStarts.length + 1);
				main.add(new DelayCommand(.5));
				threadStarts[threadStarts.length-1] = main.size()-1;

				main.add(new MoveStraightCommand(60, -.8, robot));
				break;
				
			case 3: //Low Bar
				main.add(new MoveFeederArmCommand(true, 130, .5, robot));
				main.add(new MoveStraightCommand(120, .8, robot));
				break;
			default: //Anything else
				main.add(new MoveStraightCommand(130, .8, robot));
			}
			switch(positionID) {
			case 0:
				return;
			case 1:	
//				main.add(new MoveUntilProximityCommand(64, .8, robot));
				main.add(new MoveStraightCommand(100, .8, robot));
//				main.add(new MoveFeederArmCommand(true, 150, .4, robot));
				main.add(new MoveToPointCommand(140, 67.5, .8, robot));
				break;

			case 2:	
				if(isLeft) {
	//				main.add(new MoveUntilProximityCommand(32, .8, robot));
					main.add(new MoveStraightCommand(130, .8, robot));
					main.add(new MoveToPointCommand(70, 36, .8, robot));
				}
				else {
					main.add(new MoveToPointCommand(180.2, 135, .8, robot));
					main.add(new TurnCommand(-42.43256595, .6, robot));
					main.add(new MoveToPointCommand(-60.5236, 36, .8, robot));
				}
				break;
			case 3:	
				if(isLeft) {
					main.add(new MoveStraightCommand(100, .8, robot));
					main.add(new MoveAlongCurveCommand(true, 36, .6, 330, robot));
					main.add(new MoveStraightCommand(30, .8, robot));
				}
				else {
					main.add(new MoveToPointCommand(118.6, 130, .8, robot));
					main.add(new TurnCommand(-30, .6, robot));
					main.add(new MoveToPointCommand(-52.345, 33, .8, robot));
				}
				break;
			case 4:	
				main.add(new MoveToPointCommand(60, 136, .8, robot));
				main.add(new TurnCommand(-Math.atan(60.0/136.0)*(180.0/Math.PI) -63, .6, robot));
				main.add(new MoveStraightCommand(60, .8, robot));
				break;
			case 5:
				main.add(new MoveStraightCommand(147, .8, robot));
				main.add(new MoveToPointCommand(-35, 20, .8, robot));
				break;
		}
		if(positionID >=1 && positionID <= 5) {
			ArrayList<Command> moveArmsDown = new ArrayList<>();
			moveArmsDown.add(new MoveFeederArmCommand(true, 150, .5, robot));
			threads = Arrays.copyOf(threads, threads.length + 1);
			threads[threads.length-1] = new Thread(new RunCommands(moveArmsDown));
			
			threadStarts = Arrays.copyOf(threadStarts, threadStarts.length + 1);
			threadStarts[threadStarts.length-1] = main.size()-1;
			main.add(new ShootCommand(4, robot));
		}

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
	
	/*
	 * Test choosing;
	 */
	public static void main(String[] args) {
		ChooseAuton c = new ChooseAuton(null);
		Scanner in = new Scanner(System.in);
		int obsId = in.nextInt();
		int posId = in.nextInt();
		
		c.chooseAuton(obsId, posId, false);
		
		ArrayList<Command> main = c.getMain();
		int[] threadStarts = c.getThreadStarts();
		Thread[] threads = c.getThreads();
		
		System.out.println("Main: " + main.size());
		System.out.println("threadStarts: " + threadStarts.length);
		System.out.println("threads: " + threads.length + "\nCommands in Main: ");
		
		for(Command com: main) {
			System.out.println("\t" + com.getClass().toString());
		}
		System.out.println("Thread Starts");

		for(int start: threadStarts) {
			System.out.print(start + " ");
		}
		System.out.println();

		for(Thread t: threads) {
			System.out.println(t==null);
		}
		
		int i = 0;
		while(i < main.size()) {
			try {
				for(int start = 1; start < threadStarts.length; start++){ 
					if (threadStarts[start] == i) {
						threads[start].start();
					}
				}
				main.get(i).execute();
			}
			catch(Exception e) {e.printStackTrace();}
			finally {
				i++;
			}
		}	

	}
}
