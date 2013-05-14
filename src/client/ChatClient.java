package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import conversation.ChatHistory;

import user.User;

public class ChatClient extends JFrame {

    /**
     * Default serial ID.
     */
    private static final long serialVersionUID = 1L;

    private User user;

    private Map<String, JLabel> userLabels;
    private JPanel users;
    private JPanel conversations;
    private JScrollPane userScroll;
    private JLabel welcome;
    private JPanel welcomePanel;
    private JButton logoutButton;
    private JPanel userPanel;
    private JPanel userNest;
    private JPanel conversationNest;
    private JPanel background;
    GroupLayout layout;
    private JLabel icon;
    private final ChatClientModel model;
    private Map<Integer, ChatHistory> histories;

    public ChatClient() {
        this.model = new ChatClientModel(this);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                model.quitChats();
                System.exit(0);
            }
        });
        
        startLoginWindow();
        this.setSize(200, 500);
        this.setVisible(true);
        
        this.model.startListening();

        /*String username = welcomePane("Enter a username:");
        while (!this.model.tryUsername(username)) {
            if (username != null && !username.equals("")) {
            	username = welcomePane("Sorry, that username has already been taken! Enter a username:");
            }
            else {
            	username = welcomePane("Usernames must be >0 characters long.");
            }
        }
        this.user = new User(username);
        System.out.println("GOT USERNAME");*/

    }
    
    public void startLoginWindow() {
        background = new JPanel();
        background.setBackground(new Color(0, 51, 102));
        JPanel login = new JPanel();
        JPanel avatars = new JPanel();
        
        ImageIcon imageIcon = new ImageIcon("icons/chat.png");
        icon = new JLabel(imageIcon);
        
        final JTextField usernameBox = new JTextField();
        usernameBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Here");
                String username = usernameBox.getText();
                model.tryUsername(username);
                user = new User(username);
                startPostLoginWindow();
            }

        });
        login.add(usernameBox);
        JButton loginButton = new JButton("Login");
        //login.add(loginButton);


        login.setLayout(new BoxLayout(login, BoxLayout.PAGE_AXIS));
        login.setOpaque(false);
        

        JLabel avatar1 = new JLabel(new ImageIcon("icons/avatar1.png"));
        
        
        avatars.add(avatar1);
        avatars.setOpaque(false);
        
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        TitledBorder loginBorder = BorderFactory.createTitledBorder(emptyBorder, "Enter Username");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(Font.BOLD));
        
        login.setBorder(loginBorder);
        
        TitledBorder avatarBorder = BorderFactory.createTitledBorder(emptyBorder, "Choose Avatar");
        avatarBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        avatarBorder.setTitleColor(Color.white);
        avatarBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(Font.BOLD));
        
        avatars.setBorder(avatarBorder);
        
        // Create layout
        layout = new GroupLayout(background);
        background.setLayout(layout);
        
        Group h = layout.createParallelGroup();
        h.addComponent(icon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(login, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(avatars, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(icon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(login, 50, 50, 50);
        v.addComponent(avatars, 150, 150, 150);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
        
        this.add(background);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        
    }
    
    public void startPostLoginWindow() {
        JPanel postLoginBackground = new JPanel(); 
        userPanel = new JPanel();
        
        this.histories = new HashMap<Integer, ChatHistory>();


        logoutButton = new JButton();
        logoutButton.setActionCommand("logout");
        logoutButton.addActionListener(model);

        userLabels = new HashMap<String, JLabel>();

        users = new JPanel();
        users.setLayout(new BoxLayout(users, BoxLayout.PAGE_AXIS));
        userScroll = new JScrollPane(users);

        conversations = new JPanel();
        conversations.setLayout(new BoxLayout(conversations,
                BoxLayout.PAGE_AXIS));
        JScrollPane conversationScroll = new JScrollPane(conversations);

        userNest = new JPanel();
        userNest.add(userScroll);
        userNest.setLayout(new BoxLayout(userNest, BoxLayout.PAGE_AXIS));
        userNest.setOpaque(false);

        conversationNest = new JPanel();
        conversationNest.add(conversationScroll);
        conversationNest.setLayout(new BoxLayout(conversationNest,
                BoxLayout.PAGE_AXIS));
        conversationNest.setOpaque(false);

        userPanel.add(userNest);
        userPanel.add(conversationNest);
        userPanel.setOpaque(true);

        welcome = new JLabel("Log in to start using guichat"); // ,
                                                               // " + user.getUsername() + "!");
        welcome.setHorizontalAlignment(JLabel.CENTER);

        welcomePanel = new JPanel();
        welcomePanel.add(welcome);

        // Add color
        users.setBackground(Color.white);
        conversations.setBackground(Color.white);
        userPanel.setBackground(new Color(0, 51, 102));
        welcomePanel.setBackground(new Color(0, 51, 102));
        welcome.setForeground(Color.white);

        // Add padding
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);

        TitledBorder userBorder = BorderFactory.createTitledBorder(emptyBorder,
                "Friends");
        userBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        userBorder.setTitleColor(Color.white);
        userBorder
                .setTitleFont(userBorder.getTitleFont().deriveFont(Font.BOLD));

        users.setBorder(paddingBorder);
        userScroll.setBorder(lineBorder);
        userNest.setBorder(userBorder);

        
        TitledBorder conversationBorder = BorderFactory.createTitledBorder(emptyBorder, "Group Chats");
        conversationBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        conversationBorder.setTitleColor(Color.white);
        conversationBorder.setTitleFont(conversationBorder.getTitleFont()
                .deriveFont(Font.BOLD));

        conversations.setBorder(paddingBorder);
        conversationScroll.setBorder(lineBorder);
        conversationNest.setBorder(conversationBorder);

        Border outerBorder = BorderFactory.createEmptyBorder(0, 10, 10, 10);
        userPanel.setBorder(outerBorder);

        welcome.setBorder(BorderFactory.createCompoundBorder(
                welcome.getBorder(), paddingBorder));

        this.setTitle("GUI CHAT");

        
        userPanelLayout();
        welcome.setText("<html><font size=+1><b>Welcome, " + this.user.getUsername() + "!</b></font></html>");
        
        getContentPane().removeAll();
        
        GroupLayout layout = new GroupLayout(postLoginBackground);
        postLoginBackground.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(welcomePanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(userPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(welcomePanel, 40, 40, 40);
        v.addComponent(userPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
        
        getContentPane().add(postLoginBackground);

    }

    public void addUser(User user) {
        JLabel userLabel = new JLabel(user.getUsername());
        userLabels.put(user.getUsername(), userLabel);
        new UserListener(userLabel, model, user);
        users.add(userLabel);
        validate();
    }
    
    public void addHistory(ChatHistory history, int ID) {
    	String label = "Chat with ";
    	for (User user: history.getParticipants()) {
    		label += user.getUsername() + ", ";
    	}
    	label = label.substring(0,label.length()-2);
    	JLabel historyLabel = new JLabel(label);
    	histories.put(ID, history);
    	new HistoryListener(historyLabel, model, ID);
    	conversations.add(historyLabel);
    	validate();
    }

    public void removeUser(String username) {
        users.remove(userLabels.get(username));
        validate();
    }

    public void userPanelLayout() {
        GroupLayout layout = new GroupLayout(userPanel);
        userPanel.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(userNest, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(conversationNest, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(userNest, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(conversationNest, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, 150);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);

    }

    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(welcomePanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(userPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(welcomePanel, 40, 40, 40);
        v.addComponent(userPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);

    }

    public String welcomePane(String message) {
        ImageIcon icon = new ImageIcon("icons/chat.png");
        String username = (String) JOptionPane.showInputDialog(null, message,
                "Welcome!", JOptionPane.CLOSED_OPTION, icon, null, null);
        System.out.println(username);
        return username;
    }

    public ChatClientModel getModel() {
        return this.model;
    }
}
