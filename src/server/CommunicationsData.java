package server;

import java.net.Socket;

public class CommunicationsData {
    private final String message;
    private final Socket socket;
    
    public CommunicationsData(String message, Socket socket) {
        this.message = message;
        this.socket = socket;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Socket getSocket() {
        return this.socket;
    }
}
