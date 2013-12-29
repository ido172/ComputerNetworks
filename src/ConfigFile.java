import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConfigFile {

	public static String SMTPName;
	public static int SMTPPort;
	public static String ServerName;
	public static String SMTPUsername;
	public static String SMTPPassword;
	public static String SMTPIsAuthLogin;
	public static String reminderFilePath;
	public static String taskFilePath;
	public static String pollFilePath;

	public static void readConfogFile(String filePath, WebServer webServer) throws NullPointerException, IOException {

		// Open Configuration file.
		File configFile = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		String line = "";
		String key = "";
		String value = "";
		HashMap<String, String> hashMap = new HashMap<String, String>();

		// Read lines into hash map.
		while ((line = reader.readLine()) != null) {
			key = line.substring(0, line.indexOf('='));
			value = line.substring(line.indexOf('=') + 1, line.length());
			hashMap.put(key, value);
		}

		// Set server parameters.
		webServer.setPort(Integer.parseInt(hashMap.get("port")));
		webServer.setRoot(hashMap.get("root"));
		webServer.setDefaultPage(hashMap.get("defaultPage"));
		webServer.setMaxThreads(Integer.parseInt(hashMap.get("maxThreads")));

		// Set static parameters.
		SMTPName = hashMap.get("SMTPName");
		SMTPPort = Integer.parseInt(hashMap.get("SMTPPort"));
		ServerName = hashMap.get("ServerName");
		SMTPUsername = hashMap.get("SMTPUsername");
		SMTPPassword = hashMap.get("SMTPPassword");
		SMTPIsAuthLogin = hashMap.get("SMTPIsAuthLogin");
		reminderFilePath = hashMap.get("reminderFilePath");
		taskFilePath = hashMap.get("taskFilePath");
		pollFilePath = hashMap.get("pollFilePath");

		// Close file.
		try {
			reader.close();
		} catch (IOException e) {
			System.err.println("Unable to close config file");
			e.printStackTrace();
		}
	}
}