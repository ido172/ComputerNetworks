import java.io.*;
import java.net.*;
import java.util.*;

final class HttpRequestHandler implements Runnable {

	/** Constants **/
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_HEAD = "HEAD";
	public static final String HTTP_METHOD_TRACE = "TRACE";
	public static final String HTTP_METHOD_OPTIONS = "OPTIONS";
	public static final String PROTOCOL_HTTP11 = "HTTP/1.1";
	public static final String PROTOCOL_HTTP10 = "HTTP/1.0";
	public static final String[] ALLOWED_METHODS = { "GET", "POST", "HEAD", "TRACE", "OPTIONS" };
	private static final String FORM_PAGE = "/params_info.html";
	private static final int CONNECTION_TIMEOUT = 30 * 1000; //30 seconds timeout
	private static final int NUMBER_OF_CHUNKS = 5;

	/** members **/
	public boolean isHeadRequest = false;
	public boolean isKeepAlive = false;
	public boolean isChunked = false;
	public boolean isTerminatedByClient = false;
	public HashMap<String, String> parsedHttpRequest = null;
	public HashMap<String, String> httpRequestParams = null;
	private Socket socket;
	private String root;
	private String defaultPage;
	private ThreadPool thraedPool = null;
	private DataOutputStream outToClient = null;
	private String request;
	private HttpResponseMaker httpResponseMaker = null;

	// Constructor
	public HttpRequestHandler(Socket socket, ThreadPool threadPool, String root,
			String defaultPage) {
		this.socket = socket;
		this.thraedPool = threadPool;
		
		// If root name does not end with '\' add it 
		this.root = (root.charAt(root.length() - 1) == '\\') ? root : root + '\\';
		this.defaultPage = defaultPage;
		httpResponseMaker = new HttpResponseMaker(this);
	}

	private void initRequestFlags() {
		isHeadRequest = false;
		isKeepAlive = false;
		isChunked = false;
		isTerminatedByClient = false;
	}

	// Implement the run() method of the Runnable interface.
	public void run() {
		try {

			// Set Timeout for connection
			socket.setSoTimeout(CONNECTION_TIMEOUT);
			outToClient = new DataOutputStream(socket.getOutputStream());

			// keep getting request while in keep alive mode or until time out.
			do {
				initRequestFlags();
				
				// Process the request and get the response.
				HttpResponse httpResponse = processRequest();
				
				if (httpResponse != null) {
					sendResponseToClient(httpResponse);
				}

			} while (isKeepAlive);

		} catch (SocketException e) {
			// This exception usually happens when trying to write to an already closed socket
			// Like when in the middle of writing the image back to the client he request another page 
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			KillMe();
		}
	}

	private void sendResponseToClient(HttpResponse httpResponse)
			throws IOException {

		// Print Response headers to console
		System.out.println(httpResponse.getHeaders());

		// Write response headers to client
		outToClient.writeBytes(httpResponse.getHeaders());

		outToClient.writeBytes(HttpResponse.CRLF);

		// Send body only if not head request
		if (!isHeadRequest) {

			// Check if chunked request
			if (isChunked) {
				String responseBody = httpResponse.getBody();
				int contentLength = responseBody.length();
				int lengthOfChunk = (int) (contentLength / NUMBER_OF_CHUNKS);
				int lengthOfLastChunk = lengthOfChunk + contentLength % NUMBER_OF_CHUNKS;
				

				for (int i = 0; i < NUMBER_OF_CHUNKS; i++) {
					StringBuilder builder = new StringBuilder();
					
					// Last chunk.
					if (i == NUMBER_OF_CHUNKS - 1) {
						builder.append(Integer.toHexString(lengthOfLastChunk));
						builder.append(HttpResponse.CRLF);
						builder.append(responseBody.substring(i * lengthOfChunk));
					} else {
						builder.append(Integer.toHexString(lengthOfChunk));
						builder.append(HttpResponse.CRLF);
						builder.append(responseBody.substring(i * lengthOfChunk, (i + 1) * lengthOfChunk));
					}
					
					builder.append(HttpResponse.CRLF);
					outToClient.writeBytes(builder.toString());
					System.out.println(builder.toString());
				}

				// Write chunk of 0 length to indicate end.
				outToClient.writeBytes("0" + HttpResponse.CRLF);
			} else {
				outToClient.writeBytes(httpResponse.getBody());
			}
		}
	}

	private HttpResponse processRequest() {
		try {
			
			// Read client Input and parse it.
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			RequestParser requestParser = new RequestParser(this);
			parsedHttpRequest= requestParser.getParsedRequest(inFromClient);
			
			// Check if connection was terminated by client.
			if (isTerminatedByClient) {
				isKeepAlive = false;
				return null;
			}
			
			// Get request parameters and request as string.
			httpRequestParams = requestParser.getParametersMap();
			request = requestParser.getRequest();

			// Print request to console
			System.out.println(request);

			// Validate if parse is OK.
			if (parsedHttpRequest == null) {
				return httpResponseMaker.makeErrorPageResponse(
						HttpResponse.RESPONSE_400_BAD_REQUEST, 
						"Your browser sent a request that this server did not understand");
			}

			/*
			 * Check if head request (this check is here because even if
			 * response would be an error response, we will return only the
			 * headers)
			 */
			isHeadRequest = (parsedHttpRequest.get(RequestParser.METHOD).equals(HTTP_METHOD_HEAD));

			// Validate location
			if (!isValidLocation(parsedHttpRequest.get(RequestParser.LOCATION))) {
				return httpResponseMaker.makeErrorPageResponse(
						HttpResponse.RESPONSE_400_BAD_REQUEST,
						"Your browser sent a request that this server did not understand");
			}

			// Validate HTTP Protocol
			else if (!isValidProtocol(parsedHttpRequest.get(RequestParser.PROTOCOL))) {
				return httpResponseMaker.makeErrorPageResponse(
						HttpResponse.RESPONSE_505_BAD_HTTP_VERSION,
						"Your browser sent a request with an HTTP version this server does not support");
			}

			// Validations for HTTP 1.1
			else if (parsedHttpRequest.get(RequestParser.PROTOCOL).equals(PROTOCOL_HTTP11) && !isValidHTTP11()) {
					return httpResponseMaker.makeErrorPageResponse(
							HttpResponse.RESPONSE_400_BAD_REQUEST,
							"Your browser sent a request that this server did not understand");
			} 
			
			else 
			{
				isKeepAlive = checkKeepalive();
				isChunked = checkChunked();
				return processHttpResponse();
			}

		} catch (SocketTimeoutException e) {
			return httpResponseMaker.makeErrorPageResponse(
					HttpResponse.RESPONSE_408_REQUEST_TIMEOUT,
					"The server timed out while waiting for the browser's request");
		} catch (Exception e) {
			e.printStackTrace();
			return httpResponseMaker.makeErrorPageResponse(
					HttpResponse.RESPONSE_500_INTERNAL_ERROR, "Internal Error");
			
		}
	}
	
	private HttpResponse processHttpResponse() throws IOException {
		String methodName = parsedHttpRequest.get(RequestParser.METHOD);
		HttpResponse result = null;
		
		if (methodName.equals(HTTP_METHOD_GET) || isHeadRequest) {
			result = httpResponseMaker.handleGETRequest(root, defaultPage);
		}
		
		else if (methodName.equals(HTTP_METHOD_TRACE)) {
			result = httpResponseMaker.makeHttpResponse(HttpResponse.RESPONSE_200_OK, request, HttpHeaders.CONTENT_TYPE_MESSAGE);
		} 
		
		else if (methodName.equals(HTTP_METHOD_POST)) {
			result = httpResponseMaker.handleGETRequest(root, defaultPage);
		} 
		
		else if (methodName.equals(HTTP_METHOD_OPTIONS)) {
			result = httpResponseMaker.makeOptionsResponse();
		} 
		
		// If non of the above condition passed than method is not implemented 
		else 
		{
			result = httpResponseMaker.makeErrorPageResponse(
				HttpResponse.RESPONSE_501_NOT_IMPLEMENTED, "Your browser sent a request that this server does not support");
		}
		
		return result;
	}
	


	
	private boolean checkKeepalive() {
		return (parsedHttpRequest.get(RequestParser.PROTOCOL).equals(PROTOCOL_HTTP11) && 
				parsedHttpRequest.containsKey(HttpHeaders.HEADER_CONNECTION.toLowerCase()) && 
				parsedHttpRequest.get(HttpHeaders.HEADER_CONNECTION.toLowerCase()).equals(HttpHeaders.CONNECTION_KEEP_ALIVE.toLowerCase()));
	}
	
	private boolean checkChunked() {
		return (parsedHttpRequest.containsKey(HttpHeaders.HEADER_CHUNKED.toLowerCase()) && parsedHttpRequest
				.get(HttpHeaders.HEADER_CHUNKED.toLowerCase())
				.equals(HttpHeaders.CHUNKED_YES.toLowerCase()));
	}

	private boolean isValidHTTP11() {
		// Check if host header exists
		return (parsedHttpRequest.containsKey(HttpHeaders.HEADER_HOST.toLowerCase()));
	}

	private boolean isValidProtocol(String protocol) {
		return (protocol.equals(PROTOCOL_HTTP11) || protocol
				.equals(PROTOCOL_HTTP10));
	}

	private boolean isValidLocation(String location) {
		// Validate location does not contain ".." spaces and start with "/" 
		return !(location.contains("..") || location.contains(" ") || 
				!(location.charAt(0) == '/'));
	}

	private void KillMe() {

		// close connection
		closeSocket();

		// Kill this tread in the thread pool
		this.thraedPool.killHttpRequest();
	}

	public void closeSocket() {
		// try to close connection
		try {
			outToClient.close();
		} catch (Exception e) {
		}
		try {
			socket.close();
		} catch (Exception e) {
			System.err.println("Error while trying to close connection");
			e.printStackTrace();
		}
	}
}
