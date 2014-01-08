import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

	public static String TIME_FORMAT = "dd/MM/yyyy HH:mm";
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
	public static String ID = "id";

	private File tasksDataBase;
	private Document tasksDoc;

	private File remindersDataBase;
	private Document reminderDoc;

	private File pollsDataBase;
	private Document pollsDoc;

	private Transformer transformer;

	public DataXMLManager() {

		try {

			String filePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			// tasksDataBase = new File(ConfigFile.taskFilePath);
			tasksDataBase = new File(filePath + "tasks.xml");

			// this.remindersDataBase = new File(ConfigFile.reminderFilePath);
			remindersDataBase = new File(filePath + "reminders.xml");

			// this.pollsDataBase = new File(ConfigFile.pollFilePath);
			pollsDataBase = new File(filePath + "polls.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			tasksDoc = dBuilder.parse(tasksDataBase);
			reminderDoc = dBuilder.parse(remindersDataBase);
			pollsDoc = dBuilder.parse(pollsDataBase);

			// Normalize the docs.
			tasksDoc.getDocumentElement().normalize();
			reminderDoc.getDocumentElement().normalize();
			pollsDoc.getDocumentElement().normalize();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTask(Task task) {
		synchronized (tasksDoc) {
			Node newTaskElement = convertTaskToNode(task);
			tasksDoc.getFirstChild().appendChild(newTaskElement);

			DOMSource source = new DOMSource(tasksDoc);
			StreamResult result = new StreamResult(tasksDataBase);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteTaskFromXML(Task taskToDelete) {
		synchronized (tasksDoc) {
			Node taskToDeleteAsNode = retriveTaskItemByID(taskToDelete.getId());
			tasksDoc.getFirstChild().removeChild(taskToDeleteAsNode);

			DOMSource source = new DOMSource(tasksDoc);
			StreamResult result = new StreamResult(tasksDataBase);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	public Node retrivePollItemByID(int id) {
		Node poll = null;
		NodeList polls = pollsDoc.getFirstChild().getChildNodes();

		for (int i = 0; i < polls.getLength(); i++) {
			if (Integer.parseInt(polls.item(i).getAttributes().getNamedItem(ID).getNodeValue()) == id) {
				poll = polls.item(i);
				break;
			}
		}

		return poll;
	}

	public Node retriveTaskItemByID(int id) {
		Node task = null;
		NodeList tasks = tasksDoc.getFirstChild().getChildNodes();

		for (int i = 0; i < tasks.getLength(); i++) {
			if (Integer.parseInt(tasks.item(i).getAttributes().getNamedItem(ID).getNodeValue()) == id) {
				task = tasks.item(i);
				break;
			}
		}

		return task;
	}

	public Node retriveReminderItemByID(int id) {
		Node reminder = null;
		NodeList reminders = reminderDoc.getFirstChild().getChildNodes();

		for (int i = 0; i < reminders.getLength(); i++) {
			if (Integer.parseInt(reminders.item(i).getAttributes().getNamedItem(ID).getNodeValue()) == id) {
				reminder = reminders.item(i);
				break;
			}
		}

		return reminder;
	}

	private Node convertTaskToNode(Task task) {
		Element taskAsElement = tasksDoc.createElement(ITEM);
		taskAsElement.setAttribute(USER, task.getTaskCreator());
		taskAsElement.setAttribute(TITLE, task.getTitle());
		taskAsElement.setAttribute(DateOfCreation, new SimpleDateFormat(TIME_FORMAT).format(task.getDateOfCreation()));
		taskAsElement.setAttribute(DUEDATE, new SimpleDateFormat(TIME_FORMAT).format(task.getDueDate()));
		taskAsElement.setAttribute(STATUS, task.getStatus());
		taskAsElement.setAttribute(RCPT, task.getRcpt());
		String isCompleted = Boolean.toString(task.isCompleted());
		taskAsElement.setAttribute(ISCOMPLETED, isCompleted);
		String taskExpiredHadBeenNotify = Boolean.toString(task.isTaskExpiredHadBeenNotify());
		taskAsElement.setAttribute("taskExpiredHadBeenNotify", taskExpiredHadBeenNotify);
		taskAsElement.setAttribute(ID, String.valueOf(task.getId()));

		Element _content = tasksDoc.createElement(CONTENT);
		_content.appendChild(tasksDoc.createTextNode(task.getContent()));
		taskAsElement.appendChild(_content);

		return taskAsElement;
	}

	public void addReminder(Reminder reminder) {
		synchronized (reminderDoc) {
			Node newRemionder = convertReminderToNode(reminder);
			reminderDoc.getFirstChild().appendChild(newRemionder);

			DOMSource source = new DOMSource(reminderDoc);
			StreamResult result = new StreamResult(remindersDataBase);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteReminderFromXML(Reminder reminderToDelete) {
		synchronized (reminderDoc) {
			Node reminderToDeleteAsNode = retriveReminderItemByID(reminderToDelete.getId());
			reminderDoc.getFirstChild().removeChild(reminderToDeleteAsNode);

			DOMSource source = new DOMSource(reminderDoc);
			StreamResult result = new StreamResult(remindersDataBase);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	private Node convertReminderToNode(Reminder reminder) {
		Element remainderAsElement = reminderDoc.createElement(ITEM);
		remainderAsElement.setAttribute(USER, reminder.getUser());
		remainderAsElement.setAttribute(TITLE, reminder.getTitle());
		remainderAsElement.setAttribute(DateOfCreation,
				new SimpleDateFormat(TIME_FORMAT).format(reminder.getDateOfCreation()));
		remainderAsElement.setAttribute(DateOfReminding,
				new SimpleDateFormat(TIME_FORMAT).format(reminder.getDateOfReminding()));
		remainderAsElement.setAttribute(HadBeenSend, Boolean.toString(reminder.isHadBeenSend()));
		remainderAsElement.setAttribute(ID, String.valueOf(reminder.getId()));

		Element _content = reminderDoc.createElement(CONTENT);
		_content.appendChild(reminderDoc.createTextNode(reminder.getContent()));
		remainderAsElement.appendChild(_content);

		return remainderAsElement;
	}

	public void addPoll(Poll poll) {
		synchronized (pollsDoc) {
			Node newPoll = convertPollToNode(poll);
			pollsDoc.getFirstChild().appendChild(newPoll);

			DOMSource source = new DOMSource(pollsDoc);
			StreamResult result = new StreamResult(pollsDataBase);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	public void deletePollFromXML(Poll pollToDelete) {
		synchronized (pollsDoc) {
			Node pollToDeleteAsNode = retrivePollItemByID(pollToDelete.getId());
			pollsDoc.getFirstChild().removeChild(pollToDeleteAsNode);

			DOMSource source = new DOMSource(pollsDoc);
			StreamResult result = new StreamResult(pollsDataBase);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	private Node convertPollToNode(Poll poll) {
		Element _rcpts = pollsDoc.createElement("rcpts");
		Element _answers = pollsDoc.createElement("answers");

		Element pollAsElement = pollsDoc.createElement(ITEM);
		pollAsElement.setAttribute(USER, poll.getPollCreator());
		pollAsElement.setAttribute(TITLE, poll.getTitle());
		pollAsElement.setAttribute(DATE, new SimpleDateFormat(TIME_FORMAT).format(poll.getDateOfCreation()));
		pollAsElement.setAttribute(SUBJECT, poll.getSubject());
		pollAsElement.setAttribute("question", poll.getQuestion());
		pollAsElement.setAttribute(ISCOMPLETED, Boolean.toString(poll.isCompleted()));
		pollAsElement.setAttribute("question", poll.getQuestion());
		pollAsElement.setAttribute(ID, String.valueOf(poll.getId()));

		// Answers part.
		Element _answer;

		for (String answer : poll.getAnswers()) {
			_answer = pollsDoc.createElement(ANSWER);
			_answer.appendChild(pollsDoc.createTextNode(answer));

			_answers.appendChild(_answer);
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

		Date dateOfCreation = null;
		Date dueDate = null;
		NamedNodeMap att = item.getAttributes();

		String taskCreator = att.getNamedItem(USER).getNodeValue();
		String title = att.getNamedItem(TITLE).getNodeValue();

		String dateOfCreationInString = att.getNamedItem(DateOfCreation).getNodeValue();
		String dueDateInString = att.getNamedItem(DUEDATE).getNodeValue();
		try {
			dateOfCreation = new SimpleDateFormat(TIME_FORMAT).parse(dateOfCreationInString);
			dueDate = new SimpleDateFormat(TIME_FORMAT).parse(dueDateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String status = att.getNamedItem(STATUS).getNodeValue();
		String content = item.getFirstChild().getTextContent();
		String rcpt = att.getNamedItem(RCPT).getNodeValue();
		boolean isCompleted = Boolean.parseBoolean(att.getNamedItem(ISCOMPLETED).getNodeValue());
		boolean taskExpiredHadBeenNotify = Boolean.parseBoolean(att.getNamedItem("taskExpiredHadBeenNotify")
				.getNodeValue());
		int id = Integer.parseInt(att.getNamedItem(ID).getNodeValue());

		Task taskFromFile = new Task(taskCreator, title, dateOfCreation, dueDate, status, content, rcpt, isCompleted,
				taskExpiredHadBeenNotify, id);

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

		Date dateOfCreation = null;
		Date dateOfReminding = null;
		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem(USER).getNodeValue();
		String title = att.getNamedItem(TITLE).getNodeValue();
		String dateOfCreationInString = att.getNamedItem(DateOfCreation).getNodeValue();
		String dateOfRemindingInString = att.getNamedItem(DateOfReminding).getNodeValue();
		try {
			dateOfCreation = new SimpleDateFormat(TIME_FORMAT).parse(dateOfCreationInString);
			dateOfReminding = new SimpleDateFormat(TIME_FORMAT).parse(dateOfRemindingInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String content = item.getFirstChild().getTextContent();
		boolean hadBeenSend = Boolean.parseBoolean(att.getNamedItem(HadBeenSend).getNodeValue());
		int id = Integer.parseInt(att.getNamedItem(ID).getNodeValue());

		Reminder reminderFromFile = new Reminder(user, title, dateOfCreation, dateOfReminding, content, hadBeenSend, id);

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

		Date dateOfCreation = null;
		NamedNodeMap att = item.getAttributes();

		String user = att.getNamedItem(USER).getNodeValue();
		String title = att.getNamedItem(TITLE).getNodeValue();
		String dateOfCreationInString = att.getNamedItem(DATE).getNodeValue();
		int id = Integer.parseInt(att.getNamedItem(ID).getNodeValue());

		try {
			dateOfCreation = new SimpleDateFormat(TIME_FORMAT).parse(dateOfCreationInString);
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
			answers.add(answerstXML.item(i).getTextContent());
		}

		// Add the recipients.
		NodeList rcptsXML = item.getLastChild().getChildNodes();
		LinkedList<PollParticipant> rcpts = new LinkedList<PollParticipant>();

		for (int i = 0; i < rcptsXML.getLength(); i++) {
			String hadReplyed = rcptsXML.item(i).getAttributes().getNamedItem(HADREPLYED).getNodeValue();
			String rcpt = rcptsXML.item(i).getTextContent();
			rcpts.add(new PollParticipant(rcpt, Boolean.parseBoolean(hadReplyed)));

		}

		Poll pollFromFile = new Poll(user, title, dateOfCreation, subject, question, answers, rcpts, isCompleted, id);

		return pollFromFile;
	}

	public void participantHadAnswerPoll(int pollID, String pollParticipantName) {

		synchronized (pollsDoc) {

			Node pollToChangeAsNode = retrivePollItemByID(pollID);
			NodeList participants = pollToChangeAsNode.getLastChild().getChildNodes();
			Element participantToBeChange = null;

			for (int i = 0; i < participants.getLength(); i++) {
				if (participants.item(i).getTextContent().equals(pollParticipantName)) {
					participantToBeChange = (Element) participants.item(i);
					break;
				}
			}

			participantToBeChange.setAttribute(HADREPLYED, Boolean.toString(true));
			DOMSource source = new DOMSource(pollsDoc);
			StreamResult result = new StreamResult(pollsDataBase);

			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	// public void taskTimeToCompleteHadExpired(int taskID) {
	// synchronized (tasksDoc) {
	//
	// Element taskToBeChangeAsNode = (Element) retriveTaskItemByID(taskID);
	// taskToBeChangeAsNode.setAttribute(, ANSWER);
	//
	//
	// DOMSource source = new DOMSource(tasksDoc);
	// StreamResult result = new StreamResult(tasksDataBase);
	//
	// try {
	// transformer.transform(source, result);
	// } catch (TransformerException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public void taskHadBeenCompleted(int taskID) {
	// synchronized (tasksDoc) {
	//
	// Element taskToBeChangeAsNode = (Element) retriveTaskItemByID(taskID);
	// taskToBeChangeAsNode.setAttribute(, ANSWER);
	//
	//
	// DOMSource source = new DOMSource(tasksDoc);
	// StreamResult result = new StreamResult(tasksDataBase);
	//
	// try {
	// transformer.transform(source, result);
	// } catch (TransformerException e) {
	// e.printStackTrace();
	// }
	// }
	// }
}