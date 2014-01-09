import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.Socket;

import javax.xml.bind.DatatypeConverter;

public class SMTPMail {

	public static String CRLF = "\r\n";
	public static String AUTH_LOGIN = "AUTH LOGIN";
	public static String Client_Name = "ShayBozo IdoOrlov";
	public static String EHLO = "EHLO";
	public static String HELO = "HELO";
	static String From_name = "Mr.Tasker";
	static String Dot = ".";
	static String MAIL_FROM = "MAIL FROM:";
	static String RCPT_TO = "RCPT TO:";
	static String DATA = "DATA";
	static String Subject = "Subject:";
	static String FROM = "FROM:";
	static String Sender = "Sender:";
	static String Current_Server = "mail.netvision.net.il";
	static String OK_Code = "250";
	static String Code220 = "220";
	static String DataOKCode = "354";
	static String QUIT = "quit";

	public static void sendSMTPMail(String from, String address, String subject, String sender, String data) {
		Thread newMail = new Thread(new mailRun(from, address, subject, sender, data));
		newMail.start();
	}

	private static class mailRun implements Runnable {

		String from;
		String address;
		String subject;
		String sender;
		String data;

		public mailRun(String from, String address, String subject, String sender, String data) {
			this.from = from;
			this.address = address;
			this.subject = subject;
			this.sender = sender;
			this.data = data;
		}

		@Override
		public void run() {

			try {

				Socket clientSocket = null;
				String sentence = "";

				// Create client socket and check authentication.
				clientSocket = new Socket(ConfigFile.SMTPName, ConfigFile.SMTPPort);
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				sentence = inFromServer.readLine();
				System.out.println(sentence);

				// sentence = inFromServer.readLine();
				// if (!sentence.substring(0, 2).equals(Code220)) {
				// clientSocket.close();
				// return;
				// }

				// EHLO and Auto login part.
				if (ConfigFile.SMTPIsAuthLogin.toUpperCase().equals("TRUE")) {

					// EHLO part.
					outToServer.writeBytes(EHLO + " " + Client_Name + CRLF);
					System.out.println(sentence = inFromServer.readLine());
					System.out.println(sentence = inFromServer.readLine());
					System.out.println(sentence = inFromServer.readLine());
					// sentence = inFromServer.readLine();
					// if (!sentence.substring(0, 2).equals(OK_Code)) {
					// clientSocket.close();
					// return;
					// }

					// AUTH LOGIN part.
					outToServer.writeBytes(AUTH_LOGIN + CRLF);
					System.out.println(sentence = inFromServer.readLine());
					// if (!sentence.substring(0, 2).equals("334")) {
					// clientSocket.close();
					// return;
					// }

					// SMTPName part.
					outToServer.writeBytes(DatatypeConverter.printBase64Binary(ConfigFile.SMTPUsername.getBytes())
							+ CRLF);
					System.out.println(sentence = inFromServer.readLine());
					// if (!sentence.substring(0, 2).equals("334")) {
					// clientSocket.close();
					// return;
					// }

					// SMTPPassword part.
					outToServer.writeBytes(DatatypeConverter.printBase64Binary(ConfigFile.SMTPPassword.getBytes())
							+ CRLF);
					System.out.println(sentence = inFromServer.readLine());
					// if (!sentence.substring(0, 2).equals("235")) {
					// clientSocket.close();
					// return;
					// }

				} else {
					// HELO
					outToServer.writeBytes(HELO + " " + Client_Name + CRLF);
					// sentence = inFromServer.readLine();
					// if (!sentence.substring(0, 2).equals(OK_Code)) {
					// clientSocket.close();
					// return;
					// }
				}

				// MAIL FROM part.
				outToServer.writeBytes(MAIL_FROM + " " + from + CRLF);
				System.out.println(MAIL_FROM + " " + from + CRLF);
				System.out.println(sentence = inFromServer.readLine());
				// sentence = inFromServer.readLine();
				// if (!sentence.substring(0, 2).equals(OK_Code)) {
				// return;
				// }

				// RCTP TO part.
				outToServer.writeBytes(RCPT_TO + " " + address + CRLF);
				System.out.println(RCPT_TO + " " + address + CRLF);
				System.out.println(sentence = inFromServer.readLine());
				// sentence = inFromServer.readLine();
				// if (!sentence.substring(0, 2).equals(OK_Code)) {
				// return;
				// }

				// DATA part.
				outToServer.writeBytes(DATA + CRLF);
				System.out.println(DATA + CRLF);
				System.out.println(sentence = inFromServer.readLine());
				// sentence = inFromServer.readLine();
				// if (!sentence.substring(0, 2).equals(DataOKCode)) {
				// return;
				// }

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
				MailContext.append(sender);
				MailContext.append(CRLF);
				MailContext.append(data);
				MailContext.append(CRLF);
				MailContext.append(Dot);
				MailContext.append(CRLF);

				outToServer.writeBytes(MailContext.toString());
				System.out.println(MailContext.toString());
				System.out.println(sentence = inFromServer.readLine());
				// if (!sentence.substring(0, 2).equals(OK_Code)) {
				// return;
				// }

				// Quit part.
				outToServer.writeBytes(QUIT + CRLF);
				System.out.println(QUIT + CRLF);
				System.out.println(sentence = inFromServer.readLine());
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
}
