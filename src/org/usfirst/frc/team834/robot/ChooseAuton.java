package org.usfirst.frc.team834.robot;

import java.util.ArrayList;
import java.util.Arrays;

import base.Command;
import commands.DelayCommand;
import commands.MoveFeederArmCommand;
import commands.MoveStraightCommand;
import commands.MoveToPointCommand;
import commands.ShootCommand;

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
	 */
	public void chooseAuton(int id) {
		if(id > 0) {
			main.add(new MoveStraightCommand(240, .8, robot));
		}
		if(id > 1) {
			ArrayList<Command> moveArms = new ArrayList<>();
			moveArms.add(new MoveFeederArmCommand(true, 120, .4, robot));
			threads = Arrays.copyOf(threads, 2);
			threads[1] = new Thread(new RunCommands(moveArms));
			threadStarts = Arrays.copyOf(threadStarts, 2);
			threadStarts[1] = 0;
			
			main.add(0, new DelayCommand(2));
		}
		
		if(id > 2) {
			main.add(new MoveToPointCommand(140, 64, .6, robot));
			main.add(new ShootCommand(4.0, robot));
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
