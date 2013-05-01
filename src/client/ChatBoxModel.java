package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import conversation.Conversation;

public class ChatBoxModel implements ActionListener {
    
    private ChatBox chatBox;
    private Conversation conversation;
    private ChatClientModel model;

    public ChatBoxModel(ChatClientModel model, ChatBox chatBox, Conversation conversation) {
        this.model = model;
        this.chatBox = chatBox;
        this.conversation = conversation;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
    

}
