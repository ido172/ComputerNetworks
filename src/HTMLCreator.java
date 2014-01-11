import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class HTMLCreator {

	
	private static String STATUS_PLACEHOLDER = "[STATUS]";
	private static String USERNAME_PLACEHOLDER = "[USER_NAME]";
	private static String DATA_PLACEHOLDER = "[DATA_PLACEHOLDER]";
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat justDateFormat;
	private SimpleDateFormat justTimeFormat;

	public HTMLCreator() {
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		justDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		justTimeFormat = new SimpleDateFormat("HH:mm");
	}

	public String addUserNameToPage(String userName, String htmlTemaplate) {
		try {
			return htmlTemaplate.replace(USERNAME_PLACEHOLDER, URLDecoder.decode(userName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return htmlTemaplate.replace(USERNAME_PLACEHOLDER, "");
		}
	}

	public String createRemaindersPage(String userName, String htmlTemaplate, DataBase dataBase) {
		StringBuilder content = new StringBuilder();
		LinkedList<Reminder> reminders = dataBase.retrieveReminderByUser(userName);
		if (reminders.size() > 0) {
			content.append("<table width='100%' border=1>");
			content.append("<tr>");
			content.append("<th>Title</th>");
			content.append("<th>Date Of Creation</th>");
			content.append("<th>Date Of Reminder</th>");
			content.append("<th>Edit</th>");
			content.append("<th>Delete</th>");
			content.append("</tr>");
			for (Reminder reminder : reminders) {
				content.append("<tr>");
				content.append("<td>" + reminder.getTitle() + "</td>");
				content.append("<td>" + getDateInRightformat(reminder.getDateOfCreation()) + "</td>");
				content.append("<td>" + getDateInRightformat(reminder.getDateOfReminding()) + "</td>");
				if (!reminder.isHadBeenSend()) {
					content.append("<td><a data-role=button onclick=storeReminderData" + builderReminderIdParams(reminder)
							+ " href=reminder_editor.html>Edit</a></td>");
				} else {
					content.append("<td></td>");
				}
				content.append("<td><form action='submit_reminder.html' data-ajax='false' method='POST'><input type='submit' value='delete' /><input type='hidden' name='id' value='"
						+ reminder.getId() + "'/><input type='hidden' name='delete' value='delete'/></form></td>");
				content.append("</tr>");
			}
			content.append("</table>");
		}
		return htmlTemaplate.replace(DATA_PLACEHOLDER, content.toString());
	}

	public String createTasksPage(String userName, String htmlTemaplate, DataBase dataBase) {
		StringBuilder content = new StringBuilder();
		LinkedList<Task> tasks = dataBase.retrieveTasksByUser(userName);
		if (tasks.size() > 0) {
			content.append("<table width='100%' border=1>");
			content.append("<tr>");
			content.append("<th>Title</th>");
			content.append("<th>Date Of Creation</th>");
			content.append("<th>Date and time<br/>of due date</th>");
			content.append("<th>Status</th>");
			content.append("<th>Delete</th>");
			content.append("</tr>");
			for (Task task : tasks) {
				content.append("<tr>");
				content.append("<td>" + task.getTitle() + "</td>");
				content.append("<td>" + getDateInRightformat(task.getDateOfCreation()) + "</td>");
				content.append("<td>" + getDateInRightformat(task.getDueDate()) + "</td>");
				content.append("<td>" + task.getStatus() + "</td>");
				if (task.getStatus().equals(Task.In_Progress))
					content.append("<td><form action='submit_task.html' data-ajax='false' method='POST'><input type='submit' value='delete' /><input type='hidden' name='id' value='"
							+ task.getId() + "'/><input type='hidden' name='delete' value='delete'/></form></td>");
				else {
					content.append("<td></td>");
				}
				content.append("</tr>");
			}
			content.append("</table>");
		}

		return htmlTemaplate.replace(DATA_PLACEHOLDER, content.toString());
	}

	public String createPollsPage(String userName, String htmlTemaplate, DataBase dataBase) {
		StringBuilder content = new StringBuilder();
		LinkedList<Poll> polls = dataBase.retrievePollsByUser(userName);
		if (polls.size() > 0) {
			content.append("<table width='100%' border=1>");
			content.append("<tr>");
			content.append("<th>Title</th>");
			content.append("<th>Date Of Creation</th>");
			content.append("<th>Replys</th>");
			content.append("<th>Delete</th>");
			content.append("</tr>");
			for (Poll poll : polls) {
				content.append("<tr>");
				content.append("<td>" + poll.getSubject() + "</td>");
				content.append("<td>" + getDateInRightformat(poll.getDateOfCreation()) + "</td>");
				content.append("<td style='text-align:left;'>");
				LinkedList<PollParticipant> allRcpts = poll.getRcpts();
				for (PollParticipant rcpt : allRcpts) {
					content.append("<b>"+rcpt.getUserName()+"</b>: ");
					if (rcpt.isHadAnswer())
						content.append(rcpt.getParticipantReplay());
					else
						content.append("had not answered yet");
					content.append("<br/>");
				}
				content.append("</td>");
				content.append("<td><form action='submit_poll.html' data-ajax='false' method='POST'><input type='submit' value='delete' /><input type='hidden' name='id' value='"+ poll.getId() + "'/><input type='hidden' name='delete' value='delete'/></form></td>");
				
				content.append("</tr>");
			}
			content.append("</table>");
		}

		return htmlTemaplate.replace(DATA_PLACEHOLDER, content.toString());
	}

	private String builderReminderIdParams(Reminder reminder) {
		StringBuilder editParams = new StringBuilder();
		editParams.append("('" + reminder.getId() + "'");
		editParams.append(",");
		editParams.append("'" + reminder.getTitle() + "'");
		editParams.append(",");
		editParams.append("'" + reminder.getContent() + "'");
		editParams.append(",");
		editParams.append("'" + getJustDate(reminder.getDateOfReminding()) + "'");
		editParams.append(",");
		editParams.append("'" + getJustTime(reminder.getDateOfReminding()) + "')");
		return editParams.toString();
	}

	private String getDateInRightformat(Date date) {
		return dateFormat.format(date);
	}

	private String getJustDate(Date date) {
		return justDateFormat.format(date);
	}

	private String getJustTime(Date date) {
		return justTimeFormat.format(date);
	}
	
	public String createTaskReplyPage(String htmlTemaplate, boolean isSuccess) {
		String result;
		if (isSuccess) {
			result = htmlTemaplate.replace(STATUS_PLACEHOLDER, "Task completed!");
			result = result.replace(DATA_PLACEHOLDER, "Task was successfully marked as completed");
		}
		else {
			result = htmlTemaplate.replace(STATUS_PLACEHOLDER, "Error!");
			result = result.replace(DATA_PLACEHOLDER, "Error while trying to close task, maybe deleted, completed or time over due");
		}
		
		return result;
	}
	
	public String createPollReplyPage(String htmlTemaplate, DataBase dataBase, HashMap<String, String> params, boolean isSuccess) {
		String result;
		if (isSuccess) {
			result = htmlTemaplate.replace(STATUS_PLACEHOLDER, "You have aswered the poll!");
			String answer = dataBase.retrivePollByID(Integer.parseInt(params.get(DataXMLManager.ID))).getAnswers().get(Integer.parseInt(params.get(DataXMLManager.ANSWER)));
			result = result.replace(DATA_PLACEHOLDER, "Your answer was" + answer);
		}
		else {
			result = htmlTemaplate.replace(STATUS_PLACEHOLDER, "Error!");
			result = result.replace(DATA_PLACEHOLDER, "Error while trying to answer poll, maybe deleted or alredy completed");
		}
		
		return result;
	}

}
