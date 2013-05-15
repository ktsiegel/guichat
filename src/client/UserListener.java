package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import user.User;

/**
 * A UserListener listens for mouse input from the user for a particular JLabel
 * representing an online friend in the main ChatClient window, and communicates
 * with the GUI and the ChatClientModel accordingly.
 */

public class UserListener implements MouseListener {

    private final JLabel userLabel; //The JLabel to which this UserListener listens
    private final ChatClientModel model;
    private final User friend; //The user represented by userLabel
    
    public UserListener(JLabel userLabel, ChatClientModel model, User friend) {
        this.userLabel = userLabel;
        this.model = model;
        this.friend = friend;
        userLabel.addMouseListener(this);
       
    }
    
    /**
     * Called when the name label is clicked in the friends list. 
     * Initiates a chat with that user.
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        model.addChat(friend);
    }

    /**
     * Called when the name label is moused over in the friends list.
     * Highlights this user by changing the text of the label to light blue.
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
        userLabel.setForeground(new Color(102, 178, 255)); //light blue
    }

    /**
     * Called when the name label is no longer moused over in the friends list.
     * Unhighlights this user by changing the text of the label to black.
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        userLabel.setForeground(Color.black);
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // Do nothing
    }

}
