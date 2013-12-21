import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class sendSMTPMail {

	static String AUTH_LOGIN = "AUTH LOGIN";
	static String Client_Name = "ShayBozo IdoOrlov";
	static String EHLO = "EHLO";
	static String HELO = "HELO";
	static String Sender_namee = "Mr. Tasker";
	static String dot = ".";
	static String MAIL_FROM = "MAIL FROM";
	static String RCTP_TO = "RCTP TO:";
	static String DATA = "DATA";
	private String from;
	private String subject;
	private String data;
	private String address;
	
	public sendSMTPMail(String From, String address, String subject, String data) {
		this.from = From;
		this.subject = subject;
		this.data = data;
		this.address = address;
		
		this.sendMail();
	}

	private void sendMail() {
		// Establish the listen socket.
		ServerSocket serverSocket = null;
		Socket socket = null;
		String sentence = "";

		try {

			// Create client socket.
			Socket clientSocket = new Socket("localhost", ConfigFile.SMTPPort);
			// System.out.println("The sever has started listening on port " +
			// ConfigFile.SMTPPort);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// HELO/EHLO and Auto login part.
			if (ConfigFile.SMTPIsAuthLogin == "TRUE") {
				outToServer.writeBytes(EHLO + " " + Client_Name);
				sentence = inFromServer.readLine();
				// Check the input.
				outToServer.writeBytes(AUTH_LOGIN);
				// Check the input.
				outToServer.writeBytes(ConfigFile.SMTPName);
				// Check the input.				
				outToServer.writeBytes(ConfigFile.SMTPName); // IN BASE64
				// Check the input.
				outToServer.writeBytes(ConfigFile.SMTPPassword); // IN BASE64
				// Check the input.
			} else {
				outToServer.writeBytes(HELO + " " + Client_Name);
				sentence = inFromServer.readLine();
				// Check the input.
			}

			// MAIL FROM part.
			outToServer.writeBytes(EHLO + " " + Client_Name);
			sentence = inFromServer.readLine();
			// Check the input.

			// MAIL FROM part.
			outToServer.writeBytes(RCTP_TO + " " + address);
			sentence = inFromServer.readLine();
			// Check the input.


			clientSocket.close();

		} catch (BindException e) {
			System.err.println("Seems Like port " + ConfigFile.SMTPPort + " is already in use by another program");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Unable to establish server connection");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error!");
			e.printStackTrace();
		}
	}

}
