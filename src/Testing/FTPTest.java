package Testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FTPTest {
	private String host = "127.0.0.1:80",
			userName = "12345",
			password = "12345",
			filePath = "program.jar",
			remoteFilePath = "/programs/program.jar";
	
	public FTPTest(String ip, String un, String pw, String path, String remotePath) {
		host = ip;
		userName = un;
		password = pw;
		filePath = path;
		remoteFilePath = remotePath;
	}
	
	public void save() throws IOException {
	    URL url = new URL(formatURL());
	    URLConnection connection = url.openConnection();
	    OutputStream outputStream = connection.getOutputStream();
	    FileInputStream inputStream = new FileInputStream(filePath);
	    
	    File f = new File(filePath);

	    byte[] buffer = new byte[(int)f.length()];
	    int bytesRead = -1;
	    while ((bytesRead = inputStream.read(buffer)) != -1)
	        outputStream.write(buffer, 0, bytesRead);

	    inputStream.close();
	    outputStream.close();
	}
	
	private String formatURL() {
		return "ftp://" + userName + ":" + password + "@" + host + remoteFilePath;
	}
}
