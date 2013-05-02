package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

import client.*;
import javax.swing.SwingUtilities;

import server.ChatServer;
import user.User;

public class ChatClientModel{
	private User user;
	private Socket socket;
	private ChatClient client;
	private ChatServer server;
	private HashMap<Integer,ChatBoxModel> chats;
	
	public ChatClientModel(ChatClient client, User user, ChatServer server) {
		this.client = client;
		this.user = user;
		this.server = server;
		chats = new HashMap<Integer,ChatBoxModel>();
	}
	
	public void addChat(User other) {
		//submit start command to server, receive join command
		server.addMessageToQueue("start " + other.getUsername());
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			for (String line =in.readLine(); line!=null; line=in.readLine()) {
                handleRequest(line);
            }
		}
		catch(IOException e) {
			System.out.println("Error with reading info from server.");
		}
	}
	
	public void handleRequest(String output) {
		if (output.matches("join \\d+ [A-Za-z0-9]+")) {
			StringTokenizer outTokenizer = new StringTokenizer(output);
			outTokenizer.nextToken();
			final int ID = Integer.parseInt(outTokenizer.nextToken());
			String username = outTokenizer.nextToken();
			ChatBox box = new ChatBox(this,ID);
			box.setVisible(true);
			chats.put(ID, box.getModel());
		}
		
	}
}

