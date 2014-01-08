public class PollParticipant {

	private String userName;
	private boolean hadAnswer;

	public PollParticipant(String userName, boolean hadAnswer) {
		this.userName = userName;
		this.setHadAnswer(hadAnswer);
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
}
