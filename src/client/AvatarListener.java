package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JLabel;

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
    
    @Override
    public void mouseClicked(MouseEvent arg0) {
        for (JLabel label: avatarLabels) {
            label.setBackground(DARK_BLUE);
        }
        avatarLabel.setBackground(Color.white);
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (avatarLabel.getBackground() != Color.white) {
            avatarLabel.setBackground(LIGHT_BLUE);
        }
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (avatarLabel.getBackground() != Color.white) {
            avatarLabel.setBackground(DARK_BLUE);
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
