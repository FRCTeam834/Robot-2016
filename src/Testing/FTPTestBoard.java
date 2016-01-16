// Authors: Ben Zalatan
// Last Edited: 1/15/2016
// Description: Test FTP client class for saving a file to a remote FTP server.

package Testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FTPTestBoard {
	//IP and port of the remote server.
	private String host = "roborio-834-frc.local",
			//Username to login with.
			userName = "anonymous",
			//Password to login with.
			password = "",
			//Local file path to copy.
			filePath = "program.jar",
			//Remote path on server to copy to.
			remoteFilePath = "/programs/program.jar";
	
	public FTPTestBoard(String ip, String un, String pw, String path, String remotePath) {
		//Set all variables to given arguments.
		host = ip;
		userName = un;
		password = pw;
		filePath = path;
		remoteFilePath = remotePath;
	}
	
	public void save() throws IOException {
		//Format the FTP address.
		URL url = new URL(formatURL());
		//Establish a connection with the server.
		URLConnection connection = url.openConnection();
		//Create a write stream to the server.
		OutputStream outputStream = connection.getOutputStream();
		//Create a read stream from the local computer.
		FileInputStream inputStream = new FileInputStream(filePath);
	    	
	    	//File variable used to get the file size.
		File f = new File(filePath);

		//Initialize the buffer to be used for I/O to the server.
		byte[] buffer = new byte[(int)f.length()];
		
		//Read the bytes from the local file.
		//inputStream.read(buffer)
		//Save the file to the remote FTP server.
		//outputStream.write(bufferd);

		//Close the streams.
		inputStream.close();
		outputStream.close();
	}
	
	private String formatURL() {
		//Format the FTP url/address.
		return "ftp://" + userName + ":" + password + "@" + host + remoteFilePath;
	}
}
