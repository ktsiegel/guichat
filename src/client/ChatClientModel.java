package client;

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
		String output = server.submitCommand("start " + other.getUsername());
		if (output == null || !output.matches("join \\d+ [A-Za-z0-9]+")) {
			throw new IllegalArgumentException("Error: join command of illegal form");
		}
		StringTokenizer outTokenizer = new StringTokenizer(output);
		outTokenizer.nextToken();
		final int ID = Integer.parseInt(outTokenizer.nextToken());
		String username = outTokenizer.nextToken();
		
		ChatBox box = new ChatBox(this,ID);
		box.setVisible(true);
		chats.put(ID, box.getModel());
	}
}

