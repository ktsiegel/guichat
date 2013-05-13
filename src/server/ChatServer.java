package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import conversation.Conversation;

import user.User;

public class ChatServer {
    private final ServerSocket serverSocket;
    private final ConcurrentMap<User, Socket> clients;
    private final ConcurrentMap<Integer, Conversation> conversations;
    private final BlockingQueue<String> queue;

    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected IOException when creating server socket with port "
                            + port);
        }
        clients = new ConcurrentHashMap<User, Socket>();
        conversations = new ConcurrentHashMap<Integer, Conversation>();
        queue = new LinkedBlockingQueue<String>();
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

    public boolean tryAddingUser(String username, Socket socket) {
        synchronized (this.clients) {
            User user = new User(username);
            PrintWriter out;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(
                        "Unexpected IOException in tryAddingUser()");
            }
            if (this.clients.containsKey(user)) {
                out.println("invalid");
                return false;
            } else {
                out.println("success");
                out.println("login " + username);

                // update the new user with old information
                for (User oldUser : this.clients.keySet()) {
                    out.println("login " + oldUser.getUsername());
                }

                // this is done last to prevent race conditions with
                // sendMessageToClients
                this.clients.put(user, socket);
                this.addMessageToQueue("login " + username);
                return true;
            }
        }
    }

    private void sendMessageToClients(String message, Iterable<User> targets) {
        for (User user : targets) {
            if (this.clients.containsKey(user)) {
                System.out.println("sending \"" + message + "\" to "
                        + user.getUsername());

                try {
                    PrintWriter out = new PrintWriter(this.clients.get(user)
                            .getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(
                            "Unexpected IOException when sending message to client");
                }
            } else {
                throw new IllegalArgumentException(
                        "One of the Users to send a message to does not exist");
            }
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

    private void work() {
        while (true) {
            String message;
            try {
                message = this.queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(
                        "Unexpected InterruptedException in work()");
            }

            String[] split = message.split(" ");

            if (split[0].equals("login")) {
                if (split.length != 2) {
                    throw new IllegalStateException(
                            "Invalid login message received from client by server");
                }
                String username = split[1];

                // notify all clients that a new user has logged in
                Set<User> allUsers = new HashSet<User>(this.clients.keySet());
                allUsers.remove(new User(username));
                this.sendMessageToClients("login " + username, allUsers);
            }

            else if (split[0].equals("logout")) {
                if (split.length != 2) {
                    throw new IllegalStateException(
                            "Invalid logout message received from client by server");
                }
                String username = split[1];

                synchronized (this.clients) {
                    this.clients.remove(new User(username));
                }

                synchronized (this.conversations) {
                    // leave all conversations
                    for (int chatID : this.conversations.keySet()) {
                        Conversation chat = this.conversations.get(chatID);
                        if (chat.getUsers().contains(new User(username))) {
                            chat.removeUser(new User(username));
                            // send a leave message
                            this.sendMessageToClients("leave " + chatID + " "
                                    + username, chat.getUsers());
                        }
                    }
                }

                // notify all clients that a new user has logged in
                this.sendMessageToClients("logout " + username,
                        this.clients.keySet());
            }

            else if (split[0].equals("start")) {
                if (split.length == 1) {
                    throw new IllegalStateException(
                            "Invalid start message received from client by server");
                }

                Set<User> users = new HashSet<User>();
                for (int i = 1; i < split.length; i++) {
                    users.add(new User(split[i]));
                }

                for (User user : users) {
                    if (!this.clients.containsKey(user)) {
                        throw new IllegalStateException(
                                "Invalid start message received from client by server: unknown user");
                    }
                }

                Conversation chat;
                synchronized (this.conversations) {
                    chat = new Conversation(users, this.nextConversationID());
                    conversations.put(chat.getID(), chat);
                }

                for (User user : users) {
                    // first send a message to the user that is joining
                    Set<User> targetUserSet = new HashSet<User>();
                    targetUserSet.add(user);
                    this.sendMessageToClients("join " + chat.getID() + " "
                            + user.getUsername(), targetUserSet);

                    // then notify the user that all the other users are joining
                    for (User otherUser : users) {
                        if (!otherUser.equals(user)) {
                            this.sendMessageToClients("join " + chat.getID()
                                    + " " + otherUser.getUsername(), targetUserSet);
                        }
                    }
                }
            }

            else if (split[0].equals("say")) {
                if (split.length < 4) {
                    throw new IllegalStateException(
                            "Invalid say message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];
                String timestamp = split[3];
                String text = "";
                for (int i = 4; i < split.length; i++) {
                    if (i > 4) {
                        text += " ";
                    }
                    text += split[i];
                }

                Conversation chat = this.conversations.get(ID);
                this.sendMessageToClients("say " + ID + " " + username + " "
                        + timestamp + " " + text, chat.getUsers());
            }

            else if (split[0].equals("join")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid join message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                Conversation chat;
                synchronized (this.conversations) {
                    chat = this.conversations.get(ID);
                    chat.addUser(new User(username));
                }

                this.sendMessageToClients("join " + ID + " " + username,
                        chat.getUsers());

                // notify the new user of all the users that were already in the
                // conversation
                ArrayList<User> targetUser = new ArrayList<User>();
                targetUser.add(new User(username));
                for (User user : chat.getUsers()) {
                    if (!user.equals(new User(username))) {
                        this.sendMessageToClients(
                                "join " + ID + " " + user.getUsername(),
                                targetUser);
                    }
                }
            }

            else if (split[0].equals("leave")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid leave message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                Conversation chat;
                synchronized (this.conversations) {
                    chat = this.conversations.get(ID);
                    chat.removeUser(new User(username)); //CHANGED
                	//chat.deactivateUser(new User(username)); 

                    if (chat.getUsers().size() == 0) {
                        // remove conversation
                        this.conversations.remove(ID);
                    }
                }

                this.sendMessageToClients("leave " + ID + " " + username,
                        chat.getUsers());
            }

            else {
                throw new IllegalStateException(
                        "Unexpected command received from client by server: " + message);
            }
        }
    }

    public void addMessageToQueue(String message) {
        try {
            this.queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected InterruptedException in addMessageToQueue()");
        }
    }
}
