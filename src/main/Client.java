package main;

import javax.swing.SwingUtilities;

import user.User;
import client.ChatClient;

/**
 * GUI chat client runner.
 */
public class Client {
    /**
     * Start a GUI chat client.
     */
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ChatClient main = new ChatClient();
            }
        });
    }
}
