package server.testing;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import server.ChatServer;
import server.CommunicationsData;

/**
 * Creates a ChatServer that does not send messages back to clients. Instead, it
 * just allows us to view messages that the ChatServer receives from clients
 * (the messages that reach the blocking queue).
 * 
 * This is solely used for testing the ChatServerClientThread, so only the
 * methods of the ChatServer that the ChatServerClientThread uses are overriden
 * here.
 */
public class DummyChatServer extends ChatServer {

    private BlockingQueue<CommunicationsData> dummyQueue; // a dummy queue
                                                          // similar to the
                                                          // ChatServer's queue
    private Set<Socket> loggedOutSockets; // a list of all sockets that are
                                          // logged out

    /**
     * Starts a new dummy chat server with the given port.
     * 
     * @param port
     *            The port to start up a server on.
     */
    public DummyChatServer(int port) {
        super(port);
        dummyQueue = new LinkedBlockingQueue<CommunicationsData>();
        loggedOutSockets = new HashSet<Socket>();
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
    @Override
    public void addMessageToQueue(String message, Socket socket) {
        try {
            this.dummyQueue.put(new CommunicationsData(message, socket));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected InterruptedException in addMessageToQueue()");
        }
    }

    /**
     * Adds the socket to the list of sockets that are logged out.
     * 
     * @param socket
     *            The socket whose connection has ended.
     */
    @Override
    public void forceLogout(Socket socket) {
        this.loggedOutSockets.add(socket);
    }

    public CommunicationsData nextData() {
        try {
            return this.dummyQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unexpected InterruptedException in nextData()");
        }
    }

    /**
     * Returns whether the given socket was forced to log out at some point.
     * 
     * @param socket
     *            Checks whether this socket was ever forcefully logged out.
     * @return True if the given socket was ever forcefully logged out, and
     *         false otherwise.
     */
    public boolean isLoggedOutSocket(Socket socket) {
        return this.loggedOutSockets.contains(socket);
    }
}
