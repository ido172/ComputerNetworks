
public class Program {

	public static void main(String argv[]) {

		// WebServer myServer = new WebServer();
		// myServer.runServer();
		
		Poll poll = new Poll(null, null, null, null, null, null, null, false);
		Task task = new Task(null, null, null, null, null, null, null, false, false);
		Reminder reminder = new Reminder(null, null, null, null, null, false);
		
		DataXMLManager dataBase = new DataXMLManager();
		dataBase.addTask(task);
		dataBase.addReminder(reminder);
		dataBase.addPoll(poll);
		
	}
}