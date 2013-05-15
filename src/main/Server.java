package main;

import java.io.IOException;

import server.ChatServer;

/**
 * Chat server runner.
 */
public class Server {

    /**
     * Start a chat server.
     * @throws IOException If the server malfunctions.
     */
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(4567);
        server.serve();
    }
}
