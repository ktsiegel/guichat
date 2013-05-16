package main;

import javax.swing.SwingUtilities;

import client.ConnectionInfoBox;

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
                new ConnectionInfoBox();
            }
        });
    }
}
