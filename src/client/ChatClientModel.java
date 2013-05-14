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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import conversation.ChatHistory;

import user.User;

/**
 * The ChatClientModel is the implementation of the model portion of the
 * model-view-controller design pattern of our Chat Client.
 */

public class ChatClientModel implements ActionListener {
    private User user; //The user represented by this ChatClientModel object
    private final Socket socket; //The socket through which the client connects to the server
    private final ChatClient client; //The chat client GUI
    private final ConcurrentMap<Integer, ChatBoxModel> chats; //The list of all current open chats
    private final BlockingQueue<String> messages; //The queue of messages from the server that the
    											//ChatClientModel must process
    private ConcurrentMap<Integer, ChatHistory> history; //The chat history
    private ConcurrentMap<String, Integer> conversationIDMap; //For use in private chats; connects
    														//usernames with past chat IDs
    private Set<User> users; //The set of users who are currently online

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
        this.history = new ConcurrentHashMap<Integer, ChatHistory>();
        this.conversationIDMap = new ConcurrentHashMap<String, Integer>();
        this.users = new HashSet<User>();
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
        submitCommand("logout " + this.user.getUsername());
    }

    /**
     * Must be run from the event thread to avoid concurrency issues.
     * 
     * @param username
     * @return
     */
    public boolean tryUsername(String username, int avatar) {
        if (username != null && !username.equals("")) {
            this.submitCommand("login_attempt " + username + " "
                    + Integer.toString(avatar));
            try {
                String result = this.messages.take();
                if (result.equals("login_success")) {
                    this.user = new User(username);
                    return true;
                } else if (result.equals("login_invalid")) {
                    return false;
                } else {
                    throw new RuntimeException(
                            "Unexpected message when trying username: "
                                    + result);
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
        if (conversationIDMap.containsKey(other.getUsername())) {
            int ID = conversationIDMap.get(other.getUsername());
            chats.get(ID).show();
        } else {
            String command = "chat_start " + this.user.getUsername() + " "
                    + other.getUsername();
            System.out.println("client sent " + command);
            submitCommand(command);
        }
    }

    public void addGroupChat(Set<User> others) {
        String command = "group_chat_start " + this.user.getUsername() + " ";
        for (User other : others) {
            command += other.getUsername() + " ";
        }
        submitCommand(command.substring(0, command.length() - 1));
    }

    public void removeChat(int conversationID) {
        System.out.println(this.chats.toString());
        if (this.chats.containsKey(conversationID)) {
            System.out.println("removing conversation "
                    + Integer.toString(conversationID));
            ChatBoxModel boxModel = this.chats.remove(conversationID);
            ChatBox box = boxModel.getChatBox();
            String message = box.getDisplay().getText();
            Set<User> historyOthers = new HashSet<User>();
            Set<User> others = box.getOthers();
            for (User user: others) {
            	historyOthers.add(user);
            }
            Set<User> oldOthers = box.getLeftChat();
            for (User user: oldOthers) {
            	historyOthers.add(user);
            }
            ChatHistory currentHistory = new ChatHistory(historyOthers, message);
            history.put(conversationID, currentHistory);
            client.addHistory(currentHistory, conversationID);
            boxModel.quit();
        }
    }

    public void showChatHistory(int ID) {
        ChatHistory currentHistory = history.get(ID);
        HistoryBox box = new HistoryBox(currentHistory);
        box.setVisible(true);
    }

    public void exitChat(int ID) {
        submitCommand("group_chat_leave " + Integer.toString(ID) + " "
                + user.getUsername());
        removeChat(ID);
    }

    public void sendChat(int ID, String text) {
        // divide text into multiple lines
        StringTokenizer lineBreaker = new StringTokenizer(text, "\n");
        while (lineBreaker.hasMoreTokens()) {
            submitCommand("say " + Integer.toString(ID) + " "
                    + user.getUsername() + " " + lineBreaker.nextToken());
        }
    }

    public void sendTyping(int ID) {
        submitCommand("typing " + Integer.toString(ID) + " "
                + user.getUsername());
    }

    public void sendCleared(int ID) {
        submitCommand("cleared " + Integer.toString(ID) + " "
                + user.getUsername());
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
        if (output.matches("login_success")) {
        	processLoginSuccessCommand(output);
        } else if (output.matches("login_invalid")) {
        	processLoginInvalidCommand(output);
        } else if (output.matches("user_joins [A-Za-z0-9]+ \\d+")) {
            processUserJoinsCommand(output);
        } else if (output.matches("user_leaves [A-Za-z0-9]+")) {
            processUserLeavesCommand(output);
        } else if (output.matches("chat_start \\d+ [A-Za-z0-9]+ [A-Za-z0-9]+")) {
            processChatStartCommand(output);
        } else if (output.matches("group_chat_start \\d+")) {
            processGroupChatStartCommand(output);
        } else if (output.matches("group_chat_join \\d+ [A-Za-z0-9 ]+")) {
            processGroupChatJoinCommand(output);
        } else if (output.matches("group_chat_leave \\d+ [A-Za-z0-9]+")) {
            processGroupChatLeaveCommand(output);
        } else if (output.matches("say \\d+ [A-Za-z0-9]+ .*")) {
            processSayCommand(output);
        } else if (output.matches("typing \\d+ [A-Za-z0-9]+")) {
            processTypingCommand(output);
        } else if (output.matches("cleared \\d+ [A-Za-z0-9]+")) {
            processClearedCommand(output);
        } else {
            throw new RuntimeException("Illegal message from server: " + output);
        }
    }
    
    //HELPER METHODS FOR HANDLEREQUEST METHOD
    public void processLoginSuccessCommand(String output) {
    	try {
            this.messages.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void processLoginInvalidCommand(String output) {
    	try {
            this.messages.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void processUserJoinsCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String username = outTokenizer.nextToken();
                int avatar = Integer.parseInt(outTokenizer.nextToken());
                users.add(new User(username, avatar));
                client.setUserList(users);

                // find private conversation with user
                if (conversationIDMap.containsKey(username)) {
                    ChatBoxModel temp = chats.get(conversationIDMap
                            .get(username));
                    temp.addMessageToDisplay(username
                            + " has logged back in.");
                }
            }
        });
    }
    
    public void processUserLeavesCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String username = outTokenizer.nextToken();
                users.remove(new User(username));
                client.setUserList(users);

                // find private conversation with user
                if (conversationIDMap.containsKey(username)) {
                    ChatBoxModel temp = chats.get(conversationIDMap
                            .get(username));
                    temp.addMessageToDisplay(username
                            + " has logged off and can no longer receive messages.");
                }
            }
        });
    }
    
    public void processChatStartCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final int ID = Integer.parseInt(outTokenizer.nextToken());
        final String username1 = outTokenizer.nextToken();
        final String username2 = outTokenizer.nextToken();
        if (!chats.containsKey(ID)) {
            if (username1.equals(this.user.getUsername())) {
                final ChatClientModel temp = this;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!conversationIDMap.containsKey(username2)) {
                            conversationIDMap.put(username2, ID);
                        }
                        ChatBox box = new ChatBox(temp, ID,
                                user.getUsername() + ": chat with "
                                        + username2, false);
                        box.setVisible(true);
                        chats.put(ID, box.getModel());
                    }
                });
            } else if (username2.equals(this.user.getUsername())) {
                final ChatClientModel temp = this;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!conversationIDMap.containsKey(username1)) {
                            conversationIDMap.put(username1, ID);
                        }
                        ChatBox box = new ChatBox(temp, ID,
                                user.getUsername() + ": chat with "
                                        + username1, false);
                        chats.put(ID, box.getModel());
                    }
                });
            } else {
                // pass
            }
        } else {
            if (username1.equals(this.user.getUsername())) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        chats.get(ID)
                                .addMessageToDisplay(
                                        username2
                                                + " has joined the conversation.");
                        if (history.containsKey(ID)) {
                            chats.get(ID)
                                    .getChatBox()
                                    .appendMessage(
                                            history.get(ID).getHistory());
                        }
                    }
                });
            } else if (username2.equals(this.user.getUsername())) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        chats.get(ID)
                                .addMessageToDisplay(
                                        username1
                                                + " has joined the conversation.");
                        if (history.containsKey(ID)) {
                            chats.get(ID)
                                    .getChatBox()
                                    .appendMessage(
                                            history.get(ID).getHistory());
                        }
                    }
                });
            } else {
                // pass
            }
        }
    }
    
    public void processGroupChatStartCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final int ID = Integer.parseInt(outTokenizer.nextToken());
        if (!chats.containsKey(ID)) {
            final ChatClientModel temp = this;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ChatBox box = new ChatBox(temp, ID, "Chat of "
                            + user.getUsername(), true);
                    chats.put(ID, box.getModel());
                    box.setVisible(true);
                }
            });
        }
    }
    
    public void processGroupChatJoinCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final int ID = Integer.parseInt(outTokenizer.nextToken());
        if (!chats.containsKey(ID)) {
            final String username = outTokenizer.nextToken();
            if (username.equals(this.user.getUsername())) {
                // pass: this shouldn't happen
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        chats.get(ID).addMessageToDisplay(
                                username + " has joined the conversation.");
                        // if (!conversationIDMap.containsKey(username)) {
                        // conversationIDMap.put(username, ID);
                        // }
                        chats.get(ID).getChatBox()
                                .addOther(new User(username));
                        if (history.containsKey(ID)) {
                            chats.get(ID)
                                    .getChatBox()
                                    .appendMessage(
                                            history.get(ID).getHistory());
                        }
                    }
                });
            }
        }
    }
    
    public void processGroupChatLeaveCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final int ID = Integer.parseInt(outTokenizer.nextToken());
        final String username = outTokenizer.nextToken();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chats.get(ID).addMessageToDisplay(
                        username + " has left the conversation.");
                chats.get(ID).getChatBox().removeOther(new User(username));
            }
        });
    }
    
    public void processSayCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final ChatBoxModel currentChatModel = chats.get(Integer
                .parseInt(outTokenizer.nextToken()));
        String message = output;
        for (int i = 0; i < 3; i++) {
            message = message.substring(message.indexOf(" ") + 1);
        }

        final String chatMessage = message;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                currentChatModel.addChatToDisplay(outTokenizer.nextToken(),
                        chatMessage);
            }
        });
    }
    
    public void processTypingCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final ChatBoxModel currentChatModel = chats.get(Integer
                .parseInt(outTokenizer.nextToken()));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                currentChatModel.markTyping(outTokenizer.nextToken());
            }
        });
    }
    
    public void processClearedCommand(String output) {
    	final StringTokenizer outTokenizer = new StringTokenizer(output);
    	outTokenizer.nextToken();
        final ChatBoxModel currentChatModel = chats.get(Integer
                .parseInt(outTokenizer.nextToken()));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                currentChatModel.markCleared(outTokenizer.nextToken());
            }
        });
    }

    /**
     * Attempt to connect to the server via port 4444 (default port).
     * 
     * @return The socket through which the ChatClientModel is connected the server.
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
                //ret = new Socket("128.31.35.165", port);
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
    
    //ACCESSORS
    public ChatClient getClient() {return this.client;}
    public Set<User> getUsers() {return users;}
    public User getUser() {return user;}

    /**
     * Senses when the user closes the ChatClient window or presses the logout button
     * in the ChatClient window. Quits all of the chats that the user is in, alerts
     * the server that the user is logging out, and quits the ChatClient.
     * 
     * @param event An ActionEvent performed on the ChatClient.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("logout")) {
            quitChats();
            submitCommand("logout " + user.getUsername());
            this.client.dispose();
            System.exit(0);
        }

    }
}
