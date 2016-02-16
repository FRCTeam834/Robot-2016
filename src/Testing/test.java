package Testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.usfirst.frc.team834.robot.RunCommands;

import base.Command;

public class test {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		try {
			File f = new File("/home/lvuser/auton.autr"); //Select file
			ObjectInputStream ois;
			ois = new ObjectInputStream(new FileInputStream("blah.autr"));
			int numThreads = ois.readInt();
			int[] threadStarts = new int[numThreads];
			Thread[] threads = new Thread[numThreads];

			
			threadStarts[0] = ois.readInt();
			ArrayList<Command> main = (ArrayList<Command>) ois.readObject();

			for(int thread = 1; thread < numThreads; thread++ ) {
				threadStarts[thread] = ois.readInt();
				threads[thread] = new Thread(new RunCommands((ArrayList<Command>) ois.readObject()));
			}
			

			int i = 0;
			while(i < main.size()) {
				try {
				main.get(i).execute();
				for(int start : threadStarts) {
					if (start == i){
						threads[i].start();
					}
						
				}
				i++;
				
				} catch(NullPointerException e){e.printStackTrace(); i++;}
			}

		} 
		catch (IOException e) {} 
		catch (ClassNotFoundException e) {} 
		
		long endTime = System.nanoTime();
		System.out.println("Took "+ (endTime - startTime)/1000000 + " ms"); 

	}
}
