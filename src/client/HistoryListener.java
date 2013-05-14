package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class HistoryListener implements MouseListener {

    private final JLabel historyLabel;
    private final ChatClientModel model;
    private final int ID;
    
    public HistoryListener(JLabel historyLabel, ChatClientModel model, int ID) {
        this.historyLabel = historyLabel;
        this.model = model;
        this.ID = ID;
        historyLabel.addMouseListener(this);
    }
    
    /**
     * Called when a name is clicked in the friends list.
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        model.showChatHistory(ID);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        historyLabel.setForeground(new Color(102, 178, 255));
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        historyLabel.setForeground(Color.black);
        
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
