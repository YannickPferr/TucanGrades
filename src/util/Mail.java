package util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Mail {

	private String from;
	private String to;

	private Session session;

	public Mail(String from, String to) {
		this.from = from;
		this.to = to;
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", "192.168.178.54:504");

		// Get the default Session object.
		session = Session.getDefaultInstance(properties);
	}

	public void sendEmail(String msg) {
		 try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("Noten in Tucan!");

	         // Now set the actual message
	         message.setText(msg);

	         // Send message
	         Transport.send(message);
	         System.out.println("Email erfolgreich gesendet...");
	      } catch (MessagingException e) {
	    	  System.err.println(e);
	      }
	}
}