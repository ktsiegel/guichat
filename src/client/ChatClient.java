package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import javax.swing.JTextField;
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
    private final ChatClientModel model;
    
    Color DARK_BLUE = new Color(0, 51, 102);
    Color LIGHT_BLUE = new Color(102, 178, 255);
    Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);

    public ChatClient() {
        this.model = new ChatClientModel(this);
        this.model.startListening();
        quitChatOnClose();
        startLoginWindow();
    }

    /**
     * Define this to dispose of the window if the user closes the window,
     * and close all chats and logout.
     */
    private void quitChatOnClose() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (user != null) {
                    model.quitChats();
                }
                System.exit(0);
            }
        });
        
    }

    /**
     * Start the initial Login Window. This will allow the user to choose a
     * username and avatar.
     */
    private void startLoginWindow() {
        JPanel background = new JPanel();
        
        final JPanel login = new JPanel();
        login.setLayout(new BoxLayout(login, BoxLayout.PAGE_AXIS));
        
        ImageIcon imageIcon = new ImageIcon("icons/chat.jpg");
        JLabel icon = new JLabel(imageIcon);
        
        final JTextField usernameBox = new JTextField();
        login.add(usernameBox);
        JButton loginButton = new JButton("Login");
        
        JPanel avatars = new JPanel();
        
        // Load the avatars
        final JLabel[] avatarLabels = getAvatars(avatars); 

        // Create Login Listener
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String username = usernameBox.getText();
                int a = 1;
                for (int i = 1; i <= 12; i++) {
                    JLabel label = avatarLabels[i - 1];
                    if (label.getBackground().equals(Color.white)) {
                        a = i;
                    }
                }
                if (!username.matches("[A-Za-z0-9]+")) {
                    usernameIllegalUpdate(login, usernameBox);
                } else if (model.tryUsername(username, a)) {
                    user = new User(username, a);
                    startPostLoginWindow();
                } else {
                    if (username != null && !username.equals("")) {
                        usernameTakenUpdate(login, usernameBox);
                    }
                }

            }
        };
        
        // Allow user to login in by clicking button or hitting enter
        loginButton.addActionListener(listener);
        usernameBox.addActionListener(listener);

        
        // Set up the login window
        makeLoginWindowPretty(background, login, avatars);
        createBackgroundLayout(background, icon, login, avatars, loginButton);
        
        // Tell this to display the login window
        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(200, 500);
        this.setVisible(true);

    }

    /**
     * Add borders and color to the panel.
     * 
     * @param background The JPanel containing all other components
     * (should have DARK_BLUE background)
     * @param login A JPanel containing the login field
     * @param avatars A JPanel containing the avatar icons
     */
    private void makeLoginWindowPretty(JPanel background, JPanel login, JPanel avatars) {
        // Add Title to login field
        TitledBorder loginBorder = BorderFactory.createTitledBorder(
                EMPTY_BORDER, "Enter Username");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));
        login.setBorder(loginBorder);

        // Add title to avatars
        TitledBorder avatarBorder = BorderFactory.createTitledBorder(
                EMPTY_BORDER, "Choose Avatar");
        avatarBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        avatarBorder.setTitleColor(Color.white);
        avatarBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));
        avatars.setBorder(avatarBorder);

        
        // Color the window
        background.setBackground(DARK_BLUE);
        login.setOpaque(false);
    }
    
    /**
     * Layout the Components vertically. From top to bottom, there should be the icon,
     * the login field, the avatar icons, and the login button.
     * 
     * @param background The JPanel to contain all the Components
     * @param icon A JLabel containing an image (the logo)
     * @param login A JPanel which will prompt the user for a username
     * @param avatars A JPanel which will prompt the user to choose an avatar
     * @param loginButton A button which will log the user in
     */
    private void createBackgroundLayout(JPanel background, JLabel icon, JPanel login,
            JPanel avatars, JButton loginButton) {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group buttonH = layout.createSequentialGroup();
        buttonH.addGap(0, 0, Short.MAX_VALUE);
        buttonH.addComponent(loginButton, 100, 100, 100);
        buttonH.addGap(0, 10, 10);
        Group buttonV = layout.createParallelGroup();
        buttonV.addGap(0, 0, 0);
        buttonV.addComponent(loginButton, 20, 20, 20);

        Group h = layout.createParallelGroup();
        h.addComponent(icon, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(login, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(avatars, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addGroup(buttonH);

        Group v = layout.createSequentialGroup();
        v.addComponent(icon, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(login, 50, 50, 50);
        v.addComponent(avatars, 180, 180, 180);
        v.addGroup(buttonV);
        v.addGap(0, 10, 10);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
    
    /**
     * Load the 12 avatars and format them to be displayed in a 3x4 grid.
     * 
     * @return An array containing the JLabels storing the avatars.
     */
    private JLabel[] getAvatars(JPanel avatars) {
        JPanel[] avatarRows = { new JPanel(), new JPanel(), new JPanel() };
        JLabel[] avatarLabels = new JLabel[12];
        for (int i = 1; i <= 12; i++) {
            avatarLabels[i - 1] = new JLabel(new ImageIcon("icons/avatar" + i + ".png"));
        }
        for (JLabel label : avatarLabels) {
            label.setOpaque(true);
            label.setBorder(EMPTY_BORDER);
            label.setBackground(DARK_BLUE);
            label.addMouseListener(new AvatarListener(avatarLabels, label));
        }
        
        Random r = new Random();
        int defaultAvatar = r.nextInt(12);
        avatarLabels[defaultAvatar].setBackground(Color.white);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                avatarRows[row].add(avatarLabels[4 * row + col]);
                avatarRows[row].setOpaque(false);
            }
        }
        
        for (JPanel row : avatarRows) {
            avatars.add(row);
        }
        
        avatars.setOpaque(false);
        avatars.setLayout(new BoxLayout(avatars, BoxLayout.PAGE_AXIS));
        
        return avatarLabels;
    }

    /**
     * Erase the username in the login field and report that the username
     * was taken.
     * 
     * @param login A JPanel containing the login field
     * @param usernameBox A JTextField to be cleared
     */
    private void usernameTakenUpdate(JPanel login, JTextField usernameBox) {
        TitledBorder loginBorder = BorderFactory.createTitledBorder(
                EMPTY_BORDER, "Username taken");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));

        login.setBorder(loginBorder);
        usernameBox.setText("");
        validate();
    }
    
    /**
     * Erase the username in the login field and report that the username
     * was invalid.
     *
     * @param login A JPanel containing the login field
     * @param usernameBox A JTextField to be cleared
     */
    private void usernameIllegalUpdate(JPanel login, JTextField usernameBox) {
        TitledBorder loginBorder = BorderFactory.createTitledBorder(
                EMPTY_BORDER, "Username has illegal characters");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));

        login.setBorder(loginBorder);
        usernameBox.setText("");
        validate();
    }

    public void startPostLoginWindow() {
        JPanel postLoginBackground = new JPanel();
        userPanel = new JPanel();

        logoutButton = new JButton("Logout");
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


        // Add color
        users.setBackground(Color.white);
        conversations.setBackground(Color.white);
        userPanel.setBackground(DARK_BLUE);


        // Add padding
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);

        TitledBorder userBorder = BorderFactory.createTitledBorder(EMPTY_BORDER,
                "Friends");
        userBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        userBorder.setTitleColor(Color.white);
        userBorder
                .setTitleFont(userBorder.getTitleFont().deriveFont(Font.BOLD));

        users.setBorder(paddingBorder);
        userScroll.setBorder(lineBorder);
        userNest.setBorder(userBorder);

        TitledBorder conversationBorder = BorderFactory.createTitledBorder(
                EMPTY_BORDER, "Group Chats");
        conversationBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        conversationBorder.setTitleColor(Color.white);
        conversationBorder.setTitleFont(conversationBorder.getTitleFont()
                .deriveFont(Font.BOLD));

        conversations.setBorder(paddingBorder);
        conversationScroll.setBorder(lineBorder);
        conversationNest.setBorder(conversationBorder);

        Border outerBorder = BorderFactory.createEmptyBorder(0, 10, 10, 10);
        userPanel.setBorder(outerBorder);


        this.setTitle("GUI CHAT");

        userPanelLayout();

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(DARK_BLUE);
        
        buttonPanel.setBorder(EMPTY_BORDER);
        
        JButton startChatButton = new JButton("Group Chat");
        startChatButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent arg0) {
	            GroupChatSelectBox box = new GroupChatSelectBox(model);
	            box.setVisible(true);
            }
        });

        buttonPanel.add(startChatButton);

        getContentPane().removeAll();
        createPostLoginBackgroundLayout(postLoginBackground, welcomePanel, userPanel, buttonPanel);
        getContentPane().add(postLoginBackground);

    }
    
    /**
     * Create the layout for the ChatClient post-login. Will layout vertically
     * (from top to bottom): welcomePanel, userPanel, buttonPanel
     * 
     * @param postLoginBackground The JPanel to contain all the Components
     * @param welcomePanel A JPanel containing the welcome message
     * @param userPanel A JPanel containing the buddy list and chat history list
     * @param buttonPanel A JPanel containing a logout button and group chat button
     */
    private void createPostLoginBackgroundLayout(JPanel postLoginBackground,
            JPanel welcomePanel, JPanel userPanel, JPanel buttonPanel) {
        GroupLayout layout = new GroupLayout(postLoginBackground);
        postLoginBackground.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(welcomePanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(userPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);


        Group v = layout.createSequentialGroup();
        v.addComponent(welcomePanel, 50, 50, 50);
        v.addComponent(userPanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(buttonPanel, 25, 25, 25);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
    
    private JPanel createWelcomePanel() {
        welcome = new JLabel();
        welcome.setHorizontalAlignment(JLabel.CENTER);
        
        ImageIcon avatar = new ImageIcon("icons/avatar" + user.getAvatar() + ".png");
        JLabel avatarIcon = new JLabel(avatar);

        welcomePanel = new JPanel();
        welcomePanel.add(welcome);
        welcomePanel.add(avatarIcon);
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.LINE_AXIS));
        
        Border smallBorder = BorderFactory.createEmptyBorder(15, 5, 5, 5);
        avatarIcon.setBorder(smallBorder);
        welcomePanel.setBackground(DARK_BLUE);
        welcome.setForeground(Color.white);
        welcome.setBorder(BorderFactory.createCompoundBorder(
                welcome.getBorder(), paddingBorder));
        welcome.setFont(new Font(welcome.getFont().getName(), Font.PLAIN, 16));
        
        welcome.setText("<html><b>Welcome, "
                + this.user.getUsername() + "!</b></html>");

        
        
    }

    /**
     * Create layout for the userPanel, which contains the buddy list as well
     * as the list of previous group chats (aligned vertically).
     */
    private void userPanelLayout() {
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

    /**
     * Update the list of users visible to the user. This method is used
     * to both remove and add users to the buddy list.
     * 
     * @param userList A Set of Users which should be on your buddy list
     */
    public void setUserList(Set<User> userList) {
        synchronized(userList) {
        	synchronized(users) {
        		synchronized(userLabels) {
        			users.removeAll();
                    for (String label: userLabels.keySet()) {
                    	userLabels.get(label).setVisible(false);
                    }
                    userLabels.clear();
    
                    for (User nextUser : userList) {
                        // Do not add yourself to your own buddy list
                        if (nextUser.equals(this.user)) {
                            continue;
                        }
                        System.out.println("updating with " + nextUser.getUsername());
                        
                        // Load avatar
                        ImageIcon avatar = new ImageIcon("icons/avatar" + nextUser.getAvatar() + ".png");
                        JLabel avatarIcon = new JLabel(avatar);
                        
                        JLabel userLabel = new JLabel(nextUser.getUsername());
                        
                        // Create panel with <avatar> <username>
                        JPanel userPanel = new JPanel();
                        userPanel.add(avatarIcon);
                        userPanel.add(userLabel);
                        userPanel.setOpaque(false);
                        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.LINE_AXIS));
                        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        // Add panel to buddy list
                        users.add(userPanel);
                        
                        userLabels.put(nextUser.getUsername(), userLabel);
                        new UserListener(userLabel, model, nextUser);
                        
                        validate();
                    }
                    this.getContentPane().validate();
                    this.getContentPane().repaint();
        		}
        	}
        }
    }

    /**
     * Add a new ChatHistory corresponding to Conversation with ID to the
     * list of ChatHistories.
     * 
     * @param history A ChatHistory containing the history to be added
     * @param ID An int representing the ID of the Conversation to which the
     * ChatHistory pertains
     */
    public void addHistory(ChatHistory history, int ID) {
        String label = "Chat with ";
        for (User user : history.getParticipants()) {
            label += user.getUsername() + ", ";
        }
        label = label.substring(0, label.length() - 2);
        JLabel historyLabel = new JLabel(label);
        new HistoryListener(historyLabel, model, ID);
        conversations.add(historyLabel);
        conversations.revalidate();
        validate();
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
