package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Timer;

public class ChatBoxModel implements KeyListener {

    private final ChatBox chatBox;
    private final int conversationID;
    private final ChatClientModel model;
    private final boolean isGroupChat;
    private final Map<String, Integer> usersTyping;
    private final Set<String> usersEnteredText;

    public ChatBoxModel(ChatClientModel model, ChatBox chatBox,
            int conversationID, boolean isGroupChat) {
        this.model = model;
        this.chatBox = chatBox;
        this.conversationID = conversationID;
        this.isGroupChat = isGroupChat;
        this.usersTyping = new HashMap<String, Integer>();
        this.usersEnteredText = new HashSet<String>();
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
        if (this.usersTyping.containsKey(username)) {
            this.usersTyping.remove(username);
        }
        if (this.usersEnteredText.contains(username)) {
            this.usersEnteredText.remove(username);
        }
        chatBox.updateStatus(this.usersTyping.keySet(), this.usersEnteredText);
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

    public void markTyping(final String username) {
        if (this.usersEnteredText.contains(username)) {
            this.usersEnteredText.remove(username);
        }
        int num = 0;
        if (this.usersTyping.containsKey(username)) {
            num = this.usersTyping.get(username);
            num++;
        }
        this.usersTyping.put(username, num);
        chatBox.updateStatus(this.usersTyping.keySet(), this.usersEnteredText);

        final int originalNum = num;
        ActionListener enteredTextMarker = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (usersTyping.containsKey(username)
                        && usersTyping.get(username) == originalNum) {
                    usersTyping.remove(username);
                    usersEnteredText.add(username);

                    chatBox.updateStatus(usersTyping.keySet(), usersEnteredText);
                }
            }
        };
        Timer timer = new Timer(1000, enteredTextMarker);
        timer.setRepeats(false);
        timer.start();
    }

    public void markCleared(String username) {
        if (this.usersEnteredText.contains(username)) {
            this.usersEnteredText.remove(username);
        }
        if (this.usersTyping.containsKey(username)) {
            this.usersTyping.remove(username);
        }
        chatBox.updateStatus(this.usersTyping.keySet(), this.usersEnteredText);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() != KeyEvent.VK_ENTER) {
            if (chatBox.getMessage().getText().equals("")) {
                this.model.sendCleared(this.conversationID);
            } else {
                this.model.sendTyping(this.conversationID);
            }
        }
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
