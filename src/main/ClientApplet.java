package main;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import client.ConnectionInfoBox;

/**
 * Starts a ChatClient from an applet.
 */
public class ClientApplet extends JApplet {

    /**
     * Starts a ChatClient from an applet.
     */
    private static final long serialVersionUID = 1L;

    public void init() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ConnectionInfoBox();
            }
        });
    }
}