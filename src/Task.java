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
	}

	public void taskHadBeenCompleted() {
		isCompleted = true;
		status = Completed;
		StringBuilder mailContent = new StringBuilder();
		mailContent.append("The task");
		mailContent.append(SMTPMail.CRLF);
		mailContent.append(content);
		mailContent.append(SMTPMail.CRLF + "Had been completed");

		SMTPMail.sendSMTPMail(rcpt, taskCreator, "Task had been completed", rcpt, mailContent.toString());
	}

	public void handleExpiredTask() {
		taskExpiredHadBeenNotify = true;
		status = Time_Is_Due;

		StringBuilder mailContentTaskCreator = new StringBuilder();
		mailContentTaskCreator.append("The task: ");
		mailContentTaskCreator.append(title);
		mailContentTaskCreator.append(" hadn't been completed on time.");

		SMTPMail.sendSMTPMail(taskCreator, taskCreator, ExpiredTaskMassage, taskCreator,
				mailContentTaskCreator.toString());

		StringBuilder mailContentTaskRcpt = new StringBuilder();
		mailContentTaskRcpt.append("You had falied to complete the task: ");
		mailContentTaskRcpt.append(title);
		mailContentTaskRcpt.append(" on time");
		SMTPMail.sendSMTPMail(taskCreator, rcpt, ExpiredTaskMassage, taskCreator, mailContentTaskRcpt.toString());
	}

	public void handleNewTask() {

		StringBuilder mailContent = new StringBuilder();
		mailContent.append("New task: " + content + SMTPMail.CRLF);
		mailContent.append("To mark the task: ");
		mailContent.append(title);
		mailContent.append(" as completed press this link http://");
		mailContent.append(ConfigFile.ServerName);
		mailContent.append(":" + ConfigFile.SERVERPORT);
		mailContent.append("/task_reply.html?id=");
		mailContent.append(getId());

		String subject = "Task - " + title;
		SMTPMail.sendSMTPMail(taskCreator, rcpt, subject, taskCreator, mailContent.toString());
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