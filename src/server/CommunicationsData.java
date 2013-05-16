package server;

import java.net.Socket;

/**
 * A data type used to hold data in the ChatServer's blocking queue. All
 * messages received from clients are placed in this blocking queue in the form
 * of a CommunicationsData, which stores the message and the socket the data is
 * received from.
 * 
 * This data structure is immutable.
 */
public class CommunicationsData {
    private final String message; // The message associated with the
                                  // communication.
    private final Socket socket; // The socket the message was received from.

    /**
     * Initializes a new CommunicationsData with the given message and socket.
     * 
     * @param message
     *            The String message received from the client.
     * @param socket
     *            The Socket the message was received from.
     */
    public CommunicationsData(String message, Socket socket) {
        this.message = message;
        this.socket = socket;
    }

    // ACCESSORS
    public String getMessage() {
        return this.message;
    }

    public Socket getSocket() {
        return this.socket;
    }
}
