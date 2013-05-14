package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import conversation.Conversation;

import user.User;

public class ChatServer {
    private final ServerSocket serverSocket;
    private final Map<User, Socket> clients;
    private final Map<Integer, Conversation> conversations;
    private final BlockingQueue<CommunicationsData> queue;

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

    public void serve() throws IOException {
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
            Socket socket = this.serverSocket.accept();

            // create a new thread for this socket
            Thread thread = new Thread(new ChatServerClientThread(socket, this));
            thread.start();
        }
    }

    private int nextConversationID() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!conversations.containsKey(i)) {
                return i;
            }
        }
        throw new RuntimeException("Ran out of valid conversation IDs");
    }

    private void writeMessageToSocket(String message, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected IOException when printing message to socket");
        }
    }

    private void sendMessageToUser(String message, User user) {
        if (this.clients.containsKey(user)) {
            System.out.println("sending \"" + message + "\" to "
                    + user.getUsername());

            writeMessageToSocket(message, this.clients.get(user));
        } else {
            throw new IllegalArgumentException(
                    "One of the Users to send a message to does not exist");
        }
    }

    private void sendMessageToUsers(String message, Iterable<User> targets) {
        for (User user : targets) {
            this.sendMessageToUser(message, user);
        }
    }

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

            if (split[0].equals("login_attempt")) {
                if (split.length != 2) {
                    throw new IllegalStateException(
                            "Invalid login_attempt message received from client by server");
                }

                String username = split[1];
                User user = new User(username);

                if (clients.containsKey(user)) {
                    this.writeMessageToSocket("login_invalid", socket);
                } else {
                    this.writeMessageToSocket("login_success", socket);

                    // notify all current users that a new user has joined
                    this.sendMessageToUsers("user_joins " + username,
                            this.clients.keySet());

                    // add new user to list
                    this.clients.put(user, socket);

                    // notify new user of logged-in users
                    for (User onlineUser : this.clients.keySet()) {
                        this.sendMessageToUser(
                                "user_joins " + onlineUser.getUsername(), user);
                    }
                }
            } else if (split[0].equals("logout")) {
                if (split.length != 2) {
                    throw new IllegalStateException(
                            "Invalid logout message received from client by server");
                }

                String username = split[1];

                if (!this.clients.containsKey(new User(username))) {
                    throw new IllegalStateException(
                            "Invalid logout message received from client by server: invalid username");
                }

                this.clients.remove(new User(username));

                // leave all conversations
                for (int chatID : this.conversations.keySet()) {
                    Conversation chat = this.conversations.get(chatID);
                    if (chat.getUsers().contains(new User(username))) {
                        chat.removeUser(new User(username));
                        // send a leave message
                        this.sendMessageToUsers("chat_leave " + chatID + " "
                                + username, chat.getUsers());
                    }
                }

                // notify all clients that a new user has logged in
                this.sendMessageToUsers("user_leaves " + username,
                        this.clients.keySet());
            } else if (split[0].equals("chat_start")) {
                if (split.length == 1) {
                    throw new IllegalStateException(
                            "Invalid start message received from client by server");
                }

                Set<User> users = new HashSet<User>();
                for (int i = 1; i < split.length; i++) {
                    String username = split[i];
                    users.add(new User(username));
                }

                for (User user : users) {
                    if (!this.clients.containsKey(user)) {
                        throw new IllegalStateException(
                                "Invalid start message received from client by server: unknown user");
                    }
                }

                Conversation chat = new Conversation(users,
                        this.nextConversationID());
                conversations.put(chat.getID(), chat);

                for (User user : users) {
                    // first send a message to the user that is joining
                    Set<User> targetUserSet = new HashSet<User>();
                    targetUserSet.add(user);
                    this.sendMessageToUsers("chat_join " + chat.getID() + " "
                            + user.getUsername(), targetUserSet);

                    // then notify the user that all the other users are joining
                    for (User otherUser : users) {
                        if (!otherUser.equals(user)) {
                            this.sendMessageToUsers("chat_join " + chat.getID()
                                    + " " + otherUser.getUsername(),
                                    targetUserSet);
                        }
                    }
                }
            } else if (split[0].equals("chat_say")) {
                if (split.length < 4) {
                    throw new IllegalStateException(
                            "Invalid say message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                if (!this.clients.containsKey(new User(username))) {
                    throw new IllegalStateException(
                            "Invalid start message received from client by server: unknown user");
                }

                String text = "";
                for (int i = 3; i < split.length; i++) {
                    if (i > 3) {
                        text += " ";
                    }
                    text += split[i];
                }

                Conversation chat = this.conversations.get(ID);
                this.sendMessageToUsers("chat_say " + ID + " " + username + " "
                        + text, chat.getUsers());
            } else if (split[0].equals("chat_join")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid join message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                if (!this.clients.containsKey(new User(username))) {
                    throw new IllegalStateException(
                            "Invalid start message received from client by server: unknown user");
                }

                Conversation chat = this.conversations.get(ID);
                chat.addUser(new User(username));

                this.sendMessageToUsers("chat_join " + ID + " " + username,
                        chat.getUsers());

                // notify the new user of all the users that were already in the
                // conversation
                ArrayList<User> targetUser = new ArrayList<User>();
                targetUser.add(new User(username));
                for (User user : chat.getUsers()) {
                    if (!user.equals(new User(username))) {
                        this.sendMessageToUsers(
                                "chat_join " + ID + " " + user.getUsername(),
                                targetUser);
                    }
                }
            } else if (split[0].equals("chat_invite")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid join message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                if (!this.clients.containsKey(new User(username))) {
                    throw new IllegalStateException(
                            "Invalid start message received from client by server: unknown user");
                }

                Conversation chat = this.conversations.get(ID);
                chat.addUser(new User(username));

                this.sendMessageToUsers("chat_join " + ID + " " + username,
                        chat.getUsers());

                // notify the new user of all the users that were already in the
                // conversation
                ArrayList<User> targetUser = new ArrayList<User>();
                targetUser.add(new User(username));
                for (User user : chat.getUsers()) {
                    if (!user.equals(new User(username))) {
                        this.sendMessageToUsers(
                                "chat_join " + ID + " " + user.getUsername(),
                                targetUser);
                    }
                }
            } else if (split[0].equals("chat_leave")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid leave message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                if (!this.clients.containsKey(new User(username))) {
                    throw new IllegalStateException(
                            "Invalid start message received from client by server: unknown user");
                }

                Conversation chat = this.conversations.get(ID);
                chat.removeUser(new User(username)); // CHANGED
                // chat.deactivateUser(new User(username));

                if (chat.getUsers().size() == 0) {
                    // remove conversation
                    this.conversations.remove(ID);
                }

                this.sendMessageToUsers("leave " + ID + " " + username,
                        chat.getUsers());
            } else {
                throw new IllegalStateException(
                        "Unexpected command received from client by server: "
                                + message);
            }
        }
    }

    public void addMessageToQueue(String message, Socket socket) {
        try {
            this.queue.put(new CommunicationsData(message, socket));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected InterruptedException in addMessageToQueue()");
        }
    }
}
