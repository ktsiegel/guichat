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

/**
 * A ChatBoxModel is the implementation of the model portion of the
 * model-view-controller design pattern of the chat box part of our Chat Client.
 * It contains the data needed for each chat box and to communicate
 * with the main chat client window.
 */

public class ChatBoxModel implements KeyListener {

    private final ChatBox chatBox; //The chat box GUI
    private final int conversationID; //The ID of the conversation being held in the
    								//chat box represented by this model
    private final ChatClientModel model; //The main chat client of the user
    									//to which this chat box belongs
    private final boolean isGroupChat; //Whether or not this chat box represents
    									//a group chat
    private final Map<String, Integer> usersTyping; //The users currently typing in
    												//chatboxes corresponding to the
    												//conversation happening in this chatbox
    												//Maps to an integer that will be used to
    												//indicate whether a user is currently typing
    private final Set<String> usersEnteredText; //The users that have entered but not sent text in
    											//chatboxes corresponding to the conversation
    											//happening in this chatbox

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
            this.usersTyping.remove(username); //once a user submits a message, he/she is no longer typing.
        }
        if (this.usersEnteredText.contains(username)) {
            this.usersEnteredText.remove(username); //once a user submits a message, he/she has no more
            										//unsent entered text.
        }
        chatBox.updateStatus(this.usersTyping.keySet(), this.usersEnteredText); //update the GUI
    }

    /**
     * Adds a general message line to the display.
     * 
     * @param message The message that will be added to the display.
     */
    public void addMessageToDisplay(String message) {
        chatBox.appendMessage(message);
    }

    /**
     * Makes the chat box of this chat box model visible.
     */
    public void show() {
        chatBox.setVisible(true);
    }

    /**
     * Makes the chat box of this chat box model invisible.
     */
    public void quit() {
        chatBox.setVisible(false);
    }

    /**
     * Quits the chat box of this chat box model.
     */
    public void quitChatBox() {
        if (this.isGroupChat) {
            this.model.exitChat(conversationID);
        }
    }

    /**
     * Processes that a particular user is entering text into their chat box in
     * the conversation encapsulated by this chat box model. 
     * 
     * @param username The username of the user that is entering text into their chat box
     */
    public void markTyping(final String username) {
        if (this.usersEnteredText.contains(username)) { 
            this.usersEnteredText.remove(username); //user no longer has entered unsent text
        }
        int num = 0;
        if (this.usersTyping.containsKey(username)) {
            num = this.usersTyping.get(username); //incrementing the integer corresponding to this
            									//username in usersTyping is used to detect whether
            									//the user is still typing
            num++;
        }
        this.usersTyping.put(username, num);
        chatBox.updateStatus(this.usersTyping.keySet(), this.usersEnteredText);

        //Create an ActionListener for detecting when the user has stopped typing
        final int originalNum = num;
        ActionListener enteredTextMarker = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (usersTyping.containsKey(username)
                        && usersTyping.get(username) == originalNum) {
                    usersTyping.remove(username); //if the user has stopped typing, remove from usersTyping list
                    usersEnteredText.add(username); //the user has stopped typing but still has entered
                    								//text, so add to usersEntereText list

                    chatBox.updateStatus(usersTyping.keySet(), usersEnteredText); //update the GUI
                }
            }
        };
        Timer timer = new Timer(1000, enteredTextMarker); //detect if typing has stopped every 1000 milliseconds
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Processes that a particular user has no more text in the editable text field
     * in their chat box. 
     * 
     * @param username The username of the user that no longer has text in their chat box
     */
    public void markCleared(String username) {
        if (this.usersEnteredText.contains(username)) {
            this.usersEnteredText.remove(username); //user has no entered unsent text
        }
        if (this.usersTyping.containsKey(username)) {
            this.usersTyping.remove(username); //user is no longer typing
        }
        chatBox.updateStatus(this.usersTyping.keySet(), this.usersEnteredText); //update the GUI
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Registers when a key is released; if there is no text in the editable text
     * field of the chat box, then alerts the main chat client model that the
     * user is no longer typing and that there is no entered unsent text in the chat box.
     * If there is text in this text field, then alerts the main chat client model that 
     * the user has entered but not submitted text in the chat box. 
     */
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

    /**
     * Registers when a key is pressed; sends the message, if there is one, contained in the
     * editable text field in the chat box if the key is the "enter" key.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = chatBox.sendMessage();
            if (!message.equals("")) {
                addChatLine(message);
            }
        }
    }

    //ACCESSOR
    public ChatBox getChatBox() {
        return chatBox;
    }
}
