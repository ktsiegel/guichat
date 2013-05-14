package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Group;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import user.User;

import conversation.ChatHistory;

public class GroupChatSelectBox extends JFrame{
	private ChatClientModel clientModel;
	private JPanel display;
	private JScrollPane displayScroll;
	private JPanel background;
	private JButton createChatButton;
	private Set<JLabel> selected;
	
	public GroupChatSelectBox(ChatClientModel model) {
		this.setSize(200, 300);
		this.clientModel = model;
		this.selected = new HashSet<JLabel>();
		createChatButton = new JButton("Start Group Chat!");
		createChatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Set<User> chatters = new HashSet<User>();
				for (JLabel label: selected) {
					chatters.add(new User(label.getText()));
				}
				clientModel.addGroupChat(chatters);
			}
		});
		display = new JPanel();
		
		
		for (User user: clientModel.getUsers()) {
			JLabel label = new JLabel(user.getUsername());
			new GroupChatListener(label, this, user);
			display.add(label);
		}
		displayScroll = new JScrollPane(display);
		Border lineBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		display.setBorder(lineBorder);
		display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
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
        h.addComponent(createChatButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);


        Group v = layout.createSequentialGroup();
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(createChatButton, 0, 20, 20);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
	
	public boolean containsSelected(JLabel label) {
		return selected.contains(label);
	}
	
	public void addSelected(JLabel label) {
		selected.add(label);
	}
	
	public void removeSelected(JLabel label) {
		selected.remove(label);
	}
	
	public static void main(String[] args) {
		ChatClientModel model = new ChatClientModel(new ChatClient());
		List<User> userList = model.getUsers();
		userList.add(new User("casey"));
		userList.add(new User("katie"));
		userList.add(new User("alex"));
		GroupChatSelectBox box = new GroupChatSelectBox(model);
		box.setVisible(true);
	}
}
