package edu.uncc.ssdi.meetingscheduler.server.email;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {
	
	private String senderEmail = "mssuncc@gmail.com";
	private String senderMailPassword = "meetingscheduler";
    private String gmail = "smtp.gmail.com";
    private Session session;
    private MyAuthenticator authentication;
	
	public void initialize(){
		         
        Properties props = System.getProperties();
 
        props.put("mail.smtp.user", senderEmail);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
              "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
 
        // Required to avoid security exception.
        authentication = new MyAuthenticator(senderEmail,senderMailPassword);
        session = Session.getDefaultInstance(props,authentication);
        session.setDebug(true);
 
	}
	
	public void send(ArrayList<String> recipeintEmail, String subject, String messageText) throws MessagingException, AddressException {
	
		for(String address : recipeintEmail)
			send(address, subject, messageText);
		
	}
	
	public void send(String recipeintEmail, String subject, String messageText) throws MessagingException, AddressException {
        MimeMessage message = new MimeMessage(session);
         
        BodyPart messageBodyPart = new MimeBodyPart();     
        messageBodyPart.setText(messageText);
         
        // Add message text
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);               
        message.setSubject(subject);
        message.setFrom(new InternetAddress(senderEmail));
        message.addRecipient(Message.RecipientType.TO,
            new InternetAddress(recipeintEmail));
 
        Transport transport = session.getTransport("smtps");
        transport.connect(gmail,465, senderEmail, senderMailPassword);
        transport.sendMessage(message, message.getAllRecipients());
         
        transport.close();
         
    }
     
    private class MyAuthenticator extends javax.mail.Authenticator {
        String User;
        String Password;
        public MyAuthenticator (String user, String password) {
            User = user;
            Password = password;
        }
         
        @Override
        public javax.mail.PasswordAuthentication getPasswordAuthentication() {
            return new javax.mail.PasswordAuthentication(User, Password);
        }
    }

}
