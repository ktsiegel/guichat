package client;

import java.util.HashSet;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
    private Set<User> others;

    public ChatBox(ChatClientModel chatClientModel, int conversationID,
            String title, String other) {
        this.model = new ChatBoxModel(chatClientModel, this, conversationID);
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
        others.add(new User(other));

        this.setTitle(title);

        createGroupLayout();
    }

    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(layout
                .createParallelGroup()
                .addGroup(
                        layout.createSequentialGroup().addComponent(
                                displayScroll, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(messageScroll, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE).addComponent(
                                displayScroll, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(messageScroll, 60, 60, 60));
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
    
    @Override
    public void dispose() {
    	this.model.quitChatBox();
    	super.dispose();
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
