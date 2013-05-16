package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

/**
 * A GroupChatListener detects mouse inputs from the user on JLabels
 * that represent users with which the user can have a group chat.
 * This listener then modifies the GUI accordingly and/or alerts the model 
 * that these mouse inputs occurred.
 */

public class GroupChatListener implements MouseListener {

    private final JLabel userLabel;
    private final GroupChatSelectBox box;
    private boolean selected;
    
    Color DARK_BLUE = new Color(0, 51, 102);
    Color LIGHT_BLUE = new Color(102, 178, 255);
    
    public GroupChatListener(JLabel userLabel, GroupChatSelectBox box) {
        this.userLabel = userLabel;
        this.box = box;
        userLabel.addMouseListener(this);
        selected = false;
    }
    
    /**
     * Called when the label that this GroupChatListener registers events for 
     * is clicked in the friends list; selects the label
     * by changing its text color to light blue, if it is not already selected,
     * and deselects the label by changing its text color back to black, if it
     * is already selected.
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (!box.containsSelected(this.userLabel)) {
        	userLabel.setForeground(LIGHT_BLUE);
        	box.addSelected(this.userLabel);
        	selected = true;
        }
        else {
        	userLabel.setForeground(Color.black); //black
        	box.removeSelected(this.userLabel);
        	selected = false;
        }
    }

    /**
     * When the label that this GroupChatListener registers events for is moused
     * over, change its text color to light blue if it is not selected.
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (!selected) {
        	userLabel.setForeground(LIGHT_BLUE); //light blue
        }
    }

    /**
     * When the label that this GroupChatListener registers events for is no longer
     * moused over, change its text color back to black if it is not selected.
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        if (!selected) {
        	userLabel.setForeground(Color.black);
        }
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
