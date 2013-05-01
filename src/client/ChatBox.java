package client;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ChatBox extends JFrame {

    
    JTextArea display;
    JTextArea message;
    JScrollPane displayScroll;
    JScrollPane messageScroll;
    
    public ChatBox() {
        this.setSize(300, 300);
        
        display = new JTextArea();
        display.setEditable(false);
        display.setLineWrap(true);
        displayScroll = new JScrollPane(display);
        
        message = new JTextArea();
        message.setLineWrap(true);
        messageScroll = new JScrollPane(message);

        createGroupLayout();
    }
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ChatBox main = new ChatBox();

                main.setVisible(true);
            }
        });
    }
    
    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addGroup(
                        layout.createSequentialGroup()
                            .addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(messageScroll, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(messageScroll, 60, 60, 60));
    }
}
