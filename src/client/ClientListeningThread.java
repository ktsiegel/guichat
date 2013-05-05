package client;

import java.io.IOException;

public class ClientListeningThread extends Thread {
	private ChatClientModel model;
	
	public ClientListeningThread(ChatClientModel model) {
		this.model = model;
	}
	
	@Override
	public void run() {
		try {
	        model.listenForResponse();
        } catch (IOException e1) {
	        System.out.println("Error listening for server response.");
        }
		try {
	        this.join();
        } catch (InterruptedException e) {
	        System.out.println("Error with joining ClientListeningThread.");
        }
	}
}
