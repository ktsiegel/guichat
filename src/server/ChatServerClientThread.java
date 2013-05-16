package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A class to act as a thread to read messages from a single client Socket.
 * 
 * In order for this class to act properly, this must be the only class reading
 * from the particular socket.
 */
public class ChatServerClientThread implements Runnable {
    private final Socket socket; // The socket that the data is read from.
    private final ChatServer server; // The ChatServer that is communicating
                                     // with the client socket.

    /**
     * Creates a new ChatServerClientThread based on a given client Socket and a
     * ChatServer. The Socket must be connected to the ChatServer.
     * 
     * @param socket
     *            The client Socket from which messages will be read.
     * @param server
     *            The ChatServer that processes the data from the client and
     *            sends data back.
     */
    public ChatServerClientThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        // Read messages from the client socket until there are no more
        // messages, adding each message to the server's blocking queue for
        // further processing.

        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);

            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                this.server.addMessageToQueue(line, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Log the user out if the user did not log out on his or her own.
            this.server.forceLogout(socket);

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
