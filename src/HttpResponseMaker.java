import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpResponseMaker {
	private HttpRequestHandler httpRequest = null;
	private HTMLCreator htmlCreator;
	private String defaultPage;
	private String root;

	public HttpResponseMaker(HttpRequestHandler httpRequest) {
		this.httpRequest = httpRequest;
		htmlCreator = new HTMLCreator();
		defaultPage = httpRequest.defaultPage;
		root = httpRequest.root;
	}

	public HttpResponse handleGETRequest() throws IOException {

		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION).substring(1);
		String mailInCookie = getMailCookie();
		String response;
		String cookie = null;
		if (mailInCookie == null) { // no cookie
			location = defaultPage;
			if (location.length() == 0 || location.equals(defaultPage)) {
				response = HttpResponse.RESPONSE_200_OK;
			} else {
				response = HttpResponse.RESPONSE_302_REDIRECT;
			}
		} else { // has cookie
			if (location.equals(defaultPage) && httpRequest.httpRequestParams.containsKey("delete")) { // logout
				response = HttpResponse.RESPONSE_200_OK;
				cookie = "";
			} else if (location.length() == 0 || location.equals("/" + defaultPage) || location.contains("submit")) {
				// asking for a submit page in GET method will redirect you to main.
				location = "main.html";
				response = HttpResponse.RESPONSE_302_REDIRECT;
			} else { // regular erquest
				response = HttpResponse.RESPONSE_200_OK;
			}
		}

		return responseFromFile(response, location, cookie);
	}

	public HttpResponse handlePOSTRequest() throws IOException {

		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION);

		String response = HttpResponse.RESPONSE_200_OK;
		String userName = null;
		String user = getMailCookie();
		HttpParamsToTask handler = new HttpParamsToTask(httpRequest.dataBase, httpRequest.httpRequestParams);
		if (user == null) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, HttpResponse.RESPONSE_404_NOT_FOUND);
		}

		if (location.equals("main.html") && httpRequest.httpRequestParams.containsKey("login")) { // location is "/" no
																									// cookie
			response = HttpResponse.RESPONSE_200_OK;
			userName = httpRequest.httpRequestParams.get("login");
		} else if (location.equals("/submit_reminder.html")) {
			if (handler.isEditRequest()) {
				handler.editReminderInDateBase(user,
						Integer.parseInt(httpRequest.httpRequestParams.get(DataXMLManager.ID)));
			} else if (handler.isDeleteRequest()) {
				handler.deleteReminderInDateBase(Integer.parseInt(httpRequest.httpRequestParams.get(DataXMLManager.ID)));
			} else {
				handler.createReminderInDataBase(user);
			}
			location = "reminders.html";
			response = HttpResponse.RESPONSE_302_REDIRECT;
		} else {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, HttpResponse.RESPONSE_404_NOT_FOUND);
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
			switch (fileName.toLowerCase()) {
			case "reminders.html":
				responseBody = htmlCreator.createRemainderPage(getMailCookie(), responseBody, httpRequest.dataBase);
				break;
			case "tasks.html":
				break;
			case "polls.html":
				break;
			}

			return makeHttpResponse(responseCode, responseBody, contentType, cookieParam, fileLoction);

		} catch (FileNotFoundException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		} catch (NullPointerException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		}
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

	private String getMailCookie() {
		String result = null;
		if (httpRequest.parsedHttpRequest.containsKey(HttpHeaders.HEADER_COOKIE.toLowerCase())
				&& httpRequest.parsedHttpRequest.get(HttpHeaders.HEADER_COOKIE.toLowerCase()).contains(
						HttpHeaders.COOKIE_PARAM)) {
			String cookie = httpRequest.parsedHttpRequest.get(HttpHeaders.HEADER_COOKIE.toLowerCase());
			result = (cookie.split("=")[1].equals(HttpHeaders.COOKIE_DELETED_VALUE)) ? null : cookie.split("=")[1];
		}

		return result;
	}

}
