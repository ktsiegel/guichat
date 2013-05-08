package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import conversation.Conversation;

public class ChatBoxModel implements KeyListener {
    
    private ChatBox chatBox;
    private int conversationID;
    private ChatClientModel model;

    public ChatBoxModel(ChatClientModel model, ChatBox chatBox, int conversationID) {
        this.model = model;
        this.chatBox = chatBox;
        this.conversationID = conversationID;
    }
    
    public void addChatLine(String text) {
    	model.sendChat(conversationID, System.currentTimeMillis(), text);
    }
    
    /**
     * Adds a message from a user at a given time to the display.
     * @param username The user from which the message is sent.
     * @param time The time at which this message was sent.
     * @param message The text of the message that was sent.
     */
    public void addChatToDisplay(String username, String time, String message) {
    	chatBox.appendChatLine(username, time, message);
    }
    
    public void addMessageToDisplay(String message) {
    	chatBox.appendMessage(message);
    }
    
    public void quit() {
    	chatBox.setVisible(false);
    }

	@Override
    public void keyPressed(KeyEvent e) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = chatBox.sendMessage();
            if (!message.equals("")) {
            	addChatLine(chatBox.sendMessage());
            }
        }
    }

	@Override
    public void keyTyped(KeyEvent e) {
    }
	
	public ChatBox getChatBox() {
		return chatBox;
	}
}
