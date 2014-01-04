import java.util.Date;
import java.util.LinkedList;

public class Program {

	public static void main(String argv[]) {

		// WebServer myServer = new WebServer();
		// myServer.runServer();

		String pollCreator = "shaybozo@gmail.com";
		String title = "Task - be my bitch";
		Date dateOfCreation = new Date();
		String subject = "subject";
		String question = "Who is my bitch?????";
		String rcpt = "uri.steinf@gmail.com";
		String title2 = "Reminder - you are my bitch";

		LinkedList<String> answers = new LinkedList<String>();
		answers.add("ido");
		answers.add("reuven");
		answers.add("uri");
		answers.add("ofer");

		PollParticipant ido = new PollParticipant("ido172@gmail.com", false);
		PollParticipant uri = new PollParticipant("Uri S <uri.steinf@gmail.com>", false);
		PollParticipant reuven = new PollParticipant("Reuven Eliyahu <ruvene@gmail.com>", false);
		PollParticipant ofer = new PollParticipant("oferbennoon@gmail.com", false);
		LinkedList<PollParticipant> rcpts = new LinkedList<PollParticipant>();
		rcpts.add(ofer);
		rcpts.add(ido);
		rcpts.add(uri);
		rcpts.add(reuven);
		boolean isCompleted = false;
		boolean taskExpiredHadBeenNotify = false;

		Task task = new Task(pollCreator, title, dateOfCreation, dateOfCreation, "status", "content", rcpt,
				isCompleted, taskExpiredHadBeenNotify);
		
		Reminder reminder = new Reminder(pollCreator, title2, dateOfCreation, dateOfCreation, "content", false);

		DataXMLManager dataBase = new DataXMLManager();
		dataBase.addTask(task);
		dataBase.addReminder(reminder);

		LinkedList<Task> tasks = dataBase.retrieveTasks();
		LinkedList<Reminder> reminder1 = dataBase.retrieveReminders();
		LinkedList<Poll> polls = dataBase.retrievePolls();
		
		System.out.println("colllllll");
	}
}