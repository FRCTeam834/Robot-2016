package Testing;
/**
 * deprecated
 */
import base.*;
import java.awt.*;
import java.io.Serializable;
import javax.swing.*;

import Config.VisualRobot;
public class SimpleCommand implements Command {

	private String message;
			
    public SimpleCommand(String msg) {
    	message = msg;
    }
    
	public void execute() {
		System.out.println(message);

	}

	public void edit() {
		String temp;
		temp = JOptionPane.showInputDialog(null, "Message:");
		if(temp != null) {
			message = temp;
		}
	}

	public void viewInfo() {
		execute();
	}

	@Override
	public void setRobot(VisualRobot r) {
		// TODO Auto-generated method stub
		
	}
	

}

