import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;


public class HttpResponseMaker {
	private HttpRequestHandler httpRequest = null;
	
	public HttpResponseMaker(HttpRequestHandler httpRequest) {
		this.httpRequest = 	httpRequest;
	}
	
	public HttpResponse handleGETRequest(String root, String defaultPage) throws IOException {
		String location = httpRequest.parsedHttpRequest.get(RequestParser.LOCATION);
		if (location.length() == 1) { // that means location is "/"
			location = root + defaultPage;
		} else {
			location = root + location.substring(1);
		}

		try {
			File requestedFile = new File(location);
			String contentType = FileTypeToContentType.convert(requestedFile.getName());
			String responseBody = FileToString.readFile(requestedFile);
			return makeHttpResponse(HttpResponse.RESPONSE_200_OK, responseBody, contentType);

		} catch (FileNotFoundException e) {
			return makeErrorPageResponse(
					HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		} catch (NullPointerException e) {
			 return makeErrorPageResponse(
				HttpResponse.RESPONSE_404_NOT_FOUND, "The file you requested is not found");
		}
	}
	
	public HttpResponse handleFORMRequest() {
		Set<String> allKeys = httpRequest.httpRequestParams.keySet();
		if (allKeys.size() == 0) {
			return makeErrorPageResponse(
					HttpResponse.RESPONSE_400_BAD_REQUEST,
					"Your browser sent a request that this server did not understand"); 
		}
		
		StringBuilder myBuilder = new StringBuilder();
		myBuilder.append("<!DOCTYPE html><HTML><HEAD><META charset=\"UTF-8\"><TITLE>Params</TITLE><BODY><table border=\"1\">");
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

		return makeHttpResponse(HttpResponse.RESPONSE_200_OK, myBuilder.toString(), HttpHeaders.CONTENT_TYPE_HTML);
	}

	public HttpResponse makeOptionsResponse() {
		httpRequest.isHeadRequest = true;
		HttpResponse httpResponse = makeHttpResponse(HttpResponse.RESPONSE_200_OK, "", HttpHeaders.CONTENT_TYPE_MESSAGE);
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
		return makeHttpResponse(responseType, responseBody, HttpHeaders.CONTENT_TYPE_HTML);
	}
	
	
	public HttpResponse makeHttpResponse(String responseType, String responseBody, String contentType) {
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
		httpResponse.appendBody(responseBody);
		return httpResponse;
	}
}
