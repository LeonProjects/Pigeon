package com.leonproject.pigeon.email;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.leonproject.pigeon.db.Connector;

public class EmailClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			int totalCounter=0;
			Connector conn = new Connector();
			SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			String startTimeStamp = d.format(Calendar.getInstance().getTime());
			System.out.println("Process Started at "+ startTimeStamp);
			
			int counter = 1;    /*Intializing this with a non zero for the first run*/
				while (counter > 0){
					counter = conn.extractRows();
					totalCounter += counter;
		    }
				 
				String endTimeStamp =  d.format(Calendar.getInstance().getTime());
				System.out.println("Process Ended at "+ endTimeStamp);
				System.out.println("Total Mail sent :"+ totalCounter);
		}
		catch (FileNotFoundException e1){
			System.out.println("File Not Found"+ e1 );
		}
		catch (InterruptedException e2){
			
		}
	}
}
