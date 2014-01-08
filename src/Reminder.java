import java.util.Date;

public class Reminder {

	private String user;
	private Date dateOfCreation;
	private Date dateOfReminding;
	private String content;
	private String title;
	private boolean hadBeenSend;
	private int id;

	public Reminder(String user, String title, Date dateOfCreation, Date dateOfReminding, String content,
			boolean hadBeenSend, int id) {
		this.user = user;
		this.dateOfCreation = dateOfCreation;
		this.dateOfReminding = dateOfReminding;
		this.content = content;
		this.title = title;
		this.hadBeenSend = hadBeenSend;
		this.id = id;
	}

	public Reminder(Reminder reminder) {
		this.user = reminder.user;
		this.dateOfCreation = reminder.dateOfCreation;
		this.dateOfReminding = reminder.dateOfReminding;
		this.content = reminder.content;
		this.title = reminder.title;
		this.hadBeenSend = reminder.hadBeenSend;
	}

	public void sendReminder() {
		SMTPMail.sendSMTPMail(user, user, title, user, content);
		this.hadBeenSend = true;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getHadBennSend() {
		return hadBeenSend;
	}

	public void setHadBennSend(Boolean hadBennSend) {
		this.hadBeenSend = hadBennSend;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public Date getDateOfReminding() {
		return dateOfReminding;
	}

	public void setDateOfReminding(Date dateOfReminding) {
		this.dateOfReminding = dateOfReminding;
	}

	public boolean isHadBeenSend() {
		return hadBeenSend;
	}

	public void setHadBeenSend(boolean hadBeenSend) {
		this.hadBeenSend = hadBeenSend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
