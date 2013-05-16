package client;

import java.io.IOException;

/**
 * A ClientListeningThread listens for communication sent by the server to the client
 * on a separate thread than the main thread of the ChatClientModel.
 */

public class ClientListeningThread extends Thread {
	private ChatClientModel model;
	
	public ClientListeningThread(ChatClientModel model) {
		this.model = model;
	}
	
	@Override
	public void run() {
		try {
	        model.listenForResponse(); //The method in the ChatClientModel that registers messages from the server.
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
