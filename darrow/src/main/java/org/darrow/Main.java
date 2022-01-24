package org.darrow;

import java.io.IOException;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
	
	public static void main(String[] args) throws IOException {
		
		/**
		 * Register a new bot. 
		 */

        try {
        
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);    
            Darrow darrow = new Darrow();
            botsApi.registerBot(darrow);
            darrow.startMainTask();
                      
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }  
        
        
    }
        
}