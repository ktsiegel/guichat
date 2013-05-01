package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class UserListener implements MouseListener {

    JLabel userLabel;
    
    public UserListener(JLabel userLabel) {
        this.userLabel = userLabel;
        userLabel.addMouseListener(this);
       
    }
    
    @Override
    public void mouseClicked(MouseEvent arg0) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ChatBox main = new ChatBox();

                main.setVisible(true);
            }
        });
        
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
