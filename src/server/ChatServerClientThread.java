package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import user.User;

public class ChatServerClientThread implements Runnable {
    private final Socket socket;
    private final ChatServer server;

    public ChatServerClientThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        String username = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);

            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                System.out.println("received message: " + line);
                
                if (line.startsWith("login")) {
                    String[] split = line.split(" ");

                    if (split.length != 2) {
                        throw new IllegalStateException(
                                "Invalid login message from client received by server");
                    }

                    username = split[1];

                    if (this.server.tryAddingUser(username, socket)) {
                        // username is valid
                        this.server.sendInformationToNewUser(new User(username));
                        this.server.addMessageToQueue(line);
                    } else {
                        // username is invalid
                        out.println("logout " + username);
                    }
                } else {
                    this.server.addMessageToQueue(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("CLOSING CHAT SERVER CLIENT THREAD");
            
            // log out
            if (username != null) {
                this.server.addMessageToQueue("logout " + username);
            }

            try {
                out.close();
                in.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
