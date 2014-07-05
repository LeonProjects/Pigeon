package com.leonproject.pigeon.db;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

import com.leonproject.pigeon.email.EmailServer;
import com.leonproject.pigeon.util.PropertyReader;

public class Connector {
	
	Connection dbCon = null;
    Statement stmt = null;
    ResultSet rs = null;
    Session ssn;
    String from,to,body,subject,dbURL,username,password;
    public static final int LIMIT = 100;        // No of rows extracted from DB in each run
    public static final int ARRAY_BUFFER = 10;  // Size of the Buffer available for each thread
    public  int threadCounter = 0 ;
    public final String selectQuery = "select * from emailQueue where timestamp IS NULL LIMIT "+ LIMIT ;
    
   
    public Connector() throws FileNotFoundException{
    	PropertyReader prop = new PropertyReader();
    	dbURL = prop.getConfigValue("dbURL");
        username = prop.getConfigValue("dbUser");
        password = prop.getConfigValue("dbPass");
        
        EmailServer server = new EmailServer();
        ssn = server.getServerSession();
       
    }
	
	public  int extractRows() throws FileNotFoundException, InterruptedException{
				
		List<String[]> emailList = new ArrayList<String[]>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, 
		        new ArrayBlockingQueue<Runnable>(10), new ThreadPoolExecutor.CallerRunsPolicy());

		int counter = 0;
		
       
        try {
        	
            //getting database connection to MySQL server
        	
            dbCon = DriverManager.getConnection(dbURL, username, password);
           
            //switch off autocommit for the select and update to happen as one atomic operation
            dbCon.setAutoCommit(false);
            
            stmt = dbCon.createStatement(
                    ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
            
            //Resultset returned by query
            rs = stmt.executeQuery(selectQuery);
            
            while(rs.next()){
             counter++;
             String[] user = new String[5];
             user[0] = rs.getString(1);
             user[1] = rs.getString(2);
             user[2] = rs.getString(3);
             user[3] = rs.getString(4);
             user[4] = rs.getString(5);
             emailList.add(user);
                     
             String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
             rs.updateString(6, timeStamp);
             rs.updateRow();
             
             if (emailList.size() == ARRAY_BUFFER) {
            	 final List<String[]> elist = new ArrayList<String[]>(emailList);
                 executor.execute(new Runnable() {
                     public void run() {
                         sendMail(ssn,elist);
                         
                     }
                 });
                 emailList.clear();
                 
             }
          }
            dbCon.commit();
            dbCon.close();
             
        } catch (SQLException ex) {
            Logger.getLogger(Collection.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
        	
           //close connection ,stmt and resultset here
        }
        if (! emailList.isEmpty()) {
            final List<String[]> elist = new ArrayList<String[]>(emailList);
            executor.execute(new Runnable() {
                public void run() {
                    sendMail(ssn,elist);
                }
            });
            emailList.clear();
        }
        while (! executor.isTerminated()) {
	            executor.shutdown();
	            executor.awaitTermination(10, TimeUnit.DAYS);
	        }
        
        return counter;
		
    }
	
	public void sendMail(Session session, List<String[]> elist){
		
		for (int i = 0; i < elist.size(); i++) {
		    String[] mail = elist.get(i);
		
		try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(mail[1]));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(mail[2]));

	         // Set Subject: header field
	         message.setSubject(mail[3]);

	         // Now set the actual message
	         message.setText(mail[4]);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message "+ mail[0] + " successfully..");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }	
	   }
   }
}
