import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.LinkedList;

public class WebServer {

	public static final String CONFIG_FILE = "config.ini";
	public static final String SERVER_NAME = "Ido & Shay's Web Server";

	private int port;
	private String root;
	private String defaultPage;
	private int maxThreads;
	private DataBase dataBase;
	private ThreadPool threadPool;

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
			dataBase = new DataBase();

			ServerSocket socket;

			// /////////////////////////////////////???????????????????
			test();
			// ////////////////////////////////////???????????????????
			try {

				// Receive client connection.
				socket = new ServerSocket(port);
				System.out.println("The sever has started listening on port " + port);

				// Process HTTP service requests in an infinite loop.
				while (true) {

					// Listen for a TCP connection request.
					Socket connection = socket.accept();

					// Construct an object to process the HTTP request message.
					HttpRequestHandler request = new HttpRequestHandler(connection, threadPool, root, defaultPage,
							dataBase);

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

	private void test() {
		String pollCreator = "shaybozo@gmail.com";
		String subject = "Bitches";
		//Date dateOfCreation = new Date();
		// String subject = "subject";
		String question = "Who is my bitch?????";
//		String rcpt = "uri.steinf@gmail.com";
//		String title2 = "Reminder - you are my bitch";

		LinkedList<String> answers = new LinkedList<String>();
		answers.add("ido");
		answers.add("reuven");
		answers.add("uri");
		answers.add("ofer");

		PollParticipant ido = new PollParticipant("ido172@gmail.com", false);
		PollParticipant uri = new PollParticipant("ido172@gmail.com", false);
		PollParticipant reuven = new PollParticipant("shaybozo@gmail.com", false);
		PollParticipant ofer = new PollParticipant("shaybozo@gmail.com", false);
		LinkedList<PollParticipant> rcpts = new LinkedList<PollParticipant>();
		rcpts.add(ofer);
		rcpts.add(ido);
		rcpts.add(uri);
		rcpts.add(reuven);

		boolean isCompleted = false;
		//boolean taskExpiredHadBeenNotify = false;
		// String pollCreator, Date dateOfCreation, String subject, String question, LinkedList<String> answers,
		// LinkedList<PollParticipant> rcpts, boolean isCompleted, int id

		Poll newPoll = new Poll(pollCreator, new Date(), subject, question, answers, rcpts, isCompleted,
				dataBase.getNewID());
		dataBase.addPoll(newPoll);

		// Task(String taskCreator, String title, Date dateOfCreation, Date dueDate, String status, String content,
		// String rcpt, boolean isCompleted, boolean taskExpiredHadBeenNotify, int id) {

		// Task task = new Task(pollCreator, "new task", dateOfCreation, dateOfCreation, Task.In_Progress, "content",
		// rcpt
		// , isCompleted, taskExpiredHadBeenNotify, dataBase.getNewID());

		// Reminder reminder = new Reminder(pollCreator, title2, dateOfCreation, dateOfCreation, "content", false,
		// dataBase.getNewID());

		// dataBase.addTask(task);
		// dataBase.addReminder(reminder);

		dataBase.participantHadAnswerPoll(1, 1, 3);
		System.out.println("end of testTTTTTTTTTTTTTTTT");
	}
}