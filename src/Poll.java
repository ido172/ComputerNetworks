import java.util.Date;
import java.util.LinkedList;

public class Poll {

	private String pollCreator;
	private String title;
	private Date dateOfCreation;
	private String subject;
	private String question; // Content
	private LinkedList<String> answers;
	private LinkedList<PollParticipant> rcpts;
	private boolean isCompleted;

	public Poll(String pollCreator, String title, Date dateOfCreation, String subject, String question,
			LinkedList<String> answers, LinkedList<PollParticipant> rcpts, boolean isCompleted) {
		this.pollCreator = pollCreator;
		this.title = title;
		this.dateOfCreation = dateOfCreation;
		this.subject = subject;
		this.question = question;
		this.answers = answers;
		this.rcpts = rcpts;
		this.isCompleted = isCompleted;
	}

	public void handleNewPoll() {
		for (PollParticipant rcpt : rcpts) {
			StringBuilder data = new StringBuilder();
			data.append(question + SMTPMail.CRLF);
			data.append("<a href=\"polls_reply.html?user=" + pollCreator + "&date=" + dateOfCreation.toString()
					+ "&question=" + question + "\">Link to poll</a>");

			SMTPMail.sendSMTPMail(pollCreator, rcpt.getUserName(), "Poll" + subject, pollCreator, data.toString());
		}
	}

	public String getPollCreator() {
		return pollCreator;
	}

	public void setPollCreator(String pollCreator) {
		this.pollCreator = pollCreator;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public LinkedList<String> getAnswers() {
		return answers;
	}

	public void setAnswers(LinkedList<String> answers) {
		this.answers = answers;
	}

	public LinkedList<PollParticipant> getRcpts() {
		return rcpts;
	}

	public void setRcpts(LinkedList<PollParticipant> rcpts) {
		this.rcpts = rcpts;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

}
