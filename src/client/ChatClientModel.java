package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import conversation.ChatHistory;

import user.User;

public class ChatClientModel implements ActionListener {
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
        this.history = new ConcurrentHashMap<Integer, ChatHistory>();
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
        submitCommand("logout " + this.user.getUsername());
    }

    /**
     * Must be run from the event thread to avoid concurrency issues.
     * 
     * @param username
     * @return
     */
    public boolean tryUsername(String username) {
        if (username != null && !username.equals("")) {
            this.submitCommand("login_attempt " + username);
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
    		System.out.println("removing conversation " + Integer.toString(conversationID));
    		ChatBoxModel boxModel = this.chats.remove(conversationID);
    		ChatBox box = boxModel.getChatBox();
    		String message = box.getDisplay().getText();
    		Set<User> others = box.getOthers();
    		ChatHistory currentHistory = new ChatHistory(others,message);
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
        submitCommand("chat_leave " + Integer.toString(ID) + " "
                + user.getUsername());
        removeChat(ID);
    }

    public void sendChat(int ID, String text) {
        submitCommand("say " + Integer.toString(ID) + " " + user.getUsername()
                + " " + text);
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
        if (output.matches("login_success")) {
            try {
                this.messages.put(output);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (output.matches("login_invalid")) {
            try {
                this.messages.put(output);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (output.matches("user_joins [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String username = outTokenizer.nextToken();
                    client.addUser(new User(username));

                    // find private conversation with user
                    if (conversationIDMap.containsKey(username)) {
                        ChatBoxModel temp = chats.get(conversationIDMap
                                .get(username));
                        temp.addMessageToDisplay(username
                                + " has logged back in.");
                    }
                }
            });
        } else if (output.matches("user_leaves [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String username = outTokenizer.nextToken();
                    client.removeUser(username);

                    // find private conversation with user
                    if (conversationIDMap.containsKey(username)) {
                        ChatBoxModel temp = chats.get(conversationIDMap
                                .get(username));
                        temp.addMessageToDisplay(username
                                + " has logged off and can no longer receive messages.");
                    }
                }
            });
        } else if (output.matches("chat_start \\d+ [A-Za-z0-9]+ [A-Za-z0-9]+")) {
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
                            ChatBox box = new ChatBox(temp, ID, "Chat of "
                                    + user.getUsername(), false);
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
                            ChatBox box = new ChatBox(temp, ID, "Chat of "
                                    + user.getUsername(), false);
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
        } else if (output.matches("group_chat_start \\d+")) {
            outTokenizer.nextToken();
            final int ID = Integer.parseInt(outTokenizer.nextToken());
            if (!chats.containsKey(ID)) {
                final ChatClientModel temp = this;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ChatBox box = new ChatBox(temp, ID, "Chat of "
                                + user.getUsername(), true);
                        chats.put(ID, box.getModel());
                    }
                });
            } else {
                // this shouldn't happen
            }
        } else if (output.matches("group_chat_join \\d+ [A-Za-z0-9 ]+")) {
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
                            if (!conversationIDMap.containsKey(username)) {
                                conversationIDMap.put(username, ID);
                            }
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
        } else if (output.matches("group_chat_leave \\d+ [A-Za-z0-9]+")) {
            outTokenizer.nextToken();
            final int ID = Integer.parseInt(outTokenizer.nextToken());
            final String username = outTokenizer.nextToken();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    chats.get(ID).addMessageToDisplay(username + " has left the conversation.");
                }
            });
        } else if (output.matches("say \\d+ [A-Za-z0-9]+ .*")) {
            outTokenizer.nextToken();
            final ChatBoxModel currentChatModel = chats.get(Integer.parseInt(outTokenizer.nextToken()));
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
                // ret = new Socket("18.189.17.62", port);
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
            submitCommand("logout " + user.getUsername());
            this.client.dispose();
            System.exit(0);
        }

    }
}
