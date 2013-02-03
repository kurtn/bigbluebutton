package org.bigbluebutton.webconference;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class BigBlueButton {

	private BlockingQueue<Message> messages = new LinkedBlockingQueue<Message>();	
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private volatile boolean processMessages = false;
	
	public void start() {
		processMessages = true;
		
		executor.execute(new Runnable() {
			public void run() {
				Message msg;
				
				while (processMessages) {
					try {
						msg = messages.take();
						processMessage(msg);
					} catch (InterruptedException e) {
						
						stop();
					}
				}
			}
		});
	}
	
	private void processMessage(Message message) {
		
	}
	
	public void send(Message message) {
		messages.offer(message);
	}
	
	public void stop() {
		processMessages = false;
		executor.shutdown();
	}
}
