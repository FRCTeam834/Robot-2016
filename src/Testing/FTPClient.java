package Testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FTPClient {
	private String host = "roborio-834-frc.local",
			userName = "anonymous",
			password = "",
			filePath,
			remoteFilePath = "/home/lvuser/";
	
	public FTPClient(String fileName) {
		filePath = fileName;
		remoteFilePath += fileName;
		try {
			this.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() throws IOException {
		URL url = new URL(formatURL());
		URLConnection connection = url.openConnection();
		OutputStream outputStream = connection.getOutputStream();
		FileInputStream inputStream = new FileInputStream(filePath);
	    
		File f = new File(filePath);

		byte[] buffer = new byte[(int)f.length()];
	    	    
		inputStream.read(buffer);
		outputStream.write(buffer);

		inputStream.close();
		outputStream.close();
	}
	
	private String formatURL() {
		return "ftp://" + userName + ":" + password + "@" + host + remoteFilePath;
	}
}
