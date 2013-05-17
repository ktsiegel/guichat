package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;

/**
 * A HistoryListener listens for mouse input from the user for a particular
 * JLabel representing a past group chat history in the main ChatClient window,
 * and communicates with the GUI and the ChatClientModel accordingly.
 */

public class HistoryListener implements MouseListener {

    private final JLabel historyLabel; // The Label to listen on
    private final ChatClientModel model; // The model associated with this whole
                                         // system
    private final int ID; // The ID of the conversation

    /**
     * Creates a HistoryListener corresponding to certain input data.
     * 
     * @param historyLabel
     *            The label to listen on.
     * @param model
     *            The model associated with the whole system.
     * @param ID
     *            The ID associated with this conversation.
     */
    public HistoryListener(JLabel historyLabel, ChatClientModel model, int ID) {
        this.historyLabel = historyLabel; // The label to which this
                                          // HistoryListener listens
        this.model = model;
        this.ID = ID; // The ID of the conversation corresponding to the chat
                      // history
                      // linked to by the label corresponding to this
                      // HistoryListener
        historyLabel.addMouseListener(this);
    }

    /**
     * Called when the label corresponding to this HistoryListener is clicked in
     * the chat history list. Tells the ChatClientModel to show the
     * corresponding chat history.
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        model.showChatHistory(ID);
    }

    /**
     * Called when the label corresponding to this HistoryListener is moused
     * over in the chat history list. Highlights the label by changing the text
     * to light blue.
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
        historyLabel.setForeground(new Color(102, 178, 255));
    }

    /**
     * Called when the label corresponding to this HistoryListener is no longer
     * moused over in the chat history list. Unhighlights the label by changing
     * the text back to black.
     */
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
