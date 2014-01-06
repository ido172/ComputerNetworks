import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpResponseMaker {
	private HttpRequestHandler httpRequest = null;
	private HTMLCreator htmlCreator;

	public HttpResponseMaker(HttpRequestHandler httpRequest) {
		this.httpRequest = httpRequest;
		htmlCreator = new HTMLCreator();
	}

	public HttpResponse handleGETRequest(String root, String defaultPage) throws IOException {

		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION);
		String mailInCookie = getMailCookie();
		String response;
		String cookie = null;
		if ((location.length() == 1 || location.equals("/index.html")) && mailInCookie == null) { // location is "/" no cookie
			location = root + defaultPage;
			response = HttpResponse.RESPONSE_200_OK;
		} else if (location.equals("/index.html") && httpRequest.httpRequestParams.containsKey("logout")) {
			location = root + location.substring(1);
			response = HttpResponse.RESPONSE_200_OK;
			cookie = "";
		} else if ((location.length() == 1 || location.equals("/index.html") || location.contains("submit")) && mailInCookie != null) { // has cookie
			location = root + "main.html"; //!!!1explain this!!!!!!
			response = HttpResponse.RESPONSE_302_REDIRECT;
		}  else if (mailInCookie == null) {
			location = root + defaultPage;
			response = HttpResponse.RESPONSE_302_REDIRECT;
		} else {
			location = root + location.substring(1);
			response = HttpResponse.RESPONSE_200_OK;
		}

		return responseFromFile(response, location, cookie);
	}
	
	
	public HttpResponse handlePOSTRequest(String root, String defaultPage) throws IOException {

		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION);
		
		String response = HttpResponse.RESPONSE_200_OK;
		String userName = null;
		HttpParamsToTask handler = new HttpParamsToTask(httpRequest.dataBase, httpRequest.httpRequestParams);
		if (location.equals("/main.html") &&  httpRequest.httpRequestParams.containsKey("login")) { // location is "/" no cookie
			location = root + location.substring(1);
			response = HttpResponse.RESPONSE_200_OK;
			userName = httpRequest.httpRequestParams.get("login");
		} else if (location.equals("/submit_reminder.html") && handler.isValidateReminder() ) {
			String user = getMailCookie();
			if (handler.isEditRequest()) {
				handler.editReminderInDateBase(user);
			} else if (handler.isDeleteRequest()) {
				handler.deleteReminderInDateBase(user);
			} else {
				handler.createReminderInDataBase(user);
			}
			location = root + "reminders.html"; 
			response = HttpResponse.RESPONSE_302_REDIRECT;
		} else {
			location = root + location.substring(1);
			response = HttpResponse.RESPONSE_200_OK;
		}

		return responseFromFile(response, location, userName);
	}

	private HttpResponse responseFromFile(String responseCode, String fileLoction, String cookieParam) throws IOException {
		try {
			File requestedFile = new File(fileLoction);
			String fileName = requestedFile.getName();
			String contentType = FileTypeToContentType.convert(fileName);
			String responseBody = FileToString.readFile(requestedFile);
			switch (fileName.toLowerCase()) {
				case "reminders.html":
					responseBody = htmlCreator.createRemainderPage(getMailCookie(), responseBody, httpRequest.dataBase);
					break;
				case "tasks.html":
					break;
				case "polls.html":
					break;
			}
			return makeHttpResponse(responseCode, responseBody, contentType, cookieParam);

		} catch (FileNotFoundException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		} catch (NullPointerException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		}
	}

	public HttpResponse makeOptionsResponse() {
		httpRequest.isHeadRequest = true;
		HttpResponse httpResponse = makeHttpResponse(HttpResponse.RESPONSE_200_OK, "", HttpHeaders.CONTENT_TYPE_MESSAGE, null);
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
		return makeHttpResponse(responseType, responseBody, HttpHeaders.CONTENT_TYPE_HTML, null);
	}
	
	public HttpResponse makeHttpResponse(String responseType, String responseBody, String contentType, String cookie) {
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
		httpResponse.appendHeader(HttpHeaders.HEADER_CONTENT_LENGTH, length);
		httpResponse.appendHeader(HttpHeaders.HEADER_CONNECTION, connection);
		if (cookie!= null && cookie.length() == 0) {
			httpResponse.appendHeader(HttpHeaders.HEADER_SET_COOKIE, HttpHeaders.COOKIE_PARAM + "=" + HttpHeaders.COOKIE_DELETED_VALUE);
		} else if (cookie != null) {
			httpResponse.appendHeader(HttpHeaders.HEADER_SET_COOKIE, HttpHeaders.COOKIE_PARAM + "=" + cookie);
		}
		
		httpResponse.appendBody(responseBody);
		return httpResponse;
	}

	private String getMailCookie() {
		String result = null;
		if (httpRequest.parsedHttpRequest.containsKey(HttpHeaders.HEADER_COOKIE.toLowerCase())
				&& httpRequest.parsedHttpRequest.get(HttpHeaders.HEADER_COOKIE.toLowerCase()).contains(HttpHeaders.COOKIE_PARAM)) {
			String cookie = httpRequest.parsedHttpRequest.get(HttpHeaders.HEADER_COOKIE.toLowerCase());
			result = (cookie.split("=")[1].equals(HttpHeaders.COOKIE_DELETED_VALUE)) ? null : cookie.split("=")[1] ;
		}

		return result;
	}

}
