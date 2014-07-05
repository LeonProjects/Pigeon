package com.leonproject.pigeon.email;

import java.io.FileNotFoundException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import com.leonproject.pigeon.util.PropertyReader;

public class EmailServer {
	
	Properties properties;
	Session session;
	
	public EmailServer() throws FileNotFoundException{

		 Properties properties = System.getProperties();
		 PropertyReader prop = new PropertyReader();
		 
	      // Setup mail server
	      properties.put("mail.smtp.starttls.enable", prop.getConfigValue("smtpTtls")); 
	      properties.put("mail.smtp.host", prop.getConfigValue("smtpHost"));
	      properties.put("mail.smtp.port", prop.getConfigValue("smtpPort"));
	      properties.put("mail.smtp.auth", prop.getConfigValue("smtpAuth"));
	      
	      Authenticator auth = new Authenticator() {
	            //override the getPasswordAuthentication method
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication("shardool.desai@gmail.com", "Godonlyknows");
	            }
	        };
	      
	      // Get the default Session object.
	      session = Session.getDefaultInstance(properties,auth);		
	}
	public Session getServerSession(){
		
		return session;
	}
}
