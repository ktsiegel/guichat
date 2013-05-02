package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import user.User;

public class UserListener implements MouseListener {

    JLabel userLabel;
    ChatClientModel model;
    User friend;
    
    public UserListener(JLabel userLabel, ChatClientModel model, User friend) {
        this.userLabel = userLabel;
        this.model = model;
        this.friend = friend;
        userLabel.addMouseListener(this);
       
    }
    
    @Override
    public void mouseClicked(MouseEvent arg0) {
        model.addChat(friend);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        userLabel.setForeground(Color.blue);
        
    }

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
