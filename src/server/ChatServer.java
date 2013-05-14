package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

                    // have the user rejoin all private conversations
                    for (int ID : this.conversations.keySet()) {
                        Conversation conversation = this.conversations.get(ID);
                        if (!conversation.isGroupChat()) {
                            Iterator<User> iterator = conversation.getUsers()
                                    .iterator();
                            User a = iterator.next();
                            User b = iterator.next();
                            if (!a.equals(user)) {
                                this.sendMessageToUser(
                                        "chat_start " + conversation.getID()
                                                + " " + a.getUsername() + " "
                                                + b.getUsername(), user);
                            } else {
                                this.sendMessageToUser(
                                        "chat_start " + conversation.getID()
                                                + " " + a.getUsername() + " "
                                                + b.getUsername(), user);
                            }
                        }
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
                    if (chat.getUsers().contains(new User(username))
                            && chat.isGroupChat()) {
                        chat.removeUser(new User(username));
                        // send a leave message
                        this.sendMessageToUsers("group_chat_leave " + chatID
                                + " " + username, chat.getUsers());
                    }
                }

                // notify all clients that a new user has logged in
                this.sendMessageToUsers("user_leaves " + username,
                        this.clients.keySet());
            } else if (split[0].equals("chat_start")) {
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

                this.sendMessageToUser("chat_start " + chat.getID() + " "
                        + username1 + " " + username2, user1);
                this.sendMessageToUser("chat_start " + chat.getID() + " "
                        + username1 + " " + username2, user2);
            } else if (split[0].equals("group_chat_start")) {
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

                Conversation chat = new Conversation(users,
                        this.nextConversationID());
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
                            this.sendMessageToUsers(
                                    "group_chat_join " + chat.getID() + " "
                                            + otherUser.getUsername(),
                                    targetUserSet);
                        }
                    }
                }
            } else if (split[0].equals("group_chat_invite")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid group_chat_invite message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                if (this.clients.containsKey(new User(username))) {
                    // only proceed if the username is valid

                    Conversation chat = this.conversations.get(ID);
                    chat.addUser(new User(username));

                    this.sendMessageToUsers("group_chat_join " + ID + " "
                            + username, chat.getUsers());

                    // notify the new user of all the users that were already in
                    // the
                    // conversation
                    ArrayList<User> targetUser = new ArrayList<User>();
                    targetUser.add(new User(username));
                    for (User user : chat.getUsers()) {
                        if (!user.equals(new User(username))) {
                            this.sendMessageToUsers("group_chat_join " + ID
                                    + " " + user.getUsername(), targetUser);
                        }
                    }
                }
            } else if (split[0].equals("group_chat_leave")) {
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

                this.sendMessageToUsers("group_chat_leave " + ID + " "
                        + username, chat.getUsers());
            } else if (split[0].equals("say")) {
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
                    text = text.substring(text.indexOf(" ") + 1);
                }

                Conversation chat = this.conversations.get(ID);
                this.sendMessageToUsers("say " + ID + " " + username + " "
                        + text, chat.getUsers());
            } else if (split[0].equals("typing")) {
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
                Set<User> chatUsers = new HashSet<User>(chat.getUsers());
                chatUsers.remove(new User(username));
                this.sendMessageToUsers("typing " + ID + " " + username,
                        chatUsers);
            } else if (split[0].equals("cleared")) {
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
                Set<User> chatUsers = new HashSet<User>(chat.getUsers());
                chatUsers.remove(new User(username));
                this.sendMessageToUsers("cleared " + ID + " " + username,
                        chatUsers);
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

    public void forceLogout(Socket socket) {
        for (User user : this.clients.keySet()) {
            if (this.clients.get(user).equals(socket)) {
                this.addMessageToQueue("logout " + user.getUsername(), socket);
            }
        }
    }
}
