import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

public class DataXMLManager {

	public static String USER = "user";
	public static String DATE = "date";
	public static String TIME = "time";
	public static String CONTENT = "content";

	private File tasksDataBase;
	private Document tasksDoc;

	private File remindersDataBase;
	private Document reminderDoc;

	private File pollsDataBase;
	private Document pollsDoc;

	public DataXMLManager() {

		try {
			// Create the files if they do not exists.
			if (!tasksDataBase.exists()) {
				tasksDataBase = new File(ConfigFile.taskFilePath);
			}

			if (!remindersDataBase.exists()) {
				this.remindersDataBase = new File(ConfigFile.reminderFilePath);
			}

			if (!pollsDataBase.exists()) {
				this.pollsDataBase = new File(ConfigFile.pollFilePath);
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			tasksDoc = dBuilder.parse(tasksDataBase);
			reminderDoc = dBuilder.parse(remindersDataBase);
			pollsDoc = dBuilder.parse(pollsDataBase);

			// Normalize the docs.
			tasksDoc.getDocumentElement().normalize();
			reminderDoc.getDocumentElement().normalize();
			pollsDoc.getDocumentElement().normalize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTask(Task task) {

		Element newTask = tasksDoc.createElement("item");
		newTask.setAttribute(USER, task.getUser());
		newTask.setAttribute("rcpt", task.getRcpt());
		newTask.setAttribute("subject", task.getSubject());
		newTask.setAttribute(DATE, task.getDate());
		newTask.setAttribute(TIME, task.getTime());
		newTask.setAttribute("isDone", task.getIsDone());

		Element _content = tasksDoc.createElement(CONTENT);
		_content.appendChild(tasksDoc.createTextNode(task.getContent()));
		newTask.appendChild(_content);

		Node taskes = tasksDoc.getFirstChild();
		taskes.appendChild(newTask);

	}

	public void addReminder(Reminder reminder) {
		Element newRemainder = reminderDoc.createElement("item");
		newRemainder.setAttribute(USER, reminder.getUser());
		newRemainder.setAttribute(DATE, reminder.getDate());
		newRemainder.setAttribute(TIME, reminder.getTime());

		Element _content = reminderDoc.createElement(CONTENT);
		_content.appendChild(reminderDoc.createTextNode(reminder.getContent()));
		newRemainder.appendChild(_content);

		Node remainders = reminderDoc.getFirstChild();
		remainders.appendChild(newRemainder);
	}

	public void addPoll(Poll poll) {
		Element newPoll = pollsDoc.createElement("item");
		newPoll.setAttribute(USER, poll.getUser());
		newPoll.setAttribute(DATE, poll.getDate().toString());
		newPoll.setAttribute(TIME, poll.getTime());
		newPoll.setAttribute("title", poll.getTitle());
		newPoll.setAttribute("question", poll.getQuestion());
		newPoll.setAttribute("isCompleted", poll.getIsCompleted());

		// Rcpts part.
		Element _rcpts = reminderDoc.createElement("rcpts");
		Element _contact;

		for (String rcpt : poll.getRcpts()) {
			_contact = reminderDoc.createElement("contact");
			_contact.setAttribute("hasReplyed", "true"); // There is a problem here with the attribute hasReplyed.
			_contact.appendChild(reminderDoc.createTextNode(rcpt));

			_rcpts.appendChild(_rcpts);
		}

		newPoll.appendChild(_rcpts);

		// Answers part.
		Element _answers = reminderDoc.createElement("answers");
		Element _answer;

		for (String answer : poll.getAnswers()) {
			_answer = reminderDoc.createElement("contact");
			_answer.appendChild(reminderDoc.createTextNode(answer));

			_rcpts.appendChild(_answers);
		}

		newPoll.appendChild(_answers);

		Node polls = pollsDoc.getFirstChild();
		polls.appendChild(newPoll);
	}

	public LinkedList<Task> retrieveTasks() {
		LinkedList<Task> taskList = new LinkedList<Task>();
		NodeList items = tasksDoc.getElementsByTagName("item");

		for (int i = 0; i < items.getLength(); i++) {
			Task taskFromFile = createTaskFromItem(items.item(i));
			taskList.add(taskFromFile);
		}

		return taskList;
	}

	private Task createTaskFromItem(Node item) {

		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem("user").getNodeValue();
		String date = att.getNamedItem("date").getNodeValue();
		String time = att.getNamedItem("time").getNodeValue();
		String content = item.getFirstChild().getNodeValue();
		String rcpt = att.getNamedItem("rcpt").getNodeValue();
		String subject = att.getNamedItem("subject").getNodeValue();
		String isDone = att.getNamedItem("isDone").getNodeValue();

		Task taskFromFile = new Task(user, date, time, content, rcpt, subject, isDone);

		return taskFromFile;
	}

	public LinkedList<Reminder> retrieveReminders() {
		NodeList items = reminderDoc.getElementsByTagName("item");
		LinkedList<Reminder> reminderList = new LinkedList<Reminder>();

		for (int i = 0; i < items.getLength(); i++) {
			Reminder reminderFromFile = createReminderFromItem(items.item(i));
			reminderList.add(reminderFromFile);
		}

		return reminderList;
	}

	private Reminder createReminderFromItem(Node item) {

		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem("user").getNodeValue();
		String date = att.getNamedItem("date").getNodeValue();
		String time = att.getNamedItem("time").getNodeValue();
		String content = item.getFirstChild().getNodeValue();
		String title = att.getNamedItem("title").getNodeValue();

		Reminder reminderFromFile = new Reminder(user, date, time, content, title);

		return reminderFromFile;
	}

	public LinkedList<Poll> retrievePolls() {
		NodeList items = pollsDoc.getElementsByTagName("item");
		LinkedList<Poll> pollList = new LinkedList<Poll>();

		for (int i = 0; i < items.getLength(); i++) {
			Poll pollFromFile = createPollFromItem(items.item(i));
			pollList.add(pollFromFile);
		}

		return pollList;
	}

	private Poll createPollFromItem(Node item) {
		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem("user").getNodeValue();
		String date = att.getNamedItem("date").getNodeValue();
		String time = att.getNamedItem("time").getNodeValue();
		String title = att.getNamedItem("title").getNodeValue();
		String question = att.getNamedItem("question").getNodeValue();
		String isCompleted = att.getNamedItem("isCompleted").getNodeValue();

		Node rcptsXML = item.getFirstChild();
		// rcptsXML.
		LinkedList<String> rcpts = new LinkedList<>();

		// for (){
		//
		// }

		Node answerstXML = item.getLastChild();
		LinkedList<String> answers = new LinkedList<>();

		// for (){
		//
		// }

		Poll pollFromFile = new Poll(user, (Date)date, time, title, question, isCompleted, rcpts, answers);

		return pollFromFile;
	}
}