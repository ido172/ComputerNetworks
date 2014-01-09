public class PollParticipant {

	private String userName;
	private boolean hadAnswer;
	private String participantReplay;

	public PollParticipant(String userName, boolean hadAnswer, String participantReplay) {
		this.userName = userName;
		this.setHadAnswer(hadAnswer);
		this.setParticipantReplay(participantReplay);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isHadAnswer() {
		return hadAnswer;
	}

	public void setHadAnswer(boolean hadAnswer) {
		this.hadAnswer = hadAnswer;
	}

	public String getParticipantReplay() {
		return participantReplay;
	}

	public void setParticipantReplay(String participantReplay) {
		this.participantReplay = participantReplay;
	}
}
