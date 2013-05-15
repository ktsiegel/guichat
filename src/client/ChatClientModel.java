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
 * model-view-controller design pattern of our Chat Client. It contains
 * the information needed for each user to use the chat client and
 * for the chat client to communicate with the server.
 */

public class ChatClientModel implements ActionListener {
    private User user; // The user represented by this ChatClientModel object
    private final Socket socket; // The socket through which the client connects
                                 // to the server
    private final ChatClient client; // The chat client GUI
    private final ConcurrentMap<Integer, ChatBoxModel> chats; // The list of all
                                                              // current open
                                                              // chats
    private final BlockingQueue<String> messages; // The queue of messages from
                                                  // the server that the
                                                  // ChatClientModel must
                                                  // process
    private ConcurrentMap<Integer, ChatHistory> history; // The chat history
    private ConcurrentMap<String, Integer> conversationIDMap; // For use in
                                                              // private chats;
                                                              // connects
                                                              // usernames with
                                                              // past chat IDs
    private Set<User> users; // The set of users who are currently online

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

    /**
     * Creates a separate thread for listening for output from the server to the
     * client.
     */
    public void startListening() {
        ClientListeningThread listener = new ClientListeningThread(this);
        listener.start();
    }

    /**
     * Quit all of the open chats and submit a logout command to the server.
     */
    public void quitChats() {
        for (Integer ID : chats.keySet()) {
            ChatBoxModel model = chats.remove(ID);
            model.getChatBox().dispose();
        }
        submitCommand("logout " + this.user.getUsername());
    }

    /**
     * Attempt to set a client's username and avatar by submitting a login
     * command to the server and seeing if the server reports that the login was
     * successful.
     * 
     * @throws RuntimeException
     *             if an unusual message is sent from the server after the login
     *             command is submitted.
     * 
     * @precondition Must be run from the event thread to avoid concurrency
     *               issues.
     * 
     * @param username
     *            The username that the user has inputted.
     * @param avatar
     *            The integer ID of the username that the user chose.
     * @return whether the login was successful (i.e. the username was valid and
     *         not taken)
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
     * If the chat already exists, but is hidden, make the associated chat box
     * visible. Otherwise, submit a chat start command to the server.
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

    /**
     * Create a group chat by sending a group_chat_start command to all of the
     * users invited to the group chat.
     * 
     * @param others
     */
    public void addGroupChat(Set<User> others) {
        String command = "group_chat_start " + this.user.getUsername() + " ";
        for (User other : others) {
            command += other.getUsername() + " ";
        }
        submitCommand(command.substring(0, command.length() - 1));
    }

    /**
     * Alert the server that a user is leaving a chat, then remove the actual
     * chat.
     * 
     * @param ID
     *            The ID of the chat that the user is leaving.
     */
    public void exitChat(int ID) {
        submitCommand("group_chat_leave " + Integer.toString(ID) + " "
                + user.getUsername());
        removeChat(ID);
    }

    /**
     * Remove this user from a particular chat, storing the chat history in the
     * process.
     * 
     * @param conversationID
     *            The ID of the chat conversation from which the user should be
     *            removed.
     */
    public void removeChat(int conversationID) {
        System.out.println(this.chats.toString()); // debugging code
        if (this.chats.containsKey(conversationID)) {
            System.out.println("removing conversation "
                    + Integer.toString(conversationID)); // debugging code
            // Get text associated with this conversation for the purposes of
            // storing history.
            ChatBoxModel boxModel = this.chats.remove(conversationID);
            ChatBox box = boxModel.getChatBox();
            String message = box.getDisplay().getText();

            Set<User> historyOthers = new HashSet<User>(); // the set that will
                                                           // contain
                                                           // all of the people
                                                           // who were ever
                                                           // in the group chat
            Set<User> others = box.getOthers();
            for (User user : others) {
                historyOthers.add(user);
            }
            Set<User> oldOthers = box.getLeftChat();
            for (User user : oldOthers) {
                historyOthers.add(user);
            }
            ChatHistory currentHistory = new ChatHistory(historyOthers, message);
            history.put(conversationID, currentHistory); // Store chat history
            client.addHistory(currentHistory, conversationID);
            boxModel.quit();
        }
    }

    /**
     * Display the chat history of the chat associated with ID in a new
     * HistoryBox.
     * 
     * @param ID
     *            The ID of the chat for which we want to view the history
     */
    public void showChatHistory(int ID) {
        ChatHistory currentHistory = history.get(ID);
        HistoryBox box = new HistoryBox(currentHistory);
        box.setVisible(true);
    }

    /**
     * Sends a message using say commands to the server, where it will be sent
     * to the appropriate recipients.
     * 
     * @param ID
     *            The ID corresponding to the chat
     * @param text
     */
    public void sendChat(int ID, String text) {
        // divide text into multiple lines
        StringTokenizer lineBreaker = new StringTokenizer(text, "\n");
        while (lineBreaker.hasMoreTokens()) {
            submitCommand("say " + Integer.toString(ID) + " "
                    + user.getUsername() + " " + lineBreaker.nextToken());
        }
    }

    /**
     * Alerts the server that this user is typing.
     * 
     * @param ID
     *            The ID corresponding to the chat box in which this user is
     *            typing.
     */
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

    // HELPER METHODS FOR HANDLEREQUEST METHOD

    /**
     * Alerts the user that the login attempt was a success.
     * 
     * @param output
     *            The message from the server indicating the login success.
     */
    public void processLoginSuccessCommand(String output) {
        try {
            this.messages.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alerts the user that the login attempt was a failure.
     * 
     * @param output
     *            The message from the server indicating the login attempt was a
     *            failure.
     */
    public void processLoginInvalidCommand(String output) {
        try {
            this.messages.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alerts the user that some user has logged in.
     * 
     * @param output
     *            The message from the server indicating that a user has logged
     *            in.
     */
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
                    temp.addMessageToDisplay(username + " has logged back in.");
                }
            }
        });
    }

    /**
     * Alerts the user that some user has logged out.
     * 
     * @param output
     *            The message from the server indicating that a user has logged
     *            out.
     */
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

    /**
     * Processes a command from the server indicating that the client has
     * started a chat with another user, and that the GUI should adjust as such.
     * 
     * @param output
     *            The message from the server indicating that the client has
     *            started a chat with another user.
     */
    public void processChatStartCommand(String output) {
        final StringTokenizer outTokenizer = new StringTokenizer(output);
        outTokenizer.nextToken();
        final int ID = Integer.parseInt(outTokenizer.nextToken());
        final String username1 = outTokenizer.nextToken();
        final String username2 = outTokenizer.nextToken();
        if (!chats.containsKey(ID)) { // create an entirely new chat
            if (username1.equals(this.user.getUsername())) {
                newChatCreation(username2, ID, true);
            } else if (username2.equals(this.user.getUsername())) {
                newChatCreation(username1, ID, false);
            }
        } else { // this chat should use history from a previous chat
            if (username1.equals(this.user.getUsername())) {
                oldChatRevival(username2, ID);
            } else if (username2.equals(this.user.getUsername())) {
                oldChatRevival(username1, ID);
            }
        }
    }

    /**
     * Create an entirely new chat between this user and another user.
     * 
     * @param username
     *            The other user in the chat.
     * @param ID
     *            The ID corresponding to the new chat.
     * @param popup
     *            True if the chat window should be open by default and false
     *            otherwise.
     */
    public void newChatCreation(final String username, final int ID,
            final boolean popup) {
        final ChatClientModel temp = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!conversationIDMap.containsKey(username)) {
                    conversationIDMap.put(username, ID);
                }
                ChatBox box = new ChatBox(temp, ID, user.getUsername()
                        + ": chat with " + username, false);
                box.setVisible(popup);
                chats.put(ID, box.getModel());
            }
        });
    }

    /**
     * Revive an old chat between this user and another user.
     * 
     * @param username
     *            The other user in the chat.
     * @param ID
     *            The ID corresponding to the new chat.
     */
    public void oldChatRevival(final String username, final int ID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chats.get(ID).addMessageToDisplay(
                        username + " has joined the conversation.");
                if (history.containsKey(ID)) {
                    chats.get(ID).getChatBox()
                            .appendMessage(history.get(ID).getHistory());
                }
            }
        });
    }

    /**
     * Processes a command from the server indicating that the user has bee
     * placed into a group chat with other users, and that the GUI should adjust
     * as such.
     * 
     * @param output
     *            The message from the server indicating that the new group chat
     *            has been started.
     */
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

    /**
     * Processes a command from the server indicating that some other user has
     * joined a group chat that the user is in.
     * 
     * @param output
     *            The message from the server indicating that the user has
     *            joined the group chat.
     */
    public void processGroupChatJoinCommand(String output) {
        final StringTokenizer outTokenizer = new StringTokenizer(output);
        outTokenizer.nextToken();
        final int ID = Integer.parseInt(outTokenizer.nextToken());

        final String username = outTokenizer.nextToken();
        if (!username.equals(this.user.getUsername())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (chats.containsKey(ID)) {
                        chats.get(ID).addMessageToDisplay(
                                username + " has joined the conversation.");
                        chats.get(ID).getChatBox().addOther(new User(username));
                    }
                }
            });
        }
    }

    /**
     * Processes a command from the server indicating that some other user has
     * left a group chat that the user is in.
     * 
     * @param output
     *            The message from the server indicating that the user has left
     *            the group chat.
     */
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

    /**
     * Processes a command from the server indicating that someone has said
     * something in a particular chat that the user is in.
     * 
     * @param output
     *            The message from the server indicating that a user has said
     *            something in a particular chat.
     */
    public void processSayCommand(String output) {
        final StringTokenizer outTokenizer = new StringTokenizer(output);
        outTokenizer.nextToken();
        final ChatBoxModel currentChatModel = chats.get(Integer
                .parseInt(outTokenizer.nextToken()));
        String message = output;
        for (int i = 0; i < 3; i++) { // The message is the text after the third
                                      // space in the output
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

    /**
     * Processes a command from the server indicating that someone is currently
     * typing.
     * 
     * @param output
     *            The message from the server indicating that a user is
     *            currently typing.
     */
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

    /**
     * Processes a command from the server indicating that someone is no longer
     * typing and/or has no longer entered text in a chat box editable text
     * field.
     * 
     * @param output
     *            The message from the server indicating that a user is no
     *            longer typing/has no more unsubmitted text in a chat box.
     */
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
                // ret = new Socket("128.31.35.165", port);
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

    // ACCESSORS
    public ChatClient getClient() {
        return this.client;
    }

    public Set<User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    /**
     * Senses when the user closes the ChatClient window or presses the logout
     * button in the ChatClient window. Quits all of the chats that the user is
     * in, alerts the server that the user is logging out, and quits the
     * ChatClient.
     * 
     * @param event
     *            An ActionEvent performed on the ChatClient.
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
