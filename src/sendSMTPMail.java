import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.Socket;

import javax.xml.bind.DatatypeConverter;

public class sendSMTPMail {
	static String CRLF = "\r\n";
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
	static String OK_Code = "250";
	static String DataOKCode = "235";
	static String QUIT = "quit";

	private String from;
	private String subject;
	private String data;
	private String address;
	private String senderName;
	private BufferedReader inFromServer;

	public sendSMTPMail(String from, String address, String subject, String sender, String data) {
		this.from = from;
		this.subject = subject;
		this.data = data;
		this.address = address;
		this.senderName = sender;

		this.sendMail();
	}

	private void sendMail() {
		// Establish the listen socket.
		Socket clientSocket = null;
		String sentence = "";

		try {

			// Create client socket and check authentication.
			clientSocket = new Socket(ConfigFile.SMTPName, ConfigFile.SMTPPort);
			sentence = inFromServer.readLine();
			if (!sentence.substring(0, 2).equals(OK_Code)) {
				clientSocket.close();
				return;
			}

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// EHLO and Auto login part.
			if (ConfigFile.SMTPIsAuthLogin == "TRUE") {

				// EHLO part.
				outToServer.writeBytes(EHLO + " " + Client_Name + CRLF);
				sentence = inFromServer.readLine(); // / not good there is allot
													// of lines.
				if (!sentence.substring(0, 2).equals(OK_Code)) {
					clientSocket.close();
					return;
				}

				// AUTH LOGIN part.
				outToServer.writeBytes(AUTH_LOGIN);
				if (!sentence.substring(0, 2).equals("334")) {
					clientSocket.close();
					return;
				}

				// SMTPName part.
				outToServer.writeBytes(DatatypeConverter.printBase64Binary(ConfigFile.SMTPUsername.getBytes()) + CRLF);
				if (!sentence.substring(0, 2).equals("334")) {
					clientSocket.close();
					return;
				}

				// SMTPPassword part.
				outToServer.writeBytes(DatatypeConverter.printBase64Binary(ConfigFile.SMTPPassword.getBytes()) + CRLF);
				if (!sentence.substring(0, 2).equals("334")) {
					clientSocket.close();
					return;
				}

			} else {
				// HELO
				outToServer.writeBytes(HELO + " " + Client_Name + CRLF);
				sentence = inFromServer.readLine();
				if (!sentence.substring(0, 2).equals(OK_Code)) {
					return;
				}
			}

			// MAIL FROM part.
			outToServer.writeBytes(MAIL_FROM + " " + from + CRLF);
			sentence = inFromServer.readLine();
			if (!sentence.substring(0, 2).equals(OK_Code)) {
				return;
			}

			// RCTP TO part.
			outToServer.writeBytes(RCTP_TO + " " + address + CRLF);
			sentence = inFromServer.readLine();
			if (!sentence.substring(0, 2).equals(OK_Code)) {
				return;
			}

			// DATA part.
			outToServer.writeBytes(DATA + CRLF);
			sentence = inFromServer.readLine();
			if (!sentence.substring(0, 2).equals(DataOKCode)) {
				return;
			}

			// Mail context part.
			StringBuilder MailContext = new StringBuilder();
			MailContext.append(Subject);
			MailContext.append(" ");
			MailContext.append(subject);
			MailContext.append(CRLF);
			MailContext.append(FROM);
			MailContext.append(" ");
			MailContext.append(From_name);
			MailContext.append(CRLF);
			MailContext.append(Sender);
			MailContext.append(" ");
			MailContext.append(senderName);
			MailContext.append(CRLF);
			MailContext.append(data);
			MailContext.append(CRLF);
			MailContext.append(Dot);
			MailContext.append(CRLF);

			outToServer.writeBytes(MailContext.toString());
			sentence = inFromServer.readLine();
			if (!sentence.substring(0, 2).equals(OK_Code)) {
				return;
			}

			// Quit part.
			outToServer.writeBytes(QUIT + CRLF);
			// sentence = inFromServer.readLine();
			// if (!sentence.substring(0, 2).equals("221")) {
			// return;
			// }

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
