package test;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

	public static void main(String[] args) {
		// Sender
		final String username = "dasguptakoushik97@gmail.com";
        final String password = "Comrade_kd";

        // Receiver
        String to = "kdgupta145@gmail.com";

        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);

            // sender's address
            message.setFrom(new InternetAddress(username));

            // recipient's address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject("Testing app's mail service");

            message.setText("Dear Client,\n\nThis is a sample email sent from ShriHari Dhore.");

            Transport.send(message);

            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }


	}

}
