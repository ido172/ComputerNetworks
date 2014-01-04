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
	public static String HADREPLYED = "hadReplyed";
	public static String ANSWER = "answer";

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
		synchronized (tasksDoc) {
			Node newTaskElement = convertTaskToNode(task);
			tasksDoc.getFirstChild().appendChild(newTaskElement);
		}
	}

	public void deleteTaskFromXML(Task taskToDelete) {
		synchronized (tasksDoc) {
			Node taskToDeleteAsNode = convertTaskToNode(taskToDelete);
			tasksDoc.removeChild(taskToDeleteAsNode);
		}
	}

	private Node convertTaskToNode(Task task) {
		Element taskAsElement = tasksDoc.createElement(ITEM);
		taskAsElement.setAttribute(USER, task.getTaskCreator());
		taskAsElement.setAttribute(TITLE, task.getTitle());
		taskAsElement.setAttribute(DateOfCreation, task.getDateOfCreation().toString());
		taskAsElement.setAttribute(DUEDATE, task.getDueDate().toString());
		taskAsElement.setAttribute(STATUS, task.getStatus());
		taskAsElement.setAttribute(RCPT, task.getRcpt());
		String isCompleted = Boolean.toString(task.isCompleted());
		taskAsElement.setAttribute(ISCOMPLETED, isCompleted);
		String taskExpiredHadBeenNotify = Boolean.toString(task.isTaskExpiredHadBeenNotify());
		taskAsElement.setAttribute("taskExpiredHadBeenNotify", taskExpiredHadBeenNotify);

		Element _content = tasksDoc.createElement(CONTENT);
		_content.appendChild(tasksDoc.createTextNode(task.getContent()));
		taskAsElement.appendChild(_content);

		return taskAsElement;
	}

	public void addReminder(Reminder reminder) {
		synchronized (reminderDoc) {
			Node newRemionder = convertReminderToNode(reminder);
			reminderDoc.getFirstChild().appendChild(newRemionder);
		}
	}

	public void deleteReminderFromXML(Reminder reminderToDelete) {
		synchronized (reminderDoc) {
			Node reminderToDeleteAsNode = convertReminderToNode(reminderToDelete);
			reminderDoc.removeChild(reminderToDeleteAsNode);
		}
	}

	private Node convertReminderToNode(Reminder reminder) {
		Element remainderAsElement = reminderDoc.createElement(ITEM);
		remainderAsElement.setAttribute(USER, reminder.getUser());
		remainderAsElement.setAttribute(DateOfCreation, reminder.getDateOfCreation().toString());
		remainderAsElement.setAttribute(DateOfReminding, reminder.getDateOfReminding().toString());
		String hadBeenSend = Boolean.toString(reminder.isHadBeenSend());
		remainderAsElement.setAttribute(HadBeenSend, hadBeenSend);

		Element _content = reminderDoc.createElement(CONTENT);
		_content.appendChild(reminderDoc.createTextNode(reminder.getContent()));
		remainderAsElement.appendChild(_content);

		return remainderAsElement;
	}

	public void addPoll(Poll poll) {
		synchronized (pollsDoc) {
			Node newPoll = convertPollToNode(poll);
			pollsDoc.getFirstChild().appendChild(newPoll);
		}
	}

	public void deletePollFromXML(Poll pollToDelete) {
		synchronized (pollsDoc) {
			Node pollToDeleteAsNode = convertPollToNode(pollToDelete);
			pollsDoc.removeChild(pollToDeleteAsNode);
		}
	}

	private Node convertPollToNode(Poll poll) {
		Element _rcpts = pollsDoc.createElement("rcpts");
		Element _answers = pollsDoc.createElement("answers");

		Element pollAsElement = pollsDoc.createElement(ITEM);
		pollAsElement.setAttribute(USER, poll.getPollCreator());
		pollAsElement.setAttribute(TITLE, poll.getTitle());
		pollAsElement.setAttribute(DATE, poll.getDateOfCreation().toString());
		pollAsElement.setAttribute(SUBJECT, poll.getSubject());
		pollAsElement.setAttribute("question", poll.getQuestion());
		pollAsElement.setAttribute(ISCOMPLETED, Boolean.toString(poll.isCompleted()));

		// Answers part.
		Element _answer;

		for (String answer : poll.getAnswers()) {
			_answer = pollsDoc.createElement(ANSWER);
			_answer.appendChild(pollsDoc.createTextNode(answer));

			_answers.appendChild(_answers);
		}

		pollAsElement.appendChild(_answers);

		// Recipients part.
		Element _rcpt;

		for (PollParticipant rcpt : poll.getRcpts()) {
			_rcpt = pollsDoc.createElement(RCPT);
			_rcpt.setAttribute(HADREPLYED, Boolean.toString(rcpt.isHadAnswer()));
			_rcpt.appendChild(pollsDoc.createTextNode(rcpt.getUserName()));
			_rcpts.appendChild(_rcpt);
		}

		pollAsElement.appendChild(_rcpts);

		return pollAsElement;
	}

	public LinkedList<Task> retrieveTasks() {
		synchronized (tasksDoc) {
			LinkedList<Task> taskList = new LinkedList<Task>();
			NodeList items = tasksDoc.getElementsByTagName(ITEM);

			for (int i = 0; i < items.getLength(); i++) {
				Task taskFromFile = createTaskFromItem(items.item(i));
				taskList.add(taskFromFile);
			}

			return taskList;
		}
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
		boolean isCompleted = Boolean.parseBoolean(att.getNamedItem(ISCOMPLETED).getNodeValue());
		boolean taskExpiredHadBeenNotify = Boolean.parseBoolean(att.getNamedItem("taskExpiredHadBeenNotify")
				.getNodeValue());

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
		boolean hadBeenSend = Boolean.parseBoolean(att.getNamedItem(HadBeenSend).getNodeValue());

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

		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
		Date dateOfCreation = null;
		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem(USER).getNodeValue();
		String title = att.getNamedItem(TITLE).getNodeValue();
		String dateOfCreationInString = att.getNamedItem(DATE).getNodeValue();

		try {
			dateOfCreation = formatter.parse(dateOfCreationInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String subject = att.getNamedItem(SUBJECT).getNodeValue();
		String question = att.getNamedItem("question").getNodeValue();
		boolean isCompleted = Boolean.parseBoolean(att.getNamedItem(ISCOMPLETED).getNodeValue());

		// Adds the answers.
		NodeList answerstXML = item.getFirstChild().getChildNodes();
		LinkedList<String> answers = new LinkedList<String>();

		for (int i = 0; i < answerstXML.getLength(); i++) {
			answers.add(answerstXML.item(i).getNodeValue());
		}

		// Add the recipients.
		NodeList rcptsXML = item.getLastChild().getChildNodes();
		LinkedList<PollParticipant> rcpts = new LinkedList<PollParticipant>();

		for (int i = 0; i < rcptsXML.getLength(); i++) {
			String hadReplyed = rcptsXML.item(i).getAttributes().getNamedItem(HADREPLYED).getNodeValue();
			String rcpt = rcptsXML.item(i).getNodeName();
			rcpts.add(new PollParticipant(rcpt, Boolean.parseBoolean(hadReplyed)));

		}

		Poll pollFromFile = new Poll(user, title, dateOfCreation, subject, question, answers, rcpts, isCompleted);

		return pollFromFile;
	}
}