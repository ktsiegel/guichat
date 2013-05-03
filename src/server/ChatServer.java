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
import java.util.concurrent.LinkedBlockingQueue;

import conversation.Conversation;

import user.User;

public class ChatServer {
    private final ServerSocket serverSocket;
    private final Map<User, Socket> clients;
    private final Map<Integer, Conversation> conversations;
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
        clients = new HashMap<User, Socket>();
        conversations = new HashMap<Integer, Conversation>();
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
    
    public void sendInformationToNewUser(User user) {
        List<User> list = new ArrayList<User>();
        list.add(user);
        for (User oldUser : clients.keySet()) {
            this.sendMessageToClients("login " + oldUser.getUsername(), list);
        }
    }

    private void sendMessageToClients(String message, List<User> targets) {
        for (User user : targets) {
            if (this.clients.containsKey(user)) {
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
                this.sendMessageToClients("login " + username,
                        new ArrayList<User>(this.clients.keySet()));
            }

            else if (split[0].equals("logout")) {
                if (split.length != 2) {
                    throw new IllegalStateException(
                            "Invalid logout message received from client by server");
                }
                String username = split[1];

                // notify all clients that a new user has logged in
                this.sendMessageToClients("logout " + username,
                        new ArrayList<User>(this.clients.keySet()));
            }

            else if (split[0].equals("start")) {
                if (split.length == 1) {
                    throw new IllegalStateException(
                            "Invalid login message received from client by server");
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

                Conversation chat = new Conversation(users,
                        this.nextConversationID());
                conversations.put(chat.getID(), chat);

                for (User user : users) {
                    this.sendMessageToClients("join " + chat.getID() + " "
                            + user.getUsername(), new ArrayList<User>(users));
                }
            }

            else if (split[0].equals("say")) {
                if (split.length < 5) {
                    throw new IllegalStateException(
                            "Invalid login message received from client by server");
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
                        + timestamp + " " + text,
                        new ArrayList<User>(chat.getUsers()));
            }

            else if (split[0].equals("join")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid login message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                Conversation chat = this.conversations.get(ID);
                this.sendMessageToClients("join " + ID + " " + username,
                        new ArrayList<User>(chat.getUsers()));
            }

            else if (split[0].equals("leave")) {
                if (split.length != 3) {
                    throw new IllegalStateException(
                            "Invalid login message received from client by server");
                }
                int ID = Integer.parseInt(split[1]);
                String username = split[2];

                Conversation chat = this.conversations.get(ID);
                this.sendMessageToClients("leave " + ID + " " + username,
                        new ArrayList<User>(chat.getUsers()));
            }

            else {
                throw new IllegalStateException(
                        "Unexpected command received from client by server");
            }
        }
    }

    public boolean tryAddingUser(String username, Socket socket) {
        User user = new User(username);
        if (this.clients.containsKey(user)) {
            return false;
        } else {
            this.clients.put(user, socket);
            return true;
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
