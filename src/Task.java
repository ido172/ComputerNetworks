import java.util.Date;

public class Task {

	public static String ExpiredTaskMassage = "The time to complete this task had expired.";
	private String taskCreator;
	private String title;
	private Date dateOfCreation;
	private Date dueDate;
	private String status;
	private String content;
	private String rcpt;
	private boolean isCompleted;
	private boolean taskExpiredHadBeenNotify;
	private int id;

	public Task(String taskCreator, String title, Date dateOfCreation, Date dueDate, String status, String content,
			String rcpt, boolean isCompleted, boolean taskExpiredHadBeenNotify, int id) {
		this.taskCreator = taskCreator;
		this.title = title;
		this.dateOfCreation = dateOfCreation;
		this.dueDate = dueDate;
		this.status = status;
		this.content = content;
		this.rcpt = rcpt;
		this.isCompleted = isCompleted;
		this.taskExpiredHadBeenNotify = taskExpiredHadBeenNotify;
		this.id = id;
		handleNewTask();
	}

	public void taskHadBeenCompleted() {
		// //????????
		isCompleted = true;
		SMTPMail.sendSMTPMail(rcpt, taskCreator, "Task had been completed", rcpt, "The task:\n\n" + content
				+ "\nHad been completed");
	}

	public void handleExpiredTask() {

		SMTPMail.sendSMTPMail(taskCreator, taskCreator, ExpiredTaskMassage, taskCreator,
				"This is the task that her time had expired:\n" + content);

		SMTPMail.sendSMTPMail(taskCreator, rcpt, ExpiredTaskMassage, taskCreator,
				"This is the task that her time had expired:\n" + content);
		taskExpiredHadBeenNotify = true;
	}

	public void handleNewTask() {

		SMTPMail.sendSMTPMail(taskCreator, rcpt, title, taskCreator, "New task:\n" + "link\n" + content + "\n"
				+ "Task status: " + status + ".");
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRcpt() {
		return rcpt;
	}

	public void setRcpt(String rcpt) {
		this.rcpt = rcpt;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isTaskExpiredHadBeenNotify() {
		return taskExpiredHadBeenNotify;
	}

	public void setTaskExpiredHadBeenNotify(boolean taskExpiredHadBeenNotify) {
		this.taskExpiredHadBeenNotify = taskExpiredHadBeenNotify;
	}

	public static String getExpiredTaskMassage() {
		return ExpiredTaskMassage;
	}

	public static void setExpiredTaskMassage(String expiredTaskMassage) {
		ExpiredTaskMassage = expiredTaskMassage;
	}

	public String getTaskCreator() {
		return taskCreator;
	}

	public void setTaskCreator(String taskCreator) {
		this.taskCreator = taskCreator;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}