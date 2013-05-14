package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatBoxModel implements KeyListener {

    private final ChatBox chatBox;
    private final int conversationID;
    private final ChatClientModel model;
    private final boolean isGroupChat;

    public ChatBoxModel(ChatClientModel model, ChatBox chatBox,
            int conversationID, boolean isGroupChat) {
        this.model = model;
        this.chatBox = chatBox;
        this.conversationID = conversationID;
        this.isGroupChat = isGroupChat;
    }

    public void addChatLine(String text) {
        model.sendChat(conversationID, text);
    }

    /**
     * Adds a message from a user at a given time to the display.
     * 
     * @param username
     *            The user from which the message is sent.
     * @param message
     *            The text of the message that was sent.
     */
    public void addChatToDisplay(String username, String message) {
        chatBox.appendChatLine(username, message);
    }

    public void addMessageToDisplay(String message) {
        chatBox.appendMessage(message);
    }
    
    public void show() {
        chatBox.setVisible(true);
    }

    public void quit() {
        chatBox.setVisible(false);
    }

    public void quitChatBox() {
        if (this.isGroupChat) {
            this.model.exitChat(conversationID);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = chatBox.sendMessage();
            if (!message.equals("")) {
                addChatLine(message);
            }
        }
    }

    public ChatBox getChatBox() {
        return chatBox;
    }
}
