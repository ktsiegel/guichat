package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import user.User;

public class GroupChatListener implements MouseListener {

    private final JLabel userLabel;
    private final GroupChatSelectBox box;
    private final User friend;
    private boolean selected;
    
    public GroupChatListener(JLabel userLabel, GroupChatSelectBox box, User friend) {
        this.userLabel = userLabel;
        this.box = box;
        this.friend = friend;
        userLabel.addMouseListener(this);
        selected = false;
    }
    
    /**
     * Called when a name is clicked in the friends list.
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (!box.containsSelected(this.userLabel)) {
        	userLabel.setForeground(new Color(102, 178, 255));
        	box.addSelected(this.userLabel);
        	selected = true;
        }
        else {
        	userLabel.setForeground(new Color(0,0,0));
        	box.removeSelected(this.userLabel);
        	selected = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (!selected) {
        	userLabel.setForeground(new Color(102, 178, 255));
        }
    }

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
