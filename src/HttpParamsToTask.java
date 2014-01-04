import java.util.HashMap;

public class HttpParamsToTask {
	HashMap<String, String> params;

	public HttpParamsToTask(HashMap<String, String> params) {
		this.params = params;
	}

	public boolean isValidateReminder() {
		try {
			return !(params.get(DataXMLManager.SUBJECT).isEmpty() || params.get(DataXMLManager.CONTENT).isEmpty()
					|| params.get(DataXMLManager.DATE).isEmpty() || params.get(DataXMLManager.TIME).isEmpty());
		} catch (Exception e) {
			return false;
		}
	}

	public boolean createReminder() {
		try {
			return !(params.get(DataXMLManager.SUBJECT).isEmpty() || params.get(DataXMLManager.CONTENT).isEmpty()
					|| params.get(DataXMLManager.DATE).isEmpty() || params.get(DataXMLManager.TIME).isEmpty());
		} catch (Exception e) {
			return false;
		}
	}
}
