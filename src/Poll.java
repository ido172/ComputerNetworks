import java.util.Date;
import java.util.LinkedList;

public class Poll {

	private String pollCreator;
	private String title;
	private Date dateOfCreation;
	private String subject;
	private String question;
	private LinkedList<String> answers;
	private LinkedList<PollParticipant> rcpts;
	private boolean isCompleted;
	private int id;

	public Poll(String pollCreator, String title, Date dateOfCreation, String subject, String question,
			LinkedList<String> answers, LinkedList<PollParticipant> rcpts, boolean isCompleted, int id) {
		this.pollCreator = pollCreator;
		this.title = title;
		this.dateOfCreation = dateOfCreation;
		this.subject = subject;
		this.question = question;
		this.answers = answers;
		this.rcpts = rcpts;
		this.isCompleted = isCompleted;
		this.id = id;
		handleNewPoll();
	}

	public Poll duplicatePoll() {
		return new Poll(pollCreator, title, dateOfCreation, subject, question, answers, rcpts, isCompleted, id);
	}

	public void handleNewPoll() {

		String link = "";

		for (int i = 0; i < getRcpts().size(); i++) {

			StringBuilder data = new StringBuilder();
			data.append(question);
			data.append(SMTPMail.CRLF);

			for (int j = 0; j < answers.size(); j++) {

				link = "<a href='" + ConfigFile.ServerName + "/poll_reply.html?id=" + getId() + "&answer=" + j
						+ "&rcpt=" + i + "' >" + answers.get(j) + "</a>";

				data.append(question + SMTPMail.CRLF);
				data.append(link);
			}

			SMTPMail.sendSMTPMail(pollCreator, rcpts.get(i).getUserName(), "Poll" + subject, pollCreator,
					data.toString());
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void participantHadAnswer(int participantIndex, int answerIndex) { // // what about if the poll is completed?
		getAnswers().get(answerIndex);
		PollParticipant currParticipant = getRcpts().get(participantIndex);
		currParticipant.setHadAnswer(true);
		checkIfPollIsCompleted();
		StringBuilder data = new StringBuilder();
		data.append("The status of the poll:\n");

		for (PollParticipant participant : getRcpts()) {
			data.append("The participant: ");
			data.append(participant.getUserName());

			if (participant.isHadAnswer()) {
				data.append(" had answer the poll.");
			} else {
				data.append(" hadn't yet answer the poll.");
			}

			data.append("\n");
		}

		SMTPMail.sendSMTPMail(pollCreator, currParticipant.getUserName(), "Poll: " + subject, pollCreator, "");
	}

	private void checkIfPollIsCompleted() {
		// TODO Auto-generated method stub

	}
}
