import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.awt.List;
import java.io.File;
import java.util.LinkedList;

public class DataManager {

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

	public DataManager() {

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

	public void addTask(String user, String date, String time, String content, String rcpt, String subject,
			String isDone) {

		Element newTask = tasksDoc.createElement("item");
		newTask.setAttribute(USER, user);
		newTask.setAttribute("rcpt", rcpt);
		newTask.setAttribute("subject", subject);
		newTask.setAttribute(DATE, user);
		newTask.setAttribute(TIME, user);
		newTask.setAttribute("isDone", isDone);

		Element _content = tasksDoc.createElement(CONTENT);
		_content.appendChild(tasksDoc.createTextNode(content));
		newTask.appendChild(_content);

		Node taskes = tasksDoc.getFirstChild();
		taskes.appendChild(newTask);

	}

	public void addRemainder(String user, String date, String time, String content) {
		Element newRemainder = reminderDoc.createElement("item");
		newRemainder.setAttribute(USER, user);
		newRemainder.setAttribute(DATE, user);
		newRemainder.setAttribute(TIME, user);

		Element _content = reminderDoc.createElement(CONTENT);
		_content.appendChild(reminderDoc.createTextNode(content));
		newRemainder.appendChild(_content);

		Node remainders = reminderDoc.getFirstChild();
		remainders.appendChild(newRemainder);
	}

	public void addPoll(String user, String date, String time, String content, String title, String question,
			String isCompleted, LinkedList<String> rcpts, LinkedList<String> answers) {
		Element newPoll = pollsDoc.createElement("item");
		newPoll.setAttribute(USER, user);
		newPoll.setAttribute(DATE, user);
		newPoll.setAttribute(TIME, user);
	}
}