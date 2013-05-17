package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import conversation.Conversation;

import user.User;

/**
 * A class to run a ChatServer through which clients can connect and interact
 * with other connected clients. Basic functionality includes logging in with a
 * username, being notified of other logged in users, starting private
 * conversations, starting group conversations, and sending messages to other
 * users in conversations.
 */
public class ChatServer {
    private final ServerSocket serverSocket; // The ServerSocket used for
                                             // communications.
    private final Map<User, Socket> clients; // Holds the Socket associated with
                                             // each connected User.
    private final Map<Integer, Conversation> conversations; // Holds the
                                                            // Conversation
                                                            // associated with
                                                            // each (unique)
                                                            // conversation ID.
    private final BlockingQueue<CommunicationsData> queue; // Holds a list of
                                                           // messages from
                                                           // clients to be
                                                           // processed.

    /**
     * Creates a ChatServer with the given port. Does not start listening for
     * messages until the serve() method is called.
     * 
     * @param port
     *            An integer port to use for the connection. An error will be
     *            thrown if this port is invalid.
     */
    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected IOException when creating server socket with port "
                            + port);
        }
        clients = new HashMap<User, Socket>();
        conversations = new HashMap<Integer, Conversation>();
        queue = new LinkedBlockingQueue<CommunicationsData>();
    }

    /**
     * First, this method creates a thread to start processing messages from the
     * ChatServer's blocking queue. Next, this method starts an infinite loop
     * that waits for new clients to connect and spawns a new
     * ChatServerClientThread to read messages from these clients.
     */
    public void serve() {
        // start a new thread to work
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        });
        worker.start();

        while (true) {
            // block until a client connects
            try {
                Socket socket = this.serverSocket.accept();

                // create a new thread for this socket
                Thread thread = new Thread(new ChatServerClientThread(socket,
                        this));
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a message (associated with the given Socket) to the server's
     * blocking queue for future processing.
     * 
     * @param message
     *            The message to add.
     * @param socket
     *            The Socket associated with the message.
     */
    public void addMessageToQueue(String message, Socket socket) {
        try {
            this.queue.put(new CommunicationsData(message, socket));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected InterruptedException in addMessageToQueue()");
        }
    }

    /**
     * Used to notify the server that a certain socket connection has ended. In
     * the case that the user did not formally log out and instead forced the
     * connection to close (perhaps by quitting the program), this method tells
     * the server to disconnect the User associated with the Socket, if such a
     * User exists.
     * 
     * @param socket
     *            The socket whose connection has ended.
     */
    public void forceLogout(Socket socket) {
        for (User user : this.clients.keySet()) {
            if (this.clients.get(user).equals(socket)) {
                this.addMessageToQueue("logout " + user.getUsername(), socket);
            }
        }
    }

    /**
     * Returns a new conversation ID to use for a new conversation. This
     * conversation ID is guaranteed to be unique among all existing
     * conversations. Will throw an error if there does not exist a valid
     * conversation ID in the range [1, Integer.MAX_VALUE).
     * 
     * @return a new conversation ID to use for a new conversation, guaranteed
     *         to be unique among all active conversations.
     */
    private int nextConversationID() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!conversations.containsKey(i)) {
                return i;
            }
        }
        throw new RuntimeException("Ran out of valid conversation IDs");
    }

    /**
     * Writes a message to the given socket. This method on its own is not
     * thread-safe. If the message fails to send, then nothing happens. This is
     * to protect sudden socket disconnects from breaking the system.
     * 
     * @param message
     *            The String message to be processed.
     * @param socket
     *            The Socket in which the message was received from.
     */
    private void writeMessageToSocket(String message, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses writeMessageToSocket() to send a message to a specific user.
     * 
     * @param message
     *            The message to send to a user.
     * @param user
     *            Any User (username must be specified, but the avatar can be
     *            anything) that is currently online and in this.clients.
     */
    private void sendMessageToUser(String message, User user) {
        if (this.clients.containsKey(user)) {
            writeMessageToSocket(message, this.clients.get(user));
        } else {
            throw new IllegalArgumentException(
                    "One of the Users to send a message to does not exist");
        }
    }

    /**
     * Sends a message to every User in a given list (in an arbitrary order).
     * 
     * @param message
     *            The message to send.
     * @param targets
     *            A list of Users who will receive the message. Only username
     *            must be specified for each User. Each User must be currently
     *            online and in this.clients.
     */
    private void sendMessageToUsers(String message, Iterable<User> targets) {
        for (User user : targets) {
            this.sendMessageToUser(message, user);
        }
    }

    /**
     * A method that reads messages from the blocking queue, waiting for new
     * messages when there are none.
     * 
     * Each message is processed accordingly and messages are sent back to the
     * sockets that they came from.
     */
    private void work() {
        while (true) {
            String message;
            Socket socket;

            try {
                CommunicationsData next = this.queue.take();
                message = next.getMessage();
                socket = next.getSocket();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(
                        "Unexpected InterruptedException in work()");
            }

            String[] split = message.split(" ");

            try {
                if (split[0].equals("login_attempt")) {
                    this.processLoginAttemptCommand(message, socket);
                } else if (split[0].equals("logout")) {
                    this.processLogoutCommand(message);
                } else if (split[0].equals("chat_start")) {
                    this.processChatStartCommand(message);
                } else if (split[0].equals("group_chat_start")) {
                    this.processGroupChatStartCommand(message);
                } else if (split[0].equals("group_chat_leave")) {
                    this.processGroupChatLeaveCommand(message);
                } else if (split[0].equals("say")) {
                    this.processSayCommand(message);
                } else if (split[0].equals("typing")) {
                    this.processTypingCommand(message);
                } else if (split[0].equals("cleared")) {
                    this.processClearedCommand(message);
                } else {
                    throw new IllegalStateException(
                            "Unexpected command received from client by server: "
                                    + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes a message that logs in a user from given socket with a given
     * requested username. If the new username is valid (not taken), then this
     * username is added to the list of online clients. The "login_success"
     * message is returned through the socket. Then, all currently online
     * clients are notified that this new user has logged in and the new user is
     * notified of all the clients that are already logged in. Finally, the new
     * user rejoins all private conversations that he or she was previously
     * involved in. If the username is not valid, then "login_invalid" is sent
     * back to the user through the socket.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "login_attempt SPACE username SPACE avatar".
     * @param socket
     *            The socket from which the message was received.
     */
    private void processLoginAttemptCommand(String message, Socket socket) {
        String[] split = message.split(" ");

        if (split.length != 3) {
            throw new IllegalStateException(
                    "Invalid login_attempt message received from client by server");
        }

        String username = split[1];
        int avatar = Integer.parseInt(split[2]);
        User user = new User(username, avatar);

        if (clients.containsKey(user)) {
            this.writeMessageToSocket("login_invalid", socket);
        } else {
            this.writeMessageToSocket("login_success", socket);

            // notify all current users that a new user has joined
            this.sendMessageToUsers(
                    "user_joins " + username + " " + Integer.toString(avatar),
                    this.clients.keySet());

            // add new user to list
            this.clients.put(user, socket);

            // notify new user of logged-in users
            for (User onlineUser : this.clients.keySet()) {
                this.sendMessageToUser("user_joins " + onlineUser.getUsername()
                        + " " + onlineUser.getAvatar(), user);
            }

            // have the user rejoin all private conversations
            for (int ID : this.conversations.keySet()) {
                Conversation conversation = this.conversations.get(ID);
                if (!conversation.isGroupChat()) {
                    Iterator<User> iterator = conversation.getUsers()
                            .iterator();
                    User a = iterator.next();
                    User b = iterator.next();
                    if (!a.equals(user) && b.equals(user)) {
                        this.sendMessageToUser(
                                "chat_start " + conversation.getID() + " "
                                        + a.getUsername() + " "
                                        + b.getUsername(), user);
                    } else if (a.equals(user) && !b.equals(user)) {
                        this.sendMessageToUser(
                                "chat_start " + conversation.getID() + " "
                                        + b.getUsername() + " "
                                        + a.getUsername(), user);
                    }
                }
            }
        }
    }

    /**
     * Logs a user out of the server if the user is not already logged out.
     * Notifies all connected clients that the user has logged out. All group
     * chats that the user was a part of are notified that this user has left.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "logout SPACE username".
     */
    private void processLogoutCommand(String message) {
        String[] split = message.split(" ");

        if (split.length != 2) {
            throw new IllegalStateException(
                    "Invalid logout message received from client by server");
        }

        String username = split[1];

        if (this.clients.containsKey(new User(username))) {
            this.clients.remove(new User(username));

            // leave all conversations
            for (int chatID : this.conversations.keySet()) {
                Conversation chat = this.conversations.get(chatID);
                if (chat.getUsers().contains(new User(username))
                        && chat.isGroupChat()) {
                    chat.removeUser(new User(username));
                    // send a leave message
                    this.sendMessageToUsers("group_chat_leave " + chatID + " "
                            + username, chat.getUsers());
                }
            }

            // notify all clients that a new user has logged in
            this.sendMessageToUsers("user_leaves " + username,
                    this.clients.keySet());
        }
    }

    /**
     * Notifies the server that a new private conversation between two users has
     * begun.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "chat_start SPACE username SPACE username". The first username
     *            is the user that started the conversation. The second username
     *            is the user that was invited to the conversation.
     */
    private void processChatStartCommand(String message) {
        String[] split = message.split(" ");
        if (split.length != 3) {
            throw new IllegalStateException(
                    "Invalid chat_start message received from client by server");
        }

        String username1 = split[1];
        User user1 = new User(username1);
        if (!this.clients.containsKey(user1)) {
            throw new IllegalStateException(
                    "Invalid chat_start message received from client by server: unknown user");
        }

        String username2 = split[2];
        User user2 = new User(username2);
        if (!this.clients.containsKey(user2)) {
            throw new IllegalStateException(
                    "Invalid chat_start message received from client by server: unknown user");
        }

        Conversation chat = new Conversation(user1, user2,
                this.nextConversationID());
        conversations.put(chat.getID(), chat);

        this.sendMessageToUser("chat_start " + chat.getID() + " " + username1
                + " " + username2, user1);
        this.sendMessageToUser("chat_start " + chat.getID() + " " + username1
                + " " + username2, user2);
    }

    /**
     * Starts a group chat among a list of users. Everyone involved will be
     * notified of the new conversation and the users involved.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "group_chat_start (SPACE username)+".
     */
    private void processGroupChatStartCommand(String message) {
        String[] split = message.split(" ");

        if (split.length == 1) {
            throw new IllegalStateException(
                    "Invalid group_chat_start message received from client by server");
        }

        Set<User> users = new HashSet<User>();
        for (int i = 1; i < split.length; i++) {
            String username = split[i];
            users.add(new User(username));
        }

        for (User user : users) {
            if (!this.clients.containsKey(user)) {
                throw new IllegalStateException(
                        "Invalid group_chat_start message received from client by server: unknown user");
            }
        }

        Conversation chat = new Conversation(users, this.nextConversationID());
        conversations.put(chat.getID(), chat);

        for (User user : users) {
            // first send a message to the user that is joining
            Set<User> targetUserSet = new HashSet<User>();
            targetUserSet.add(user);
            this.sendMessageToUsers("group_chat_start " + chat.getID(),
                    targetUserSet);

            // then notify the user that all the other users are joining
            for (User otherUser : users) {
                if (!otherUser.equals(user)) {
                    this.sendMessageToUsers("group_chat_join " + chat.getID()
                            + " " + otherUser.getUsername(), targetUserSet);
                }
            }
        }
    }

    /**
     * Notifies the server that a user has left a group conversation. This will
     * also notify all the users still in the group conversation that someone
     * has left.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "group_chat_leave SPACE id SPACE username".
     */
    private void processGroupChatLeaveCommand(String message) {
        String[] split = message.split(" ");

        if (split.length != 3) {
            throw new IllegalStateException(
                    "Invalid group_chat_leave message received from client by server");
        }
        int ID = Integer.parseInt(split[1]);
        String username = split[2];

        if (!this.clients.containsKey(new User(username))) {
            throw new IllegalStateException(
                    "Invalid group_chat_leave message received from client by server: unknown user");
        }

        Conversation chat = this.conversations.get(ID);
        chat.removeUser(new User(username));

        this.sendMessageToUsers("group_chat_leave " + ID + " " + username,
                chat.getUsers());
    }

    /**
     * Notifies the server that a new message has been said in a conversation.
     * The server notifies all clients to add this message to the conversation.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "say SPACE id SPACE username SPACE text".
     */
    private void processSayCommand(String message) {
        String[] split = message.split(" ");

        if (split.length < 3) {
            throw new IllegalStateException(
                    "Invalid say message received from client by server");
        }
        int ID = Integer.parseInt(split[1]);
        String username = split[2];

        if (!this.clients.containsKey(new User(username))) {
            throw new IllegalStateException(
                    "Invalid start message received from client by server: unknown user");
        }

        String text = message;
        for (int i = 0; i < 3; i++) {
            if (text.indexOf(" ") < 0) {
                text = "";
            } else {
                text = text.substring(text.indexOf(" ") + 1);
            }
        }

        Conversation chat = this.conversations.get(ID);
        for (User user : chat.getUsers()) {
            if (this.clients.containsKey(user)) {
                this.sendMessageToUser("say " + ID + " " + username + " "
                        + text, user);
            }
        }
    }

    /**
     * Notifies the server that someone is typing in a conversation. The server
     * then notifies all the users in the conversation.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "typing SPACE id SPACE username".
     */
    private void processTypingCommand(String message) {
        String[] split = message.split(" ");

        if (split.length != 3) {
            throw new IllegalStateException(
                    "Invalid typing message received from client by server");
        }
        int ID = Integer.parseInt(split[1]);
        String username = split[2];

        if (!this.clients.containsKey(new User(username))) {
            throw new IllegalStateException(
                    "Invalid typing message received from client by server: unknown user");
        }

        Conversation chat = this.conversations.get(ID);
        for (User user : chat.getUsers()) {
            if (!user.equals(new User(username))
                    && this.clients.containsKey(user)) {
                this.sendMessageToUser("typing " + ID + " " + username, user);
            }
        }
    }

    /**
     * Notifies the server that someone was typing but then cleared all his or
     * her text in a conversation. The server then notifies all the users in the
     * conversation.
     * 
     * @param message
     *            The message from the client. Must be in the form
     *            "cleared SPACE id SPACE username".
     */
    private void processClearedCommand(String message) {
        String[] split = message.split(" ");

        if (split.length != 3) {
            throw new IllegalStateException(
                    "Invalid cleared message received from client by server");
        }
        int ID = Integer.parseInt(split[1]);
        String username = split[2];

        if (!this.clients.containsKey(new User(username))) {
            throw new IllegalStateException(
                    "Invalid cleared message received from client by server: unknown user");
        }

        Conversation chat = this.conversations.get(ID);
        for (User user : chat.getUsers()) {
            if (!user.equals(new User(username))
                    && this.clients.containsKey(user)) {
                this.sendMessageToUser("cleared " + ID + " " + username, user);
            }
        }
    }
}
