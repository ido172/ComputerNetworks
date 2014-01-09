import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParamsToTask {

	private static Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$");
	private HashMap<String, String> params;
	private DataBase dataBase;

	public HttpParamsToTask(DataBase dataBase, HashMap<String, String> params) {
		this.params = params;
		this.dataBase = dataBase;
	}

	public boolean isValidNewReminder() {
		try {
			String subject = params.get(DataXMLManager.SUBJECT);
			String content = params.get(DataXMLManager.CONTENT);
			String date = params.get(DataXMLManager.DATE);
			String time = params.get(DataXMLManager.TIME);
			return !(subject.isEmpty() || content.isEmpty() || date.isEmpty() || !isValidDate(date) || time.isEmpty() || !isValidTime(time));
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isValidReminderEdit(String user) {
		boolean result = false;
		if (params.containsKey(DataXMLManager.ID)) {
			int id = Integer.parseInt(params.get(DataXMLManager.ID));
			Reminder reminder = dataBase.retriveReminderByID(id);
			if (reminder != null && isValidNewReminder() && reminder.getUser().equals(user)) {
				result = true;
			}
		}

		return result;
	}

	private boolean isValidDate(String date) {
		try {
			String parsedDate;
			parsedDate = URLDecoder.decode(date, "UTF-8");
			return parsedDate.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isValidTime(String time) {
		try {
			String parsedTime = URLDecoder.decode(time, "UTF-8");
			return parsedTime.matches("\\d\\d:\\d\\d");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createNewReminderInDataBase(String user) {
		Reminder reminder = createNewReminder(user);
		if (reminder != null) {
			dataBase.addReminder(reminder);
			return true;
		} else {
			return false;
		}
	}

	public boolean editReminderInDateBase(String user, int reminderId) {
		Reminder newReminder = editReminder(user, reminderId);
		if (newReminder != null) {
			dataBase.editReminder(reminderId, newReminder);
			return true;
		} else {
			return false;
		}
	}

	private Reminder editReminder(String user, int oldId) {

		String subject, content, date, time;
		try {
			subject = URLDecoder.decode(params.get(DataXMLManager.SUBJECT), "UTF-8");
			content = URLDecoder.decode(params.get(DataXMLManager.CONTENT), "UTF-8");
			date = URLDecoder.decode(params.get(DataXMLManager.DATE), "UTF-8");
			time = URLDecoder.decode(params.get(DataXMLManager.TIME), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		Date parsedDate = new Date();
		try {
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date + " " + time);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}

		Reminder oldRemider = dataBase.retriveReminderByID(oldId);
		return new Reminder(user, subject, oldRemider.getDateOfCreation(), parsedDate, content, false, oldId);
	}

	private Reminder createNewReminder(String user) {

		String subject, content, date, time;

		Date parsedDate = new Date();
		try {
			subject = URLDecoder.decode(params.get(DataXMLManager.SUBJECT), "UTF-8");
			content = URLDecoder.decode(params.get(DataXMLManager.CONTENT), "UTF-8");
			date = URLDecoder.decode(params.get(DataXMLManager.DATE), "UTF-8");
			time = URLDecoder.decode(params.get(DataXMLManager.TIME), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		try {
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date + " " + time);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}

		return new Reminder(user, subject, new Date(), parsedDate, content, false, dataBase.getNewID());
	}

	public boolean isEditRequest() {
		return params.containsKey("edit") && params.get("edit").equals("true");
	}

	public boolean isDeleteRequest() {
		return params.containsKey("delete");
	}

	public boolean isValidDeleteRequest(String user) {
		boolean result = false;
		if (params.containsKey(DataXMLManager.ID)) {
			int id = Integer.parseInt(params.get(DataXMLManager.ID));
			Reminder reminder = dataBase.retriveReminderByID(id);
			if (reminder != null && reminder.getUser().equals(user)) {
				result = true;
			}
		}

		return result;
	}

	public void deleteReminderInDateBase(int id) {
		dataBase.deleteReminder(id);
	}

	public void deleteTaskInDateBase(int id) {
		dataBase.deleteTaskByID(id);
	}

	public boolean isValidNewTask() {
		try {
			String subject = params.get(DataXMLManager.SUBJECT);
			String content = params.get(DataXMLManager.CONTENT);
			String recipient = params.get(DataXMLManager.RCPT);
			String date = params.get(DataXMLManager.DATE);
			String time = params.get(DataXMLManager.TIME);
			return !(subject.isEmpty() || content.isEmpty() || recipient.isEmpty() || !isValidEmail(recipient)
					|| date.isEmpty() || !isValidDate(date) || time.isEmpty() || !isValidTime(time));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isValidEmail(String email) {
		Matcher m = emailPattern.matcher(email); 
		return !m.matches();
	}

	public Task createNewTask(String user) {
		String subject, content, recipient, date, time;
		Date parsedDate = new Date();
		try {
			subject = URLDecoder.decode(params.get(DataXMLManager.SUBJECT), "UTF-8");
			content = URLDecoder.decode(params.get(DataXMLManager.CONTENT), "UTF-8");
			recipient = URLDecoder.decode(params.get(DataXMLManager.RCPT), "UTF-8");
			date = URLDecoder.decode(params.get(DataXMLManager.DATE), "UTF-8");
			time = URLDecoder.decode(params.get(DataXMLManager.TIME), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		try {
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date + " " + time);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}

		return new Task(user, subject, new Date(), parsedDate, Task.In_Progress, content, recipient, false, false, dataBase.getNewID());
	}
	
	public boolean createNewTaskInDataBase(String user) {
		Task newTask = createNewTask(user);
		if (newTask != null) {
			dataBase.addTask(newTask);
			return true;
		} else {
			return false;
		}
	}

}
