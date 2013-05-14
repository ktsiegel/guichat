package client;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import user.User;

public class ChatClient extends JFrame {

    /**
     * Default serial ID.
     */
    private static final long serialVersionUID = 1L;

    private User user;
    private final Map<String, JLabel> userLabels;
    private final JPanel users;
    private final JScrollPane userScroll;
    private final JLabel welcome;
    private final JPanel welcomePanel;
    private final JButton logoutButton;
    private final JPanel userPanel;
    private final JPanel userNest;
    private final JPanel conversationNest;

    private final ChatClientModel model;

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

        userPanel = new JPanel();
        // userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.PAGE_AXIS));

        logoutButton = new JButton();
        logoutButton.setActionCommand("logout");
        logoutButton.addActionListener(model);

        userLabels = new HashMap<String, JLabel>();

        users = new JPanel();
        users.setLayout(new BoxLayout(users, BoxLayout.PAGE_AXIS));
        userScroll = new JScrollPane(users);

        JPanel conversations = new JPanel();
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

        TitledBorder conversationBorder = BorderFactory.createTitledBorder(
                emptyBorder, "Past Group Chats");
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

        createGroupLayout();
        this.setTitle("GUI CHAT");

        this.model.startListening();

        String username = welcomePane("Enter a username:");
        while (!this.model.tryUsername(username)) {
            if (username != null && !username.equals("")) {
                username = welcomePane("Sorry, that username has already been taken! Enter a username:");
            } else {
                username = welcomePane("Usernames must be >0 characters long.");
            }
        }
        this.user = new User(username);
        System.out.println("GOT USERNAME");

        /*
         * For GUI Testing, will delete later addUser(new User("Friend A"));
         * addUser(new User("Friend B")); addUser(new User("Friend C"));
         * addUser(new User("Friend D")); addUser(new User("Friend E"));
         * addUser(new User("Friend F")); addUser(new User("Friend G"));
         * addUser(new User("Friend H")); addUser(new User("Friend I"));
         * addUser(new User("Friend J")); addUser(new User("Friend K"));
         * addUser(new User("Friend L")); addUser(new User("Friend M"));
         * addUser(new User("Friend N")); addUser(new User("Friend O"));
         * addUser(new User("Friend P")); addUser(new User("Friend Q"));
         * addUser(new User("Friend R")); addUser(new User("Friend S"));
         * addUser(new User("Friend T")); addUser(new User("Friend U"));
         * addUser(new User("Friend V")); addUser(new User("Friend W"));
         * addUser(new User("Friend X")); addUser(new User("Friend Y"));
         * addUser(new User("Friend Z"));
         * 
         * conversations.add(new JLabel("tester1")); conversations.add(new
         * JLabel("tester2")); conversations.add(new JLabel("tester3"));
         * conversations.add(new JLabel("tester4")); conversations.add(new
         * JLabel("tester5")); conversations.add(new JLabel("tester6"));
         * conversations.add(new JLabel("tester7")); conversations.add(new
         * JLabel("tester8"));
         */
        userPanelLayout();
        this.setSize(200, 500);
        welcome.setText("<html><font size=+1><b>Welcome, "
                + this.user.getUsername() + "!</b></font></html>");
        this.setVisible(true);
    }

    public void addUser(User user) {
        JLabel userLabel = new JLabel(user.getUsername());
        userLabels.put(user.getUsername(), userLabel);
        new UserListener(userLabel, model, user);
        users.add(userLabel);
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
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

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
