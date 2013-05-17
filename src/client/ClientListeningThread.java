package client;

import java.io.IOException;

/**
 * A ClientListeningThread listens for communication sent by the server to the
 * client on a separate thread than the main thread of the ChatClientModel.
 */

public class ClientListeningThread extends Thread {
    private ChatClientModel model; // the model associated with this listening
                                   // thread

    /**
     * Creates a listening thread for a given model.
     * 
     * @param model
     *            the model associated with this listening thread.
     */
    public ClientListeningThread(ChatClientModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        try {
            model.listenForResponse(); // The method in the ChatClientModel that
                                       // registers messages from the server.
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
