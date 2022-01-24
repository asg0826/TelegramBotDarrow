package org.darrow;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.Timestamp;


public class Darrow extends TelegramLongPollingBot {
	
	/**
	 * Instances
	 * Global Variables
	 */

	long refreshTime;
	long refreshTimeMemory = 60000;
	
	TimeUpdate timeUpdate;
	TimeCalibrate timeCalibrate;

	/**
     * Constructor 
     * Set default time to 1 min 
     */
	public Darrow() {	
		setTime(60000);

	}
	
	
	/*
	 * Start the main task. It sends the data each set time. 
	 */
	
	public void startMainTask() {
		TimeUpdate timeUpdate= new TimeUpdate((long) 0 ,setTime());
		this.timeUpdate=timeUpdate;
		
	}
	
	
	/*
	 *Starts the calibration task. Sending information each 3 sec.  
	 */
	
	public void startCalibrateTask() {
		TimeCalibrate timeCalibrate= new TimeCalibrate((long) 0 ,setTime());
		this.timeCalibrate=timeCalibrate;
		
	}
	
    

	/*
	 * Setters and getters
	 */

	public long setTime() {	
		return this.refreshTime;
	}
	

	
	public void setTime(long refreshTime) {	
		this.refreshTime=refreshTime;
	}

	//Methods	
	public void update() throws IOException {
		
		try {			
			Socket s=new Socket("localhost",23456);
		    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    s.setSoTimeout(2000);
		        
		        try {
		        	String data=in.readLine(); 
		        	//String data="8934.63,1.8850,13.5352";
		        	int length_data= data.length();
		        	String[] value = data.split(",");

		        	if(length_data == 22) {
		        		//float pH_correction = (float) 0.58 + Float.valueOf(value[1]);
		        		float pH_correction = Float.valueOf(value[1]);		
		        		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			    		String time= timestamp.toString() ;	
			    		String trama=time+ ",hotel," + String.valueOf(value[0])+","+ String.valueOf(pH_correction) +","+ String.valueOf(value[2]) ;
			    		int length_trama= trama.length();
			    		if(length_trama <= 52) {
			    			sendMsg(trama);	
					        s.close();
			    			
			    		}else {			    			
			    			s.close();
			    			update();
			    			
			    		}
			    		
		        	}else {
		         		s.close();
		    			update();
		        		
		        	}
		        		
		        	
		    		
				} catch (SocketTimeoutException a) {
					
					s.close();
					Runtime.getRuntime().exec("sudo sh /home/pi/task.sh");
					
				}
			
		} catch (ConnectException e) {
			Runtime.getRuntime().exec("sudo sh /home/pi/task.sh");
		}
        
		}
	
	
	
	public void restart() {
		
    	this.timeUpdate.Stop();
    	startMainTask();
		
	}
	
	public void updateCalibrate() throws IOException {
		
		try {
			Socket s=new Socket("localhost",23456);
		    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    s.setSoTimeout(2000);  
		        try {	
		         	String data=in.readLine(); //Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
		        	//String data="8934.63,1.8850,13.5352";
		        	int length_data= data.length();
		        	
		        	if(length_data == 22) {
			        	sendMsg(data);	
				    	s.close();
				    	
		        	}else {
		    			s.close();
		    			updateCalibrate();
		        		
		        	}

		    		
				} catch (SocketTimeoutException a) {		
					s.close();
					Runtime.getRuntime().exec("sudo sh /home/pi/task.sh");
					
				}
			
		} catch (ConnectException e) {
			Runtime.getRuntime().exec("sudo sh /home/pi/task.sh");
		}
        
		}	
	

	
	
	
	public void sendMsg(String msg) throws IOException {	
		    long channel=-1001574111334L ;
		    //long channel=-1001758498434L ;
       		SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
     	    message.setChatId(Long.toString(channel));
     	   	message.setText(msg);
    
        try {
        	execute(message); 
		
        } catch (TelegramApiException e) {
        	e.printStackTrace();
		
        }
    
	}

	
	
	@Override
	public void onUpdateReceived(Update update) {

		if (update.hasChannelPost()) {
			
	        String post= update.getChannelPost().getText();
	        String[] parts = post.split(" ");

	        if("/helloDarrow".equals(post)) {
	        	try {
	        		
					sendMsg("Hey there, I am Darrow 😄 .\n\n"
							+ "Here are my commands:\n"
							+ " /helloDarrow \n"
							+ " /locationDarrow \n"
							+ " /informationDarrow \n"
							+ " /stopDarrow \n"
							+ " /restartDarrow \n"
							+ " /restartSystem \n"
							+ " /setTime (min) \n"
							+ " /startCalibration \n"
							+ " /stopCalibration \n"
							+ " /update");
					
				} catch (IOException e) {
					e.printStackTrace();
				}
	        		
	        }
	        
	        
	        if("/update".equals(post)) {
	        	try {	
	        		update();
					
			} catch (IOException e) {

				e.printStackTrace();
			}
	        		
	        }
	        
			
	        
	        if("/locationDarrow".equals(post)) {
	        	try {
	        		
				sendMsg("I am located at Hotel Termales del Ruiz 🤩 .");
					
			} catch (IOException e) {

				e.printStackTrace();
			}
	        		
	        }
	        
	        
	        if("/informationDarrow".equals(post)) {
	        	try {
	        		
				sendMsg("I am measuring conductivity, pH and enjoying the environment 🙈 .");
					
			} catch (IOException e) {

				e.printStackTrace();
			}
	        		
	        }
	        
			
	        
	        if("/stopDarrow".equals(post)) {

	        	timeUpdate.Stop();
	        		
	        }
	        
	        if("/restartSystem".equals(post)) {

	        	try {
					Runtime.getRuntime().exec("sudo reboot");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        		
	        }
	        
	        if("/restartDarrow".equals(post)) {

	        	timeUpdate.Stop();
	        	timeUpdate.Stop();
	        	
	        	try {
	        		
	        		Long sT= setTime()/60000;
	        		sendMsg("Uptade rate set to " + sT + " min");
	        		startMainTask();
					
			} catch (IOException e) {

				e.printStackTrace();
				
			}
	        	
	     		
	        }
			
	        
	        
	        if("/setTime".equals(parts[0])) {
	        	try {
	        		
				long userTime = (Long.parseLong(parts[1]))*60000;
					
				if(userTime >= 30000) {
					
					timeUpdate.Stop();
					setTime(userTime);
					sendMsg("Uptade rate set to " + parts[1] + " min");
					startMainTask();
						
						
				}
					
			} catch (IOException e) {

				e.printStackTrace();
				
			}
	        		
	        }
	       
	        
	        if("/startCalibration".equals(post)) {
	        	
	        	
	        try {        		
	        	    refreshTimeMemory = setTime();
					timeUpdate.Stop();
					sendMsg("Calibration started");
					setTime(3000);
					startCalibrateTask();
												
				}
					
			 catch (IOException e) {

				e.printStackTrace();
				
			}
	        		
	        }
	        
	              
	        if("/stopCalibration".equals(post)) {
	        	
	        	
	        	try {	

					//timeUpdate.Stop();
	        		timeCalibrate.Stop();
					sendMsg("Calibration finished");
					setTime(refreshTimeMemory);
					sendMsg("Uptade rate set to " + refreshTimeMemory/60000 + " min");
					startMainTask();
												
				}
					
			 catch (IOException e) {

				e.printStackTrace();
				
			}
	        		
	        }
	        
	    }
	}
	

	@Override
	public String getBotUsername() {
 
        	return "Darrow";
		
    	}

    @Override
	
    public String getBotToken() {
           //return "2146758113:AAGighaEcX27xjiDZ0ngfw2-_b_FPJlaCRE";
       	   return "2110806264:AAGpaoaocSAetvSle6m5STQcKaD1CmSHXI8";
    }
}