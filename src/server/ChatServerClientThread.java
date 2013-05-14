package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        try {
            in = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);

            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                System.out.println("server received message: " + line);

                this.server.addMessageToQueue(line, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("CLOSING CHAT SERVER CLIENT THREAD");

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
