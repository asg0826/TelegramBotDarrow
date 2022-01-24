package org.telegram.darrow;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUpdate {
	
	//Instances
	Timer timer;
	//Constructor
	public TimeUpdate(Long start, Long seconds) {
		
		Timer timer = new Timer(); 
		this.timer=timer;
		timer.schedule(new timerTask(), 0, seconds);
		
    
	}
	
	public void Stop() {
		
		this.timer.cancel();
	}
	
	
    class timerTask extends TimerTask {
    		
    	
        @Override
		public void run() {
        	      	
        	Darrow darrow = new Darrow();
        	
			try {
				
				darrow.update();
				
			} catch (IOException e) {
			
				darrow.restart();
				e.printStackTrace();
			}
        }
    }
	
	//Methods
	
	

}