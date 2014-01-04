import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

public class HttpResponseMaker {
	private HttpRequestHandler httpRequest = null;
	private boolean needCookie = true;

	public HttpResponseMaker(HttpRequestHandler httpRequest) {
		this.httpRequest = httpRequest;
	}

	public HttpResponse handleGETRequest(String root, String defaultPage) throws IOException {

		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION);
		String mailInCookie = getMailCookie();
		String response;
		if (location.length() == 1 && mailInCookie == null) { // location is "/" no cookie
			location = root + defaultPage;
			response = HttpResponse.RESPONSE_200_OK;
		} else if (location.length() == 1 || location.equals("/index.html")) { // has cookie
			location = root + "main.html";
			response = HttpResponse.RESPONSE_302_REDIRECT;
		}  else if (mailInCookie == null) {
			location = root + defaultPage;
			response = HttpResponse.RESPONSE_302_REDIRECT;
		} else {
			location = root + location.substring(1);
			response = HttpResponse.RESPONSE_200_OK;
		}

		try {
			File requestedFile = new File(location);
			String fileName = requestedFile.getName();
			String contentType = FileTypeToContentType.convert(fileName);
			String responseBody = FileToString.readFile(requestedFile);
			switch (fileName.toLowerCase()) {
				case "remainders.html":
					responseBody = HTMLCreator.createRemainderPage(getMailCookie(), responseBody);
					break;
				case "tasks.html":
					break;
				case "polls.html":
					break;
			}
			return makeHttpResponse(response, responseBody, contentType, null);

		} catch (FileNotFoundException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		} catch (NullPointerException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		}
	}
	
	
	public HttpResponse handlePOSTRequest(String root, String defaultPage) throws IOException {

		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION);
		
		String response = HttpResponse.RESPONSE_200_OK;
		String userName = null;
		if (location.equals("/main.html") &&  httpRequest.httpRequestParams.containsKey("login")) { // location is "/" no cookie
			location = root + location.substring(1);
			response = HttpResponse.RESPONSE_200_OK;
			userName = httpRequest.httpRequestParams.get("login");
		}

		try {
			File requestedFile = new File(location);
			String contentType = FileTypeToContentType.convert(requestedFile.getName());
			String responseBody = FileToString.readFile(requestedFile);
			return makeHttpResponse(response, responseBody, contentType, userName);

		} catch (FileNotFoundException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		} catch (NullPointerException e) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		}
	}

	public HttpResponse handleFORMRequest() {
		Set<String> allKeys = httpRequest.httpRequestParams.keySet();
		if (allKeys.size() == 0) {
			return makeErrorPageResponse(HttpResponse.RESPONSE_400_BAD_REQUEST,
					"Your browser sent a request that this server did not understand");
		}

		StringBuilder myBuilder = new StringBuilder();
		myBuilder
				.append("<!DOCTYPE html><HTML><HEAD><META charset=\"UTF-8\"><TITLE>Params</TITLE><BODY><table border=\"1\">");
		for (String key : allKeys) {
			String value;
			try {
				value = URLDecoder.decode(httpRequest.httpRequestParams.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				value = httpRequest.httpRequestParams.get(key);
			}

			String decodedKey;
			try {
				decodedKey = URLDecoder.decode(key, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				decodedKey = key;
			}
			myBuilder.append("<tr><td>");
			myBuilder.append(decodedKey);
			myBuilder.append("</td><td>");
			myBuilder.append(value);
			myBuilder.append("</td></tr>");
		}
		myBuilder.append("</table></BODY></HTML>");

		return makeHttpResponse(HttpResponse.RESPONSE_200_OK, myBuilder.toString(), HttpHeaders.CONTENT_TYPE_HTML, null);
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
		if (cookie != null) {
			httpResponse.appendHeader(HttpHeaders.HEADER_SET_COOKIE, HttpHeaders.COOKIE_VALUE + cookie);
		}
		httpResponse.appendBody(responseBody);
		return httpResponse;
	}

	private String getMailCookie() {
		String result = null;
		if (httpRequest.parsedHttpRequest.containsKey(HttpHeaders.HEADER_COOKIE.toLowerCase())
				&& httpRequest.parsedHttpRequest.get(HttpHeaders.HEADER_COOKIE.toLowerCase()).contains("usermail-")) {
			String cookie = httpRequest.parsedHttpRequest.get(HttpHeaders.HEADER_COOKIE.toLowerCase());
			result = cookie.split("-")[1];
		}

		return result;
	}

}
