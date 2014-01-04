
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class HTMLCreator {

	private static String DELETE_KEY = "delete";
	private static String EDIT_KEY = "edit";
	private static String DATA_PLACEHOLDER = "[DATA_PLACEHOLDER]";
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat justDateFormat;
	private SimpleDateFormat justTimeFormat;
	
	public HTMLCreator() {
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		justDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		justTimeFormat = new SimpleDateFormat("HH:mm");
	}
	public String createRemainderPage(String userName, String htmlTemaplate, DataBase dataBase) {
		StringBuilder content = new StringBuilder();
		LinkedList<Reminder> reminders = dataBase.retrieveReminderByUser(userName);
		for (Reminder reminder : reminders) {
			content.append("<tr>");
			content.append("<td>"+reminder.getTitle()+"</td>");
			content.append("<td>" + getDateInRightformat(reminder.getDateOfCreation()) + "</td>");
			content.append("<td>" +	getDateInRightformat(reminder.getDateOfReminding()) + "</td>");
			content.append("<td><a data-role='button' href='reminder_editor.html?" + builderReminderIdParams(reminder) + "'>Edit</a></td>");
			content.append("<td><form action='submit_reminder.html' method='POST'><input type='submit' value='delete' /><input type='hidden' name='"+DELETE_KEY+"'/></form></td>");
			content.append("</tr>");
		}
		
		return htmlTemaplate.replace(DATA_PLACEHOLDER, content.toString());
	}
	
	private String builderReminderIdParams(Reminder reminder) {
		StringBuilder urlParams = new StringBuilder();
		urlParams.append("subject=");
		urlParams.append(reminder.getTitle());
		urlParams.append("&");
		urlParams.append("content=");
		urlParams.append(reminder.getContent());
		urlParams.append("&");
		urlParams.append("date=");
		urlParams.append(getJustDate(reminder.getDateOfReminding()));
		urlParams.append("&");
		urlParams.append("time=");
		urlParams.append(getJustTime(reminder.getDateOfReminding()));
		return urlParams.toString();
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

}
