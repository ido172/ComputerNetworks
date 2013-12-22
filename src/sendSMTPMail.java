import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.Socket;
//import org.apache.commons.codec.binary.Base64;

public class sendSMTPMail {

	static String AUTH_LOGIN = "AUTH LOGIN";
	static String Client_Name = "ShayBozo IdoOrlov";
	static String EHLO = "EHLO";
	static String HELO = "HELO";
	static String From_name = "Mr. Tasker";
	static String Dot = ".";
	static String MAIL_FROM = "MAIL FROM";
	static String RCTP_TO = "RCTP TO:";
	static String DATA = "DATA";
	static String Subject = "Subject:";
	static String FROM = "FROM:";
	static String Sender = "Sender:";
	static String Current_Server = "mail.netvision.net.il";

	private String from;
	private String subject;
	private String data;
	private String address;
	private String sender;

	public sendSMTPMail(String from, String address, String subject, String sender, String data) {
		this.from = from;
		this.subject = subject;
		this.data = data;
		this.address = address;
		this.sender = sender;

		this.sendMail();
	}

	private void sendMail() {
		// Establish the listen socket.
		Socket clientSocket = null;
		String sentence = "";

		try {

			// Create client socket.
			clientSocket = new Socket(Current_Server, ConfigFile.SMTPPort);
			// System.out.println("The sever has started listening on port " +
			// ConfigFile.SMTPPort);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// Start the connection???

			// HELO/EHLO and Auto login part.
			if (ConfigFile.SMTPIsAuthLogin == "TRUE") {

				// EHLO part.
				outToServer.writeBytes(EHLO + " " + Client_Name);
				sentence = inFromServer.readLine();
				// Check the input.

				// AUTH LOGIN part.
				outToServer.writeBytes(AUTH_LOGIN);
				// Check the input.

				// SMTPName part.
				outToServer.writeBytes(ConfigFile.SMTPName); // IN BASE64
				// Check the input.

				// SMTPPassword part.
				outToServer.writeBytes(ConfigFile.SMTPPassword); // IN BASE64
				// Check the input.

			} else {
				outToServer.writeBytes(HELO + " " + Client_Name);
				sentence = inFromServer.readLine();
				if (!sentence.substring(0, 2).equals("250")) {
					return;
				}
			}
			//
			// //encoding byte array into base 64
			// byte[] encoded = Base64.encodeBase64(orig.getBytes());
			//
			// System.out.println("Original String: " + orig );
			// System.out.println("Base64 Encoded String : " + new
			// String(encoded));
			//
			// //decoding byte array into base64
			// byte[] decoded = Base64.decodeBase64(encoded);
			// System.out.println("Base 64 Decoded  String : " + new
			// String(decoded));
			//
			//

			// MAIL FROM part.
			outToServer.writeBytes(MAIL_FROM + " " + from);
			sentence = inFromServer.readLine();
			if (!sentence.substring(0, 8).equals("250 2.5.0")) {
				return;
			}

			// RCTP TO part.
			outToServer.writeBytes(RCTP_TO + " " + address);
			sentence = inFromServer.readLine();

			if (!sentence.substring(0, 8).equals("250 2.1.5")) {
				return;
			}

			// DATA part.
			outToServer.writeBytes(DATA);
			sentence = inFromServer.readLine();
			// Check the input.

			// Mail context part.
			String MailContext = Subject + " " + subject + "\n" + FROM + " " + From_name + "\n" + Sender + " " + sender
					+ "\n" + data + "\n" + Dot;
			outToServer.writeBytes(MailContext);
			sentence = inFromServer.readLine();
			// Check the input. ???

			clientSocket.close();

		} catch (BindException e) {
			System.err.println("Seems Like port " + ConfigFile.SMTPPort + " is already in use by another program");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error!");
			e.printStackTrace();
		}
	}

}
