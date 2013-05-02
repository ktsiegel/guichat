package client;

public class ClientListeningThread extends Thread{
	private ChatClientModel model;
	
	public ClientListeningThread(ChatClientModel model) {
		this.model = model;
	}
	
	@Override
	public void run() {
		model.listenForResponse();
		try {
	        this.join();
        } catch (InterruptedException e) {
	        System.out.println("Error with joining ClientListeningThread.");
        }
	}
}
