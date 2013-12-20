import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class RequestParser {

	public static final String METHOD = "method";
	public static final String LOCATION = "location";
	public static final String PROTOCOL = "protocol";
	
	private StringBuilder requestsHeaderPrinter = null;
	private HashMap<String, String> resultMap = null;
	private HashMap<String, String> parametersMap = null;
	private HttpRequestHandler httRequest = null;
	
	public HashMap<String, String> getParametersMap() {
		return parametersMap;
	}
	
	public RequestParser(HttpRequestHandler httRequest) {
		requestsHeaderPrinter = new StringBuilder();
		this.httRequest = httRequest;
	}

	public HashMap<String, String> getParsedRequest(BufferedReader inFromUser) throws SocketTimeoutException, IOException  {
		resultMap = new HashMap<String, String>();
		parametersMap = new HashMap<String, String>();

		String line = inFromUser.readLine();
		// if end of the stream has been reached notify process thread
		if (line == null) { 
			httRequest.isTerminatedByClient = true;
			return null;
		}
		requestsHeaderPrinter.append(line + "\n");

		// Get first line
		String[] splitedLine = line.split(" ");
		if (splitedLine.length != 3) {
			return null;
		}

		resultMap.put(METHOD, splitedLine[0]);
		if (splitedLine[1].contains("?")) { //Check for URL params
			
			int q = splitedLine[1].indexOf("?");
			String dir = splitedLine[1].substring(0, q);
			String params = splitedLine[1].substring(q + 1, splitedLine[1].length());
			resultMap.put(LOCATION, dir);
			addToPramsMap(params);
			
		} else {
			resultMap.put(LOCATION, splitedLine[1]);
		}

		resultMap.put(PROTOCOL, splitedLine[2]);

		// Get all Headers
		while ((line = inFromUser.readLine()) != null && !line.isEmpty()) {
			splitedLine = line.split(": ");
			if (splitedLine.length != 2) {
				return null;
			}
			requestsHeaderPrinter.append(line + "\n");
			resultMap.put(splitedLine[0].toLowerCase(), splitedLine[1].toLowerCase());
		}

		// If POST request get params from request body
		if (resultMap.get(METHOD).equals(HttpRequestHandler.HTTP_METHOD_POST)
				&& resultMap.containsKey(HttpHeaders.HEADER_CONTENT_LENGTH.toLowerCase())) {
			StringBuilder bodyString = new StringBuilder();
			int len = Integer.parseInt(resultMap.get(HttpHeaders.HEADER_CONTENT_LENGTH.toLowerCase()));
			for (int i = 0; i < len; i++) {
				bodyString.append((char)inFromUser.read());
			}
			addToPramsMap(bodyString.toString());
		}

		return resultMap;
	}
	
	public String getRequest() {
		return requestsHeaderPrinter.toString();
	}
	
	private void addToPramsMap(String params) {
		try {
			String[] allParams = params.split("&");
	
			for (int i = 0; i < allParams.length; i++) {
				String[] keyAndValue = allParams[i].split("=");
				if (!parametersMap.containsKey(keyAndValue[0])) {
					if (keyAndValue.length == 1) { 
						parametersMap.put(keyAndValue[0], "");
					} else {
						parametersMap.put(keyAndValue[0], keyAndValue[1]);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error while parsing params!");
			e.printStackTrace();
		}
	}
}
