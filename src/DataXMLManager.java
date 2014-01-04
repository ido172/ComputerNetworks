import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class DataXMLManager {
	public static String TIME_FORMAT = "";
	public static String USER = "user";
	public static String DATE = "date";
	public static String DateOfCreation = "dateOfCreation";
	public static String DateOfReminding = "dateOfReminding";
	public static String HadBeenSend = "hadBeenSend";
	public static String TIME = "time";
	public static String CONTENT = "content";
	public static String ISCOMPLETED = "isCompleted";
	public static String RCPT = "rcpt";
	public static String ITEM = "item";
	public static String SUBJECT = "subject";
	public static String TITLE = "title";
	public static String DUEDATE = "dueDate";
	public static String STATUS = "status";
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

		Element newTask = tasksDoc.createElement(ITEM);
		newTask.setAttribute(USER, task.getTaskCreator());
		newTask.setAttribute(TITLE, task.getTitle());
		newTask.setAttribute(DateOfCreation, task.getDateOfCreation().toString());
		newTask.setAttribute(DUEDATE, task.getDueDate().toString());
		newTask.setAttribute(STATUS, task.getStatus());
		newTask.setAttribute(RCPT, task.getRcpt());
		String isCompleted = task.isCompleted() ? "true" : "false";
		newTask.setAttribute(ISCOMPLETED, isCompleted);
		String taskExpiredHadBeenNotify = task.isTaskExpiredHadBeenNotify() ? "true" : "false";
		newTask.setAttribute("taskExpiredHadBeenNotify", taskExpiredHadBeenNotify);

		Element _content = tasksDoc.createElement(CONTENT);
		_content.appendChild(tasksDoc.createTextNode(task.getContent()));
		newTask.appendChild(_content);

		Node taskes = tasksDoc.getFirstChild();
		taskes.appendChild(newTask);

	}

	public void addReminder(Reminder reminder) {

		Element newRemainder = reminderDoc.createElement(ITEM);
		newRemainder.setAttribute(USER, reminder.getUser());
		newRemainder.setAttribute(DateOfCreation, reminder.getDateOfCreation().toString());
		newRemainder.setAttribute(DateOfReminding, reminder.getDateOfReminding().toString());
		String hadBeenSend = reminder.isHadBeenSend() ? "true" : "false";
		newRemainder.setAttribute(HadBeenSend, hadBeenSend);

		Element _content = reminderDoc.createElement(CONTENT);
		_content.appendChild(reminderDoc.createTextNode(reminder.getContent()));
		newRemainder.appendChild(_content);

		Node remainders = reminderDoc.getFirstChild();
		remainders.appendChild(newRemainder);
	}

	public void addPoll(Poll poll) {
		Element newPoll = pollsDoc.createElement(ITEM);
		newPoll.setAttribute(USER, poll.getUser());
		newPoll.setAttribute(DATE, poll.getDate().toString());
		newPoll.setAttribute(TIME, poll.getTime());
		newPoll.setAttribute("question", poll.getQuestion());
		newPoll.setAttribute(ISCOMPLETED, poll.getIsCompleted());

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

		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
		Date dateOfCreation = null;
		Date dueDate = null;
		NamedNodeMap att = item.getAttributes();

		String taskCreator = att.getNamedItem(USER).getNodeValue();
		String title = att.getNamedItem(TITLE).getNodeValue();

		String dateOfCreationInString = att.getNamedItem(DateOfCreation).getNodeValue();
		String dueDateInString = att.getNamedItem(DUEDATE).getNodeValue();
		try {
			dateOfCreation = formatter.parse(dateOfCreationInString);
			dueDate = formatter.parse(dueDateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String status = att.getNamedItem(STATUS).getNodeValue();
		String content = item.getFirstChild().getNodeValue();
		String rcpt = att.getNamedItem(RCPT).getNodeValue();
		boolean isCompleted = (att.getNamedItem(ISCOMPLETED).getNodeValue().equals("true")) ? true : false;
		boolean taskExpiredHadBeenNotify = (att.getNamedItem("taskExpiredHadBeenNotify").getNodeValue().equals("true")) ? true
				: false;

		Task taskFromFile = new Task(taskCreator, title, dateOfCreation, dueDate, status, content, rcpt, isCompleted,
				taskExpiredHadBeenNotify);

		return taskFromFile;
	}

	public LinkedList<Reminder> retrieveReminders() {
		NodeList items = reminderDoc.getElementsByTagName(ITEM);
		LinkedList<Reminder> reminderList = new LinkedList<Reminder>();

		for (int i = 0; i < items.getLength(); i++) {
			Reminder reminderFromFile = createReminderFromItem(items.item(i));
			reminderList.add(reminderFromFile);
		}

		return reminderList;
	}

	private Reminder createReminderFromItem(Node item) {

		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
		Date dateOfCreation = null;
		Date dateOfReminding = null;
		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem(USER).getNodeValue();
		String title = att.getNamedItem(TITLE).getNodeValue();
		String dateOfCreationInString = att.getNamedItem(DateOfCreation).getNodeValue();
		String dateOfRemindingInString = att.getNamedItem(DateOfReminding).getNodeValue();
		try {
			dateOfCreation = formatter.parse(dateOfCreationInString);
			dateOfReminding = formatter.parse(dateOfRemindingInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String content = item.getFirstChild().getNodeValue();
		boolean hadBeenSend = (att.getNamedItem(HadBeenSend).getNodeValue().equals("true")) ? true : false;

		Reminder reminderFromFile = new Reminder(user, title, dateOfCreation, dateOfReminding, content, hadBeenSend);

		return reminderFromFile;
	}

	public LinkedList<Poll> retrievePolls() {
		NodeList items = pollsDoc.getElementsByTagName(ITEM);
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

		Poll pollFromFile = new Poll(user, (Date) date, time, title, question, isCompleted, rcpts, answers);

		return pollFromFile;
	}
}