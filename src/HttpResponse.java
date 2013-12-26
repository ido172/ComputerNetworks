import java.util.Date;


public class HttpResponse {
	
	public static final String CRLF = "\r\n";
	public static final String RESPONSE_200_OK = "200 OK";
	public static final String RESPONSE_302_REDIRECT = "302 Found";
	public static final String RESPONSE_400_BAD_REQUEST = "400 Bad Request";
	public static final String RESPONSE_404_NOT_FOUND = "404 Not Found";
	public static final String RESPONSE_408_REQUEST_TIMEOUT = "408 Request Time out";
	public static final String RESPONSE_500_INTERNAL_ERROR = "500 Internal Server Error";
	public static final String RESPONSE_501_NOT_IMPLEMENTED = "501 Not Implemented";
	public static final String RESPONSE_505_BAD_HTTP_VERSION = "505 HTTP Version Not Supported";
	

	
	public static final String PLCAEHOLDER_TITLE= "[title]";
	public static final String PLCAEHOLDER_BODY= "[body]";
	
	public static final String ERROR_PAGE_TEMPLATE = "<!DOCTYPE html><HTML><HEAD><TITLE>[title]</TITLE>" +
            										 "</HEAD><BODY><H1>"+PLCAEHOLDER_TITLE+"</H1>" +
            										 "<p>"+PLCAEHOLDER_BODY+"</P>" +
            										 "</BODY></HTML>";
	

	
	private StringBuilder headResponse = null;
	private StringBuilder bodyResponse = null;
	
	public HttpResponse(String responseType, String protocol) {
		headResponse = new StringBuilder();
		headResponse.append(protocol);
		headResponse.append(" ");
		headResponse.append(responseType);
		headResponse.append(CRLF);
		appendHeader(HttpHeaders.HEADER_SERVER, WebServer.SERVER_NAME);
		appendHeader(HttpHeaders.HEADER_DATE, getDate());
	}
	
	public void appendHeader(String headerName, String headerValue) {
		headResponse.append(headerName);
		headResponse.append(": ");
		headResponse.append(headerValue);
		headResponse.append(CRLF);
	}
	
	public void appendBody(String body) {
		bodyResponse = new StringBuilder();
		bodyResponse.append(body);
	}
	
	public String getHeaders() {
		return headResponse.toString();
	}
	
	public String getBody() {
		return bodyResponse.toString();
	}
	
	private String getDate() {
		return (new Date()).toString();
	}
}