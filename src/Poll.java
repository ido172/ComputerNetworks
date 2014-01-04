import java.util.Date;
import java.util.LinkedList;

public class Poll {

	private String user;
	private Date date;
	private String question;
	private String isCompleted;
	private LinkedList<PollParticipant> rcpts;
	private LinkedList<String> answers;

	public Poll(String user, Date date, String time, String question, String isCompleted,
			LinkedList<PollParticipant> rcpts, LinkedList<String> answers) {
		this.user = user;
		this.date = date;
		this.question = question;
		this.isCompleted = isCompleted;
		this.rcpts = rcpts;
		this.answers = answers;
	}

	public void handleNewPoll() {
		for (PollParticipant rcpt : rcpts) {
			StringBuilder data = new StringBuilder();
			data.append(question + SMTPMail.CRLF);
			data.append("<a href=\"polls_review.html?user=" + user + "&date=" + date.toString() + "&question="
					+ question + "\">Link to poll</a>");

			SMTPMail.sendSMTPMail(user, rcpt.getUserName(), "Poll", user, data.toString());
		}
	}
	
	public void pollParticipantHadAnswer() {
		
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(String isCompleted) {
		this.isCompleted = isCompleted;
	}

	public LinkedList<PollParticipant> getRcpts() {
		return rcpts;
	}

	public void setRcpts(LinkedList<PollParticipant> rcpts) {
		this.rcpts = rcpts;
	}

	public LinkedList<String> getAnswers() {
		return answers;
	}

	public void setAnswers(LinkedList<String> answers) {
		this.answers = answers;
	}
}
