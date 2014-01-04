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
					||date.isEmpty() || !isValidDate(date)  || time.isEmpty() || !isValidTime(time));
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isValidDate(String date) {
			return date.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d");
			
	}
	
	private boolean isValidTime(String time) {
		return time.matches("\\d\\d:\\d\\d");
	}
	
	public void createReminderInDataBase(String user) {
		dataBase.addReminder(createReminder(user));
	}
	
	public void editReminderInDateBase(String user) {
		Reminder newReminder = createReminder(user);
		Reminder oldReminder = createOriginalReminder(user);
	}

	private Reminder createReminder(String user) {
		String subject = params.get(DataXMLManager.SUBJECT); 
		String content = params.get(DataXMLManager.CONTENT);
		String date = params.get(DataXMLManager.DATE);
		String time = params.get(DataXMLManager.TIME);
		Date parsedDate = new Date();
		try {
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date+" "+time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new Reminder(user, subject, new Date(), parsedDate, content, false);
	}
	
	private Reminder createOriginalReminder(String user) {
		String subject = params.get("original_" + DataXMLManager.SUBJECT); 
		String content = params.get("original_" + DataXMLManager.CONTENT);
		String date = params.get("original_" + DataXMLManager.DATE);
		String time = params.get("original_" + DataXMLManager.TIME);
		Date parsedDate = new Date();
		try {
			parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date+" "+time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new Reminder(user, subject, new Date(), parsedDate, content, false);
	}
	
	public boolean isEditRequest() {
		return params.containsKey("is_edit");
	}
}
