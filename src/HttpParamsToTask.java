import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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

	public void createNewReminderInDataBase(String user) {
		Reminder reminder = createNewReminder(user);
		dataBase.addReminder(reminder);
	}

	public void editReminderInDateBase(String user) {
		int reminderIdAsInt = Integer.parseInt(params.get(DataXMLManager.ID));
		Reminder newReminder = editReminder(user, reminderIdAsInt);
		dataBase.editReminder(reminderIdAsInt, newReminder);
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

	public void deleteReminderInDateBase() {
		int idInt = Integer.parseInt(params.get(DataXMLManager.ID));
		dataBase.deleteReminderById(idInt);
	}

	public void deleteTaskInDateBase() {
		int idInt = Integer.parseInt(params.get(DataXMLManager.ID));
		dataBase.deleteTaskByID(idInt);
	}
	
	public void deletePollInDateBase() {
		int idInt = Integer.parseInt(params.get(DataXMLManager.ID));
		//dataBase.deletePoll(pollToDelete)
	}

	public boolean isValidNewPoll() {
		

		try {
			String subject = params.get(DataXMLManager.SUBJECT);
			String content = params.get(DataXMLManager.CONTENT);
			String recipients = params.get(DataXMLManager.RCPT);
			if (recipients.isEmpty()) return false;
			String[] allRcpts = recipients.split("\n");
			for (int i = 0; i < allRcpts.length; i++) {
				if (!isValidEmail(allRcpts[i])) {
					return false;
				}
			}
			String answers = URLDecoder.decode(params.get(DataXMLManager.ANSWER), "UTF-8");
			return !(subject.isEmpty() || content.isEmpty() || answers.isEmpty());
		} catch (Exception e) {
			return false;
		}
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

	private static boolean isValidEmail(String email) {
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
	
	public Poll createNewPoll(String user) {
		String subject, content, recipients, answers;
		LinkedList<PollParticipant> rcpts = new LinkedList<PollParticipant>();
		LinkedList<String> parsedAnswers = new LinkedList<String>();
		try {
			subject = URLDecoder.decode(params.get(DataXMLManager.SUBJECT), "UTF-8");
			content = URLDecoder.decode(params.get(DataXMLManager.CONTENT), "UTF-8");
			recipients = URLDecoder.decode(params.get(DataXMLManager.RCPT), "UTF-8");
			String[] allRcpts = recipients.split("\n");
			for (int i = 0; i < allRcpts.length; i++) {
				rcpts.add(new PollParticipant(allRcpts[i], false, ""));
			}
			answers = URLDecoder.decode(params.get(DataXMLManager.ANSWER), "UTF-8");
			String[] allAnswers = answers.split("\n");
			for (int j = 0; j < allAnswers.length; j++) {
				parsedAnswers.add(allAnswers[j]);
			}
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}


		return new Poll(user, new Date(), subject, content, parsedAnswers, rcpts, false, dataBase.getNewID());
	}	
	
	public void createNewTaskInDataBase(String user) {
		Task newTask = createNewTask(user);
		newTask.handleNewTask();
	}
	
	public void createNewPollInDataBase(String user) {
		Poll newPoll = createNewPoll(user);
		dataBase.addPoll(newPoll);
		//TODO
	}

	public boolean isValidTaskReply() {
		try {
			String id = params.get(DataXMLManager.ID);
			Task task = dataBase.retriveTaskByID(Integer.parseInt(id));
			return task != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void closeTask() {
		int idInt = Integer.parseInt(params.get(DataXMLManager.ID));
		dataBase.closeTask(idInt);
	}

	public void setPollAnswer() {
		int idInt = Integer.parseInt(params.get(DataXMLManager.ID));
		int rcptIndex = Integer.parseInt(params.get(DataXMLManager.RCPT));
		int answerIndex = Integer.parseInt(params.get(DataXMLManager.ANSWER));
		dataBase.participantHadAnswerPoll(idInt, rcptIndex, answerIndex);
	}
	
	public boolean isValidPollReply() {
		try {
			String id = params.get(DataXMLManager.ID);
			String answerId = params.get(DataXMLManager.ID);
			String rcptId = params.get(DataXMLManager.ID);
			Poll poll = dataBase.retrivePollByID(Integer.parseInt(id));
			return (!(poll == null && answerId.isEmpty() && rcptId.isEmpty()));
		} catch (Exception e) {
			return false;
		}
	}

}
