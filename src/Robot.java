import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/*
 * Test class for Robot. In actual implementation, will probably
 * import the robot class so that the commands to manipulate the robot will
 * work.
 */
public class Robot {
	ArrayList<Command> commands = new ArrayList<Command>();
	JFileChooser fs = new JFileChooser();
	public void chooseProgram() {
		//Read  from switches to find out which file to use.
		
		File f = null;
		//temporary
		if(fs.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)  {				
			f = fs.getSelectedFile();
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				commands = (ArrayList<Command>) ois.readObject();
				ois.close();
			}
			
			catch(IOException e) {}
			catch(ClassNotFoundException e) {}
		}
		// end temporary
		
		runProgram(f);
		
	}
	public void runProgram(File f) {
		for(Command c:commands) {
			c.execute(); 
		}
		
	}
}
