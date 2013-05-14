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

public class GroupChatSelectBox extends JFrame{
	private ChatClientModel client;
	private JScrollPane displayScroll;
	private JPanel background;
	
	public GroupChatSelectBox(ChatClient client) {
		this.setSize(100, 300);
		JPanel display = new JPanel();
		displayScroll = new JScrollPane(display);
		Border lineBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		display.setBorder(lineBorder);
        displayScroll.setBorder(lineBorder);
		background = new JPanel();
		background.setBackground(new Color(0, 51, 102));
		background.setBorder(paddingBorder);
		this.add(background);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
	    createGroupLayout();
	}
	
	private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);


        Group v = layout.createSequentialGroup();
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
}
