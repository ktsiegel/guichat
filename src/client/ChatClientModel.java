package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
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
	private HashMap<Integer,ChatBoxModel> chats;
	
	public ChatClientModel(ChatClient client, User user) {
		this.client = client;
		this.user = user;
		chats = new HashMap<Integer,ChatBoxModel>();
		try {
	        this.socket = this.connect();
        } catch (IOException e) {
	        System.out.println("Failure with connecting to socket.");
        }
	}
	
	public void addChat(User other) {
		//submit start command to server, receive join command
		submitCommand("start " + other.getUsername());
	}
	
	public void submitCommand(String command) {
		PrintWriter out;
        try {
	        out = new PrintWriter(socket.getOutputStream(), true);
	        out.println(command);
        } catch (IOException e) {
	        System.out.println("Error with printing output");
        }
	}
	
	public void listenForResponse() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			for (String line =in.readLine(); line!=null; line=in.readLine()) {
                handleRequest(line);
            }
			in.close();
		}
		catch(IOException e) {
			System.out.println("Error with reading info from server.");
		}
	}
	
	public void handleRequest(String output) {
		StringTokenizer outTokenizer = new StringTokenizer(output);
		if (output.matches("login [A-Za-z0-9]+")) {
			outTokenizer.nextToken();
			client.addUser(new User(outTokenizer.nextToken()));
		}
		else if (output.matches("logout [A-Za-z0-9]+")) {
			outTokenizer.nextToken();
			client.removeUser(outTokenizer.nextToken());
		}
		else if (output.matches("say \\d+ [A-Za-z0-9]+ ^_+ ^_")) {
			outTokenizer.nextToken();
			ChatBoxModel currentChatModel = chats.get(outTokenizer.nextToken());
			currentChatModel.addChatLine(outTokenizer.nextToken(),outTokenizer.nextToken(),outTokenizer.nextToken());
		}
		else if (output.matches("join \\d+ [A-Za-z0-9]+")) {
			outTokenizer.nextToken();
			final int ID = Integer.parseInt(outTokenizer.nextToken());
			String username = outTokenizer.nextToken();
			ChatBox box = new ChatBox(this,ID);
			box.setVisible(true);
			chats.put(ID, box.getModel());
		}
		else if (output.matches("leave \\d+ [A-Za-z0-9]+")) {
			outTokenizer.nextToken();
			ChatBoxModel currentChatModel = chats.get(outTokenizer.nextToken());
			currentChatModel.quit();
			chats.remove(Integer.parseInt(outTokenizer.nextToken()));
		}
	}
	
	public Socket connect() throws IOException {
        int port = 4444;
        Socket ret = null;
        final int MAX_ATTEMPTS = 50;
        int attempts = 0;
        do {
            try {
                ret = new Socket("127.0.0.1", port);
            } catch (ConnectException ce) {
                try {
                    if (++attempts > MAX_ATTEMPTS)
                        throw new IOException("Exceeded max connection attempts", ce);
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    throw new IOException("Unexpected InterruptedException", ie);
                }
            }
        } while (ret == null);
        ret.setSoTimeout(3000);
        return ret;
    }
}

