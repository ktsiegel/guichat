package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout.Group;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import user.User;

/**
 * A GroupChatSelectBox is a GUI that lets the user select whom he/she
 * wants to have in a group chat.
 */

public class GroupChatSelectBox extends JFrame{
	private static final long serialVersionUID = 66723252538538343L;
	
	private ChatClientModel clientModel;
	private JPanel display; //displays the labels corresponding to the possible
							//users with which the user can chat.
	private JScrollPane displayScroll;
	private JPanel background;
	private JButton createChatButton; //when clicked, creates a group chat with the selected users.
	private Set<JLabel> selected;
	
	public GroupChatSelectBox(ChatClientModel model) {
		this.setSize(200, 300);
		this.clientModel = model;
		this.selected = new HashSet<JLabel>();
		
		//The createChatButton creates a group chat with the users in the
		//HashSet of selected users when clicked.
		createChatButton = new JButton("Start Group Chat!");
		createChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<User> chatters = new HashSet<User>();
				for (JLabel label: selected) { //add all selected users to a new set of group chatters
					chatters.add(new User(label.getText()));
				}
				chatters.add(clientModel.getUser()); //add yourself to the group chat as well
				if (chatters.size() > 1) {
				    clientModel.addGroupChat(chatters);
				    dispose();
				}
			}
		});
		display = new JPanel();
		
		
		for (User user: clientModel.getUsers()) {
			if (!user.equals(clientModel.getUser())) {
				JLabel label = new JLabel(user.getUsername());
                
				ClassLoader cl = getClass().getClassLoader();
                URL url = cl.getResource("icons/avatar"
                        + user.getAvatar() + ".png");
                ImageIcon avatar = new ImageIcon(Toolkit
                        .getDefaultToolkit().createImage(url));
                JLabel avatarIcon = new JLabel(avatar);
				
                JPanel userPanel = new JPanel();
                userPanel.add(avatarIcon);
                userPanel.add(label);
                userPanel.setOpaque(false);
                userPanel.setLayout(new BoxLayout(userPanel,
                        BoxLayout.LINE_AXIS));
                
                userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
				new GroupChatListener(label, this);
				display.add(userPanel);
			}
		}
		displayScroll = new JScrollPane(display);
		Border lineBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		display.setBorder(emptyBorder);
		display.setBackground(Color.white);
		display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
        displayScroll.setBorder(lineBorder);
		background = new JPanel();
		background.setBackground(new Color(0, 51, 102));
		background.setBorder(paddingBorder);
		this.add(background);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
	    createGroupLayout();
	}
	
	/**
	 * Create the layout of the GroupChatSelectBox.
	 */
	private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup(); //horizontal group
        h.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(createChatButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);


        Group v = layout.createSequentialGroup(); //vertical group.
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addGap(10, 10, 10);
        v.addComponent(createChatButton, 0, 25, 25);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
	
	/**
	 * Checks whether a user (represented by a label) is contained in the stored list
	 * of selected user labels.
	 * 
	 * @param label The label that is checked.
	 * @return whether or not the list of selected user labels contains the user label.
	 */
	public boolean containsSelected(JLabel label) {
		return selected.contains(label);
	}
	
	/**
	 * Adds a user (represented by a label) from the stored list of selected
	 * user labels.
	 * 
	 * @param label The label that should be added to 
	 * 				the list of selected users' labels.
	 */
	public void addSelected(JLabel label) {
		selected.add(label);
	}
	
	/**
	 * Removes a user (represented by a label) from the stored list of selected
	 * user labels.
	 * 
	 * @param label The label that should be removed from the
	 * 				list of selected users' labels.
	 */
	public void removeSelected(JLabel label) {
		selected.remove(label);
	}
	

}
