package client;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

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

import user.User;

public class ChatBox extends JFrame {

    /**
     * Auto-generated default serial ID.
     */
    private static final long serialVersionUID = 1L;

    private final JTextArea display;
    private final JTextArea message;
    private final JScrollPane displayScroll;
    private final JScrollPane messageScroll;
    private final ChatBoxModel model;
    private final JPanel background;
    private final JPanel gap;
    private Set<User> others;

    public ChatBox(ChatClientModel chatClientModel, int conversationID,
            String title) {
        this.model = new ChatBoxModel(chatClientModel, this, conversationID);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("dispose was called");
                model.quitChatBox();
                dispose();
            }
        });
        this.setSize(300, 300);

        display = new JTextArea();
        display.setEditable(false);
        display.setLineWrap(true);
        displayScroll = new JScrollPane(display);

        message = new JTextArea();
        message.setLineWrap(true);
        messageScroll = new JScrollPane(message);
        message.addKeyListener(model);

        others = new HashSet<User>();

        background = new JPanel();
        gap = new JPanel();
        gap.setBackground(new Color(0, 51, 102));

        background.setBackground(new Color(0, 51, 102));

        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border textBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);

        display.setBorder(textBorder);
        displayScroll.setBorder(lineBorder);
        message.setBorder(textBorder);
        messageScroll.setBorder(lineBorder);
        background.setBorder(paddingBorder);

        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        createGroupLayout();
    }

    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(gap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE);
        h.addComponent(messageScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(gap, 10, 10, 10);
        v.addComponent(messageScroll, 60, 60, 60);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }

    public String sendMessage() {
        String currentMessage = message.getText().trim();
        message.setText("");
        return currentMessage;
    }

    public void appendChatLine(String username, String message) {
        display.append(username + ": " + message + "\n");
    }

    public void appendMessage(String message) {
        display.append(message + "\n");
    }

    // accessors
    public ChatBoxModel getModel() {
        return model;
    }

    public JTextArea getMessage() {
        return this.message;
    }

    public JTextArea getDisplay() {
        return this.display;
    }

    public Set<User> getOthers() {
        return others;
    }

}
