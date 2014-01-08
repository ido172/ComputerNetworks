import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HttpParamsToTask {

	HashMap<String, String> params;
	DataBase dataBase;

	public HttpParamsToTask(DataBase dataBase, HashMap<String, String> params) {
		this.params = params;
		this.dataBase = dataBase;
	}

	public boolean isValidateNewReminder() {
		try {
			String subject = params.get(DataXMLManager.SUBJECT); 
			String content = params.get(DataXMLManager.CONTENT);
			String date = params.get(DataXMLManager.DATE); 
			String time = params.get(DataXMLManager.TIME);	
			return !(subject.isEmpty() || content.isEmpty()
					|| date.isEmpty() || !isValidDate(date)  || time.isEmpty() || !isValidTime(time));
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isValidateReminderEdit() {
		boolean result = false;
		if (params.containsKey(DataXMLManager.ID)) {
			int id = Integer.parseInt(params.get(DataXMLManager.ID)); 
			Reminder reminder = dataBase.retriveReminderByID(id);
			if (reminder != null) {
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
	
	public boolean createReminderInDataBase(String user) {
		Reminder reminder = createReminder(user);
		if (reminder != null) {
			dataBase.addReminder(reminder);
			return true;
		} else {
			return false;
		}
	}
	
	public void editReminderInDateBase(String user, int reminderId) {
		Reminder newReminder = createReminder(user);
		dataBase.editReminder(reminderId, newReminder);
	}

	private Reminder createReminder(String user) {
		
		String subject, content, date, time, parsedUser;
		
		Date parsedDate = new Date();
		try {
			subject = URLDecoder.decode(params.get(DataXMLManager.SUBJECT), "UTF-8");
			content = URLDecoder.decode(params.get(DataXMLManager.CONTENT), "UTF-8");
			date = URLDecoder.decode(params.get(DataXMLManager.DATE), "UTF-8");
			time = URLDecoder.decode(params.get(DataXMLManager.TIME), "UTF-8");
			parsedUser = URLDecoder.decode(user, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date+" "+time);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}
		
		return new Reminder(parsedUser, subject, new Date(), parsedDate, content, false, 1);
	}
	

	
	public boolean isEditRequest() {
		return params.containsKey("edit") && params.get("edit").equals("true");
	}

	public boolean isDeleteRequest() {
		return params.containsKey("delete");
	}

	public void deleteReminderInDateBase(int id) {
		dataBase.deleteReminder(id);
	}
}
