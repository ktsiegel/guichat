package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JLabel;

/**
 * An AvatarListener listens for mouse input for an avatar image on the welcome screen of
 * the chat GUI, and changes the background of the avatar image accordingly.
 */

public class AvatarListener implements MouseListener {
    List<JLabel> avatarLabels;
    JLabel avatarLabel;
    Color DARK_BLUE = new Color(0, 51, 102);
    Color LIGHT_BLUE = new Color(102, 178, 255);
    Color MEDIUM_BLUE = new Color(0, 102, 204);

    public AvatarListener(List<JLabel> avatarLabels, JLabel avatarLabel) {
        this.avatarLabels = avatarLabels;
        this.avatarLabel = avatarLabel;
    }
    
    /**
     * Registers when the user clicks the avatar image in the chat client GUI that this
     * AvatarListener is listening to, and adjusts the avatar highlighting accordingly.
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        for (JLabel label: avatarLabels) {
            label.setBackground(DARK_BLUE); //erase the highlights from any selected avatar
        }
        avatarLabel.setBackground(Color.white); //highlight this avatar with a white background
        
    }

    /**
     * Registers when the user mouses over the avatar image in the chat client GUI that this
     * AvatarListener is listening to, and adjusts the avatar highlighting accordingly.
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (avatarLabel.getBackground() != Color.white) {
            avatarLabel.setBackground(LIGHT_BLUE); //highlight with light blue when moused over
        }
        
    }

    /**
     * Registers when the user finishes mousing over the avatar image in the chat client GUI
     * that this AvatarListener is listening to, and adjusts the avatar highlighting accordingly.
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        if (avatarLabel.getBackground() != Color.white) {
            avatarLabel.setBackground(DARK_BLUE); //remove highlights
        }
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
