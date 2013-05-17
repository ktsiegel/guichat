package server.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A dummy client class used for the testing of a ChatServer. All it does is set
 * up a socket to send and read messages from the server.
 */
public class DummyClient {
    private final Socket socket; // The socket used
    private final BlockingQueue<String> queue; // Stores messages read

    /**
     * Creates a client that connects to a server with the given hostname and
     * port.
     * 
     * @param hostname
     *            The hostname to connect to.
     * @param port
     *            The port to connect to.
     */
    public DummyClient(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("DummyClient() failed");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("DummyClient() failed");
        }
        queue = new LinkedBlockingQueue<String>();

        // start waiting for messages
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    for (String next = in.readLine(); next != null; next = in
                            .readLine()) {
                        try {
                            queue.put(next);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            throw new RuntimeException("queue.put() failed");
                        }
                    }
                } catch (IOException e) {
                    // ignore server close or socket close
                }
            }
        });
        thread.start();
    }

    /**
     * Sends the message to the server.
     * 
     * @param message
     *            the message to send to the server.
     */
    public void send(String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("sendMessageToServer() failed");
        }
    }

    /**
     * Returns a message read from the server. Requires that the socket is ready
     * to be read from.
     * 
     * @return a message read from the server.
     */
    public String read() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("read() failed");
        }
    }

    /**
     * Shuts down the connection.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("close() failed");
        }
    }
}
