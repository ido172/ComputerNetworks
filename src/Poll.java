import java.util.Date;
import java.util.LinkedList;

public class Poll {

	private String pollCreator;
	private Date dateOfCreation;
	private String subject;
	private String question;
	private LinkedList<String> answers;
	private LinkedList<PollParticipant> rcpts;
	private boolean isCompleted;
	private int id;

	public Poll(String pollCreator, Date dateOfCreation, String subject, String question, LinkedList<String> answers,
			LinkedList<PollParticipant> rcpts, boolean isCompleted, int id) {
		this.pollCreator = pollCreator;
		this.dateOfCreation = dateOfCreation;
		this.subject = subject;
		this.question = question;
		this.answers = answers;
		this.rcpts = rcpts;
		this.isCompleted = isCompleted;
		this.id = id;
	}

	public void sendMailsToParticipants() {

		String link = "";

		for (int i = 0; i < getRcpts().size(); i++) {

			StringBuilder mailContent = new StringBuilder();
			mailContent.append(question);

			for (int j = 0; j < answers.size(); j++) {

				link = "click on http://" + ConfigFile.ServerName + "/poll_reply.html?id=" + getId() + "&answer=" + j
						+ "&rcpt=" + i + " for the answer: " + answers.get(j);

				mailContent.append(link);
				mailContent.append(SMTPMail.CRLF);
			}

			SMTPMail.sendSMTPMail(pollCreator, rcpts.get(i).getUserName(), "Poll: " + subject, pollCreator,
					mailContent.toString());
		}
	}

	public void participantHadAnswer(int participantIndex, int answerIndex) {
		// TODO
		String answer = getAnswers().get(answerIndex);
		PollParticipant currParticipant = getRcpts().get(participantIndex);
		currParticipant.setParticipantReplay(answer);
		currParticipant.setHadAnswer(true);
		boolean pollHadBeenCompleted = checkIfPollIsCompleted();
		StringBuilder data = new StringBuilder();

		if (pollHadBeenCompleted) {
			data.append("The poll: ");
			data.append(getSubject());
			data.append(" had been completed" + SMTPMail.CRLF);
		}

		data.append("The status for the participants in the poll " + getSubject() + " is" + SMTPMail.CRLF);

		for (PollParticipant participant : getRcpts()) {
			data.append("The participant ");
			data.append(participant.getUserName());

			if (participant.isHadAnswer()) {
				data.append(" had answer the poll with this answer ");
				data.append(participant.getParticipantReplay());
				// data.append(SMTPMail.CRLF);
			} else {
				data.append(" hadnt yet answer the poll");
				// data.append(SMTPMail.CRLF);
			}
		}

		String pollStatus = "Status of the poll: " + subject;
		SMTPMail.sendSMTPMail(pollCreator, pollCreator, pollStatus, pollCreator, data.toString());
	}

	private boolean checkIfPollIsCompleted() {

		for (PollParticipant participant : getRcpts()) {
			if (!participant.isHadAnswer()) {
				return false;
			}
		}
		setCompleted(true);
		return true;
	}

	public String getPollCreator() {
		return pollCreator;
	}

	public void setPollCreator(String pollCreator) {
		this.pollCreator = pollCreator;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
