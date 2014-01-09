import java.util.Date;

public class Task {
	public static final String Time_Is_Due = "time is due";
	public static final String Completed = "completed";
	public static final String In_Progress = "in progress";

	public static String ExpiredTaskMassage = "Expired Task";
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

		SMTPMail.sendSMTPMail(taskCreator, taskCreator, ExpiredTaskMassage, taskCreator, "The task: " + title
				+ " hadn't been completed on time.");

		SMTPMail.sendSMTPMail(taskCreator, rcpt, ExpiredTaskMassage, taskCreator,
				"You had falied to complete the task: " + title + " on time.");
		taskExpiredHadBeenNotify = true;
		status = Time_Is_Due;
	}

	public void handleNewTask() {

		String link = "<a href='" + ConfigFile.ServerName + "/task_reply.html?id=" + getId()
				+ "' >Mark as Completed</a>";
		SMTPMail.sendSMTPMail(taskCreator, rcpt, "Task: " + title, taskCreator, getContent() + "\n" + link);
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

	public void taskHadBeenClosed() {
		setCompleted(true);
		setStatus(Task.Completed);
		SMTPMail.sendSMTPMail(rcpt, taskCreator, "Task completion", rcpt, "The task: " + title + " had been completed");
	}
}