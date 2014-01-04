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

	public boolean isValidateReminder() {
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
	
	public void createReminderInDataBase(String user) {
		dataBase.addReminder(createReminder(user, ""));
	}
	
	public void editReminderInDateBase(String user) {
		Reminder newReminder = createReminder(user, "");
		Reminder oldReminder = createReminder(user, "original_");
		dataBase.editReminder(newReminder, oldReminder);
	}

	private Reminder createReminder(String user, String prefix) {
		
		try {
			String subject = URLDecoder.decode(params.get(prefix + DataXMLManager.SUBJECT), "UTF-8");
			String content = URLDecoder.decode(params.get(prefix + DataXMLManager.CONTENT), "UTF-8");
			String date = URLDecoder.decode(params.get(prefix + DataXMLManager.DATE), "UTF-8");
			String time = URLDecoder.decode(params.get(prefix + DataXMLManager.TIME), "UTF-8");
			Date parsedDate = new Date();
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date+" "+time);
			return new Reminder(user, subject, new Date(), parsedDate, content, false);
		} catch (UnsupportedEncodingException e) {
			String subject = params.get(prefix + DataXMLManager.SUBJECT);
			String content = params.get(prefix + DataXMLManager.CONTENT);
			String date = params.get(prefix + DataXMLManager.DATE);
			String time = params.get(prefix + DataXMLManager.TIME);
			Date parsedDate = new Date();
			try {
				parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date+" "+time);
				return new Reminder(user, subject, new Date(), parsedDate, content, false);
			} catch (ParseException ex) {
				ex.printStackTrace();
				return null;
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	
	public boolean isEditRequest() {
		return params.containsKey("edit") && params.get("edit").equals("true");
	}

	public boolean isDeleteRequest() {
		return params.containsKey("delete");
	}

	public void deleteReminderInDateBase(String user) {
		dataBase.deleteReminder(createReminder(user, ""));
	}
}
