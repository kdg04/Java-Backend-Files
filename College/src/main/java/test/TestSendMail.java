package test;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestSendMail {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");   
		props.put("mail.smtp.auth", "true");          
		props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.user", "dasguptakoushik97@gmail.com");
        props.put("mail.smtp.password", "Comrade_kd");


        // Session for sending email
        Session session = Session.getInstance(props, null);

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("dasguptakoushik97@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("dasguptakoushik97@gmail.com"));
            message.setSubject("Test Subject");
            message.setText("This is a test message.");

            // Send the message
            Transport.send(message);
            System.out.println("Message sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
	   } 

	}


