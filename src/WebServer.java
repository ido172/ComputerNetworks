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
		Date dateOfCreation = new Date();
		String subject1 = "subject";
		String question = "Who is my bitch?????";
		String rcpt = "shaybozo@gmail.com";
		String title2 = "Reminder - you are my bitch";

		LinkedList<String> answers = new LinkedList<String>();
		answers.add("ido");
		answers.add("reuven");
		answers.add("uri");
		answers.add("ofer");

		PollParticipant ido = new PollParticipant("ido172@gmail.com", false, "");
		PollParticipant uri = new PollParticipant("ido172spam@gmail.com", false, "");
		PollParticipant shayWalla = new PollParticipant("shaybozo@walla.com", false, "");
		PollParticipant shayGmail = new PollParticipant("shaybozo@gmail.com", false, "");
		LinkedList<PollParticipant> rcpts = new LinkedList<PollParticipant>();
		rcpts.add(ido);
		rcpts.add(uri);
		rcpts.add(shayWalla);
		rcpts.add(shayGmail);

		boolean isCompleted = false;
		boolean taskExpiredHadBeenNotify = false;

		Poll newPoll = new Poll(pollCreator, new Date(), subject1, question, answers, rcpts, isCompleted,
				dataBase.getNewID());
		dataBase.addPoll(newPoll);
		newPoll.sendMailsToParticipants();

		Task task = new Task(pollCreator, "new task", dateOfCreation, dateOfCreation, Task.In_Progress, "content",
				rcpt, isCompleted, taskExpiredHadBeenNotify, dataBase.getNewID());

		dataBase.addTask(task);
		task.handleNewTask();

		dataBase.participantHadAnswerPoll(12, "ido172@gmail.com", 3);
		dataBase.participantHadAnswerPoll(12, "ido172spam@gmail.com", 3);
		dataBase.participantHadAnswerPoll(12, "shaybozo@walla.com", 3);
		// dataBase.participantHadAnswerPoll(12, "shaybozo@gmail.com", 3);

	}
}