package Testing;

import base.*;

import java.io.File;

import javax.swing.JFrame;

public class Test {
	public static void main(String[] args) {
		BuildAnAuton x = new BuildAnAuton();
		x.pack();
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.setVisible(true);
		if(args.length != 0) {
			x.open(new File(args[0]));
		}
		Robot r = new Robot();
	}
}
