import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;

public class HttpResponseMaker {
	private HTMLCreator htmlCreator;
	private HttpRequestHandler httpRequest;
	private String defaultPage;
	private String root;
	private DataBase dataBase;
	private HashMap<String, String> params;
	private HashMap<String, String> request;

	public HttpResponseMaker(HttpRequestHandler httpRequest) {
		this.httpRequest= httpRequest; 
		htmlCreator = new HTMLCreator();
		defaultPage = httpRequest.defaultPage;
		root = httpRequest.root;
		dataBase = httpRequest.dataBase;
		params = httpRequest.httpRequestParams;
		request = httpRequest.parsedHttpRequest;
	}

	public HttpResponse handleGETRequest() throws IOException {

		String location = request.get(RequestParser.LOCATION).substring(1);
		if (location.length() == 0) {
			location = defaultPage;
		}
		String mailInCookie = getMailCookie();
		String response;
		String cookie = null;
		if (location.contains("replay.html")) {
			HttpParamsToTask handler = new HttpParamsToTask(dataBase, params);
			if (location.equals("task_reply.html") && handler.isValidTaskReply()) {
				location = "tasks.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
				handler.closeTask();
			} else if(location.equals("poll_reply.html") && handler.isValidPollReply()) {
				location = "polls.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
				handler.setPollAnswer();
			} else {
				return makeErrorPageResponse(HttpResponse.RESPONSE_400_BAD_REQUEST, HttpResponse.RESPONSE_400_BAD_REQUEST);
			}
		}
		else if (mailInCookie == null && location.contains(".html")) { // no cookie
			if (location.equals(defaultPage)) {
				response = HttpResponse.RESPONSE_200_OK;
			} else {
				response = HttpResponse.RESPONSE_302_REDIRECT;
			}
		} else {
			if (location.equals(defaultPage) && params.containsKey("logout")) { // logout
				response = HttpResponse.RESPONSE_200_OK;
				cookie = "";
			} else if (location.equals(defaultPage) || location.contains("submit")) {
				// asking for a submit page or index in GET method will redirect you to main.
				location = "main.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else { // regular erquest
				response = HttpResponse.RESPONSE_200_OK;
			}
		}

		return responseFromFile(response, location, cookie);
	}

	public HttpResponse handlePOSTRequest() throws IOException {

		String location = request.get(RequestParser.LOCATION).substring(1);

		String response = HttpResponse.RESPONSE_200_OK;
		String userName = null;
		String user = getMailCookie();
		HttpParamsToTask handler = new HttpParamsToTask(dataBase, params);
		if (user == null) {
			if (location.equals("main.html") && params.containsKey("login")) { 
				response = HttpResponse.RESPONSE_200_OK;
				try {
					userName = URLDecoder.decode(params.get("login"), "UTF-8");
				} catch(Exception e) {
					return makeErrorPageResponse(HttpResponse.RESPONSE_400_BAD_REQUEST, HttpResponse.RESPONSE_400_BAD_REQUEST);
				}
			} else {
				return makeErrorPageResponse(HttpResponse.RESPONSE_400_BAD_REQUEST, HttpResponse.RESPONSE_400_BAD_REQUEST);	
			}
			
			//Reminders
		} else if (location.equals("submit_reminder.html")) {
			if (handler.isDeleteRequest()  && handler.isValidDeleteRequest(user)) {
				handler.deleteReminderInDateBase();
				location = "reminders.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else if (handler.isEditRequest() && handler.isValidReminderEdit(user)) {
				handler.editReminderInDateBase(user);
				location = "reminders.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else if (handler.isValidNewReminder()) {
				handler.createNewReminderInDataBase(user);
				location = "reminders.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else {
				response = HttpResponse.RESPONSE_200_OK;	//Error page
			}
			
		} else if (location.equals("submit_task.html")) {
			if (handler.isDeleteRequest() && handler.isValidDeleteRequest(user)) {
				handler.deleteTaskInDateBase();
				location = "tasks.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else if (handler.isValidNewTask()) {
				handler.createNewTaskInDataBase(user);
				location = "tasks.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else {
				response = HttpResponse.RESPONSE_200_OK;	//Error page
			}
		} else if (location.equals("submit_poll.html")) {
			if (handler.isDeleteRequest() && handler.isValidDeleteRequest(user)) {
				handler.deleteTaskInDateBase();
				location = "tasks.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else if (handler.isValidNewTask()) {
				handler.createNewTaskInDataBase(user);
				location = "tasks.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else {
				response = HttpResponse.RESPONSE_200_OK;
			}
		} else {
			return makeErrorPageResponse(HttpResponse.RESPONSE_400_BAD_REQUEST, HttpResponse.RESPONSE_400_BAD_REQUEST);
		}

		return responseFromFile(response, location, userName);
	}

	private HttpResponse responseFromFile(String responseCode, String fileLoction, String cookieParam)
			throws IOException {
		try {
			File requestedFile = new File(root + fileLoction);
			String fileName = requestedFile.getName();
			String contentType = FileTypeToContentType.convert(fileName);
			String responseBody = FileToString.readFile(requestedFile);
			String name = (cookieParam != null) ? cookieParam : getMailCookie();
			if (name != null && contentType.equals(HttpHeaders.CONTENT_TYPE_HTML)) {
				responseBody = htmlCreator.addUserNameToPage(name, responseBody);
			}
			switch (fileName.toLowerCase()) {
				case "reminders.html":
					responseBody = htmlCreator.createRemaindersPage(getMailCookie(), responseBody, dataBase);
					break;
				case "tasks.html":
					responseBody = htmlCreator.createTasksPage(getMailCookie(), responseBody, dataBase);
					break;
				case "polls.html":
					responseBody = htmlCreator.createPollsPage(getMailCookie(), responseBody, dataBase);
					break;
			}

			return makeHttpResponse(responseCode, responseBody, contentType, cookieParam, fileLoction);

		} catch (FileNotFoundException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		} catch (NullPointerException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		}
	}

	public HttpResponse makeHttpResponse(String responseType, String responseBody, String contentType, String cookie,
			String loaction) {
		String protocol = "";
		String connection = "";
		String length = (httpRequest.isHeadRequest) ? "0" : Integer.toString(responseBody.length());
		if (httpRequest.isKeepAlive) {
			protocol = HttpRequestHandler.PROTOCOL_HTTP11;
			connection = HttpHeaders.CONNECTION_KEEP_ALIVE;
		} else {
			protocol = HttpRequestHandler.PROTOCOL_HTTP10;
			connection = HttpHeaders.CONNECTION_CLOSE;
		}

		HttpResponse httpResponse = new HttpResponse(responseType, protocol);
		httpResponse.appendHeader(HttpHeaders.HEADER_CONTENT_TYPE, contentType);
		if (httpRequest.isChunked) {
			httpResponse.appendHeader(HttpHeaders.HEADER_TRANSFER_ENCODING, HttpHeaders.TRANSFER_ENCODING_CHUNKED);
		}

		if (responseType.equals(HttpResponse.RESPONSE_302_REDIRECT)) {
			httpResponse.appendHeader(HttpHeaders.HEADER_LOCATION, loaction);
		}

		httpResponse.appendHeader(HttpHeaders.HEADER_CONTENT_LENGTH, length);
		httpResponse.appendHeader(HttpHeaders.HEADER_CONNECTION, connection);
		
		if (cookie != null && cookie.length() == 0) {
			httpResponse.appendHeader(HttpHeaders.HEADER_SET_COOKIE, HttpHeaders.COOKIE_PARAM + "="
					+ HttpHeaders.COOKIE_DELETED_VALUE);
		} else if (cookie != null) {
			httpResponse.appendHeader(HttpHeaders.HEADER_SET_COOKIE, HttpHeaders.COOKIE_PARAM + "=" + cookie);
		}

		httpResponse.appendBody(responseBody);
		return httpResponse;
	}
	
	public HttpResponse makeOptionsResponse() {
		httpRequest.isHeadRequest = true;
		HttpResponse httpResponse = makeHttpResponse(HttpResponse.RESPONSE_200_OK, "",
				HttpHeaders.CONTENT_TYPE_MESSAGE, null, null);
		StringBuilder sb = new StringBuilder();
		int len = HttpRequestHandler.ALLOWED_METHODS.length;
		for (int i = 0; i < len; i++) {
			sb.append(HttpRequestHandler.ALLOWED_METHODS[i]);
			if (i < len - 1) {
				sb.append(", ");
			}
		}

		httpResponse.appendHeader(HttpHeaders.HEADER_ALLOW, sb.toString());
		return httpResponse;
	}

	public HttpResponse makeErrorPageResponse(String responseType, String bodyText) {
		httpRequest.isKeepAlive = false;
		httpRequest.isChunked = false;
		String responseBody = HttpResponse.ERROR_PAGE_TEMPLATE;
		responseBody = responseBody.replace(HttpResponse.PLCAEHOLDER_TITLE, responseType);
		responseBody = responseBody.replace(HttpResponse.PLCAEHOLDER_BODY, bodyText);
		return makeHttpResponse(responseType, responseBody, HttpHeaders.CONTENT_TYPE_HTML, null, null);
	}
	private String getMailCookie() {
		String result = null;
		if (request.containsKey(HttpHeaders.HEADER_COOKIE.toLowerCase())
				&& request.get(HttpHeaders.HEADER_COOKIE.toLowerCase()).contains(
						HttpHeaders.COOKIE_PARAM)) {
			String cookie = request.get(HttpHeaders.HEADER_COOKIE.toLowerCase());
			try {
				result = (cookie.split("=")[1].equals(HttpHeaders.COOKIE_DELETED_VALUE)) ? null : cookie.split("=")[1];
			} catch (Exception e) {
				result = null;
			}
		}

		return result;
	}

}
