package client;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Group;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import conversation.ChatHistory;

/**
 * A HistoryBox is a GUI that displays the chat history of a past group chat.
 */

public class HistoryBox extends JFrame {
    private static final long serialVersionUID = 1L; // default serial ID

    private JPanel background; // background of the history box
    private JScrollPane displayScroll; // scroll bar for the history box

    /**
     * Creates a HistoryBox with the given ChatHistory.
     * 
     * @param history
     *            the ChatHistory to associate with the HistoryBox.
     */
    public HistoryBox(ChatHistory history) {
        this.setSize(300, 300);

        // Borders used in the HistoryBox GUI
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        // The component that displays the text of the history
        JTextArea display = new JTextArea();
        display.setEditable(false);
        display.setLineWrap(true);
        display.append(history.getHistory());
        display.setBackground(Color.white);

        // Make the display of the history scrollable.
        displayScroll = new JScrollPane(display);
        displayScroll.setBorder(lineBorder);

        // The background panel that will contain the text field that contains
        // the chat history.
        background = new JPanel();
        background.setBackground(new Color(0, 51, 102));
        background.setBorder(paddingBorder);
        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        createGroupLayout();
    }

    /**
     * Create the layout of the HistoryBox.
     */
    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup(); // horizontal group
        h.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup(); // vertical group
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
}
