package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import client.*;
import javax.swing.SwingUtilities;

import conversation.ChatHistory;

import server.ChatServer;
import user.User;

public class ChatClientModel implements ActionListener{
    private User user;
    private final Socket socket;
    private final ChatClient client;
    private final ConcurrentMap<Integer, ChatBoxModel> chats;
    private final BlockingQueue<String> messages;
    private ConcurrentMap<Integer, ChatHistory> history;
    private ConcurrentMap<String, Integer> conversationIDMap;

    public ChatClientModel(ChatClient client) {
        this.client = client;
        this.user = null;
        chats = new ConcurrentHashMap<Integer, ChatBoxModel>();
        try {
            this.socket = this.connect();
        } catch (IOException e) {
            System.out.println("Failure with connecting to socket.");
            throw new RuntimeException(
                    "Unexpected IOException in ChatClientModel()");
        }
        this.messages = new LinkedBlockingQueue<String>();
        this.history = new ConcurrentHashMap<Integer,ChatHistory>();
        this.conversationIDMap = new ConcurrentHashMap<String, Integer>();
    }

    public void startListening() {
        ClientListeningThread listener = new ClientListeningThread(this);
        listener.start();
    }

    /**
     * Quit all of the open chats.
     */
    public void quitChats() {
        for (Integer ID : chats.keySet()) {
            ChatBoxModel model = chats.remove(ID);
            model.getChatBox().dispose();
        }
    }

    /**
     * Must be run from the event thread to avoid concurrency issues.
     * 
     * @param username
     * @return
     */
    public boolean tryUsername(String username) {
        if (username != null && !username.equals("")) {
        	this.submitCommand("login " + username);
            try {
                String result = this.messages.take();
                if (result.equals("success")) {
                    this.user = new User(username);
                    return true;
                } else if (result.equals("invalid")) {
                    return false;
                } else {
                    throw new RuntimeException(
                            "Unexpected message when trying username: " + result);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(
                        "Unexpected InterruptedException in tryUsername()");
            }
        }
        return false;
    }

    /**
     * Submit a start command to the server
     * 
     * @param other
     *            The User with which the client wants to chat
     */
    public void addChat(User other) {
    	String command = "";
    	if (conversationIDMap.containsKey(other.getUsername())) {
        	command = "join " + Integer.toString(conversationIDMap.get(other.getUsername())) + 
        			" " + this.user.getUsername();
        }
        else {
        	command = "start " + this.user.getUsername() + " " + other.getUsername();
        }
    	System.out.println("client sent " + command);
    	submitCommand(command);
    }
    
    public void removeChat(int conversationID) {
    	System.out.println(this.chats.toString());
    	if (this.chats.containsKey(conversationID)) {
    		System.out.println("removing conversation " + Integer.toString(conversationID));
    		ChatBoxModel boxModel = this.chats.remove(conversationID);
    		ChatBox box = boxModel.getChatBox();
    		String message = box.getDisplay().getText();
    		Set<User> others = box.getOthers();
        	history.put(conversationID, new ChatHistory(others,message));
        	boxModel.quit();
    	}
    }
    
    public void exitChat(int ID) {
    	submitCommand("leave " + Integer.toString(ID) + " " + user.getUsername());
    	removeChat(ID);
    }

    public void sendChat(int ID, long time, String text) {
        submitCommand("say " + Integer.toString(ID) + " " + user.getUsername()
                + " " + Long.toString(time) + " " + text);
    }

    /**
     * Send a command to the server via the socket. The command must follow the
     * grammar detailed in the design document.
     * 
     * @param command
     *            The properly-formatted command that will be sent to the
     *            server.
     */
    public void submitCommand(String command) {
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(command);
        } catch (IOException e) {
            System.out.println("Error with printing output");
        }
    }

    /**
     * Listen for commands sent from the server.
     * 
     * @throws IOException
     */
    public void listenForResponse() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        try {
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                System.out.println("client received line " + line);
                handleRequest(line);
            }
            System.out.println("received null input line, closing now");
        } catch (IOException e) {
            System.out.println("Error with reading info from server.");
            e.printStackTrace();
        } finally {
            System.out.println("closing CHAT CLIENT CONNECTION!!");
            in.close();
        }
    }

    /**
     * Handle output from the server according to the guidelines specified in
     * the design document. Must be run from the event thread to avoid
     * concurrency issues.
     * 
     * @param output
     *            The output from the server.
     */
    public void handleRequest(String output) {
        final StringTokenizer outTokenizer = new StringTokenizer(output);
        if (output.matches("success")) {
            try {
                this.messages.put(output);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (output.matches("invalid")) {
            try {
                this.messages.put(output);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (output.matches("login [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    client.addUser(new User(outTokenizer.nextToken()));
                }
            });
        } else if (output.matches("logout [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    client.removeUser(outTokenizer.nextToken());
                }
            });
        } else if (output.matches("say \\d+ [A-Za-z0-9]+ [0-9]+ .*")) {
            outTokenizer.nextToken();
            final ChatBoxModel currentChatModel = chats.get(Integer
                    .parseInt(outTokenizer.nextToken()));
            String message = output;
            for (int i = 0; i < 4; i++) {
                message = message.substring(message.indexOf(" ") + 1);
            }

            final String chatMessage = message;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    currentChatModel.addChatToDisplay(outTokenizer.nextToken(),
                            outTokenizer.nextToken(), chatMessage);
                }
            });
        } else if (output.matches("join \\d+ [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            final int ID = Integer.parseInt(outTokenizer.nextToken());
            final String username = outTokenizer.nextToken();
            if (username.equals(this.user.getUsername())) {
                final ChatClientModel temp = this;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ChatBox box = new ChatBox(temp, ID, "Chat of " + user.getUsername());
                        box.setVisible(true);
                        chats.put(ID, box.getModel());
                    }
                });
            } else {
            	SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        chats.get(ID).addMessageToDisplay(username + " has joined the conversation.");
                        if (!conversationIDMap.containsKey(username)) {
                            conversationIDMap.put(username, ID);
                        }
                        if (history.containsKey(ID)) {
                        	chats.get(ID).getChatBox().appendMessage(history.get(ID).getHistory());
                        }
                    }
                });
            }
        } else if (output.matches("leave \\d+ [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            final int ID = Integer.parseInt(outTokenizer.nextToken());
            final String username = outTokenizer.nextToken();
            SwingUtilities.invokeLater(new Runnable() {
            	public void run() {
            		chats.get(ID).addMessageToDisplay(
            				username + " has left the conversation.");
            	}
            });
        } else {
            throw new RuntimeException("Illegal message from server: " + output);
        }
    }

    /**
     * Attempt to connect to the server via port 4444 (default port).
     * 
     * @return The socket through which the ChatClientModel is connected the
     *         server.
     * @throws IOException
     *             If there is an error with connecting to the server.
     */
    public Socket connect() throws IOException {
        int port = 4567;
        Socket ret = null;
        final int MAX_ATTEMPTS = 50;
        int attempts = 0;
        do {
            try {
                //ret = new Socket("18.189.17.62", port);
                ret = new Socket("localhost", port);
            } catch (ConnectException ce) {
                try {
                    if (++attempts > MAX_ATTEMPTS)
                        throw new IOException(
                                "Exceeded max connection attempts", ce);
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    throw new IOException("Unexpected InterruptedException", ie);
                }
            }
        } while (ret == null);
        return ret;
    }

    public ChatClient getClient() {
        return this.client;
    }

	@Override
    public void actionPerformed(ActionEvent event) {
	    if (event.getActionCommand().equals("logout")) {
	    	quitChats();
	    	this.client.dispose();
	    	System.exit(0);
	    }
	    
    }
}
