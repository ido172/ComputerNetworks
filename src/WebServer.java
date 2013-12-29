import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;

public class WebServer {

	public static final String CONFIG_FILE = "config.ini";
	public static final String SERVER_NAME = "Ido & Shay's Web Server";

	private int port;
	private String root;
	private String defaultPage;
	private int maxThreads;

	private ThreadPool threadPool;

	/**
	 * Read parameters from configuration file.
	 * 
	 * @return true if read was successful or false otherwise
	 */
	private boolean readConfogFile() {
		boolean result = false;
		try {

			// Get parameters from configuration file.
			String configFilePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
					+ CONFIG_FILE;
			ConfigFile.readConfogFile(configFilePath, this);

			// Initialize thread pool with max thread number.
			threadPool = new ThreadPool(maxThreads);

			result = true;
		} catch (FileNotFoundException e) {
			System.err.println("Config file not found!");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Config file not found!");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error while reading config file!");
			e.printStackTrace();
		}
		return result;
	}

	public void runServer() {

		// If read configuration file is successful start server.
		if (readConfogFile()) {

			String from = "shaybozo@gmail.com";
			String address = "shaybozo@walla.com";
			String subject = "read";
			String sender = "shay";
			String data = "My data.";

			SMTPMail.sendSMTPMail(from, address, subject, sender, data);

			// Establish the listen socket.
			ServerSocket socket;

			try {

				// Receive client connection.
				socket = new ServerSocket(port);
				System.out.println("The sever has started listening on port " + port);

				// Process HTTP service requests in an infinite loop.
				while (true) {

					// Listen for a TCP connection request.
					Socket connection = socket.accept();

					// Construct an object to process the HTTP request message.
					HttpRequestHandler request = new HttpRequestHandler(connection, threadPool, root, defaultPage);

					// Send HTTP request to thread poll for handling.
					threadPool.handleNewHttpRequest(request);
				}
			} catch (BindException e) {
				System.err.println("Seems Like port " + port + " is already in use by another program");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Unable to establish server connection");
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("Error!");
				e.printStackTrace();
			}
		}
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public void setThreadPool(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}
}