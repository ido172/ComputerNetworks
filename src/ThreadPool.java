import java.util.LinkedList;

public class ThreadPool {
    
	// Set Max number for waiting requests, this is to make sure memory will not be full, 
	// if there are 10000 requests waiting the next one will be automatically closed.
	private static final int MAX_WAITING_REQUESTS = 10000;
    private LinkedList<HttpRequestHandler> waitingThreads;
    private final int maxThreads;
    private int numberOfThreadsRunning;
    
    public ThreadPool(int maxThreads) {
        this.maxThreads = maxThreads;
        numberOfThreadsRunning = 0;
        waitingThreads = new LinkedList<HttpRequestHandler>();
    }
    
    public synchronized void handleNewHttpRequest(HttpRequestHandler newHttpRequest){
    	// If number of running thread is smaller than newHttpRequest, start thread 
        if (numberOfThreadsRunning < maxThreads){
            Thread thread = new Thread(newHttpRequest);
            thread.start();
            numberOfThreadsRunning++;
            
        // Add request into the waiting list if list size is smaller than  MAX_WAITING_REQUESTS
        } else if (waitingThreads.size() < MAX_WAITING_REQUESTS){
            waitingThreads.addLast(newHttpRequest);
        } else {
        	newHttpRequest.closeSocket();
        }
    }
    
    public synchronized void killHttpRequest(){
        // Set counter to one less thread is running
        numberOfThreadsRunning--;
        
        // Pop request for waiting queue and start it
        if(waitingThreads.size() > 0){
            HttpRequestHandler runMe = waitingThreads.pop();
            Thread thread = new Thread(runMe);
            thread.start();
            numberOfThreadsRunning++;
        }
    }
 }