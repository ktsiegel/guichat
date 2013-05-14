package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private JPanel background;
    GroupLayout layout;
    private JLabel icon;
    private final ChatClientModel model;
    private JPanel login;
    private JTextField usernameBox;
    private List<JLabel> avatarLabels;
    Color DARK_BLUE = new Color(0, 51, 102);
    Color LIGHT_BLUE = new Color(102, 178, 255);
    Color MEDIUM_BLUE = new Color(0, 102, 204);

    public ChatClient() {
        this.model = new ChatClientModel(this);
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

        startLoginWindow();
        this.setSize(200, 500);
        this.setVisible(true);

        this.model.startListening();

    }

    public void startLoginWindow() {
        background = new JPanel();
        background.setBackground(new Color(0, 51, 102));
        login = new JPanel();
        JPanel avatars = new JPanel();
        
        ImageIcon imageIcon = new ImageIcon("icons/chat.jpg");
        icon = new JLabel(imageIcon);

        usernameBox = new JTextField();
        login.add(usernameBox);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String username = usernameBox.getText();
                int a = 1;
                for (int i = 1; i <= 12; i++) {
                    JLabel label = avatarLabels.get(i - 1);
                    if (label.getBackground().equals(Color.white)) {
                        a = i;
                    }
                }
                if (!username.matches("[A-Za-z0-9]+")) {
                    usernameIllegalUpdate();
                } else if (model.tryUsername(username, a)) {
                    user = new User(username, a);
                    startPostLoginWindow();
                } else {
                    if (username != null && !username.equals("")) {
                        usernameTakenUpdate();
                    }
                }

            }

        });

        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);

        login.setLayout(new BoxLayout(login, BoxLayout.PAGE_AXIS));
        login.setOpaque(false);

        JPanel avatarsRow1 = new JPanel();
        JPanel avatarsRow2 = new JPanel();
        JPanel avatarsRow3 = new JPanel();

        JLabel avatar1 = new JLabel(new ImageIcon("icons/avatar1.png"));
        JLabel avatar2 = new JLabel(new ImageIcon("icons/avatar2.png"));
        JLabel avatar3 = new JLabel(new ImageIcon("icons/avatar3.png"));
        JLabel avatar4 = new JLabel(new ImageIcon("icons/avatar4.png"));
        JLabel avatar5 = new JLabel(new ImageIcon("icons/avatar5.png"));
        JLabel avatar6 = new JLabel(new ImageIcon("icons/avatar6.png"));
        JLabel avatar7 = new JLabel(new ImageIcon("icons/avatar7.png"));
        JLabel avatar8 = new JLabel(new ImageIcon("icons/avatar8.png"));
        JLabel avatar9 = new JLabel(new ImageIcon("icons/avatar9.png"));
        JLabel avatar10 = new JLabel(new ImageIcon("icons/avatar10.png"));
        JLabel avatar11 = new JLabel(new ImageIcon("icons/avatar11.png"));
        JLabel avatar12 = new JLabel(new ImageIcon("icons/avatar12.png"));

        avatarLabels = new ArrayList<JLabel>();
        avatarLabels.add(avatar1);
        avatarLabels.add(avatar2);
        avatarLabels.add(avatar3);
        avatarLabels.add(avatar4);
        avatarLabels.add(avatar5);
        avatarLabels.add(avatar6);
        avatarLabels.add(avatar7);
        avatarLabels.add(avatar8);
        avatarLabels.add(avatar9);
        avatarLabels.add(avatar10);
        avatarLabels.add(avatar11);
        avatarLabels.add(avatar12);

        for (JLabel label : avatarLabels) {
            label.setOpaque(true);
            label.setBorder(emptyBorder);
            label.setBackground(DARK_BLUE);
            label.addMouseListener(new AvatarListener(avatarLabels, label));
        }
        avatar1.setBackground(Color.white);

        avatarsRow1.add(avatar1);
        avatarsRow1.add(avatar2);
        avatarsRow1.add(avatar3);
        avatarsRow1.add(avatar4);
        avatarsRow2.add(avatar5);
        avatarsRow2.add(avatar6);
        avatarsRow2.add(avatar7);
        avatarsRow2.add(avatar8);
        avatarsRow3.add(avatar9);
        avatarsRow3.add(avatar10);
        avatarsRow3.add(avatar11);
        avatarsRow3.add(avatar12);

        avatars.add(avatarsRow1);
        avatars.add(avatarsRow2);
        avatars.add(avatarsRow3);
        avatars.setLayout(new BoxLayout(avatars, BoxLayout.PAGE_AXIS));
        avatarsRow1.setOpaque(false);
        avatarsRow2.setOpaque(false);
        avatarsRow3.setOpaque(false);
        avatars.setOpaque(false);

        TitledBorder loginBorder = BorderFactory.createTitledBorder(
                emptyBorder, "Enter Username");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));

        login.setBorder(loginBorder);

        TitledBorder avatarBorder = BorderFactory.createTitledBorder(
                emptyBorder, "Choose Avatar");
        avatarBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        avatarBorder.setTitleColor(Color.white);
        avatarBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));

        avatars.setBorder(avatarBorder);

        // Create layout
        layout = new GroupLayout(background);
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

        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

    }

    public void usernameTakenUpdate() {
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        TitledBorder loginBorder = BorderFactory.createTitledBorder(
                emptyBorder, "Username taken");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));

        login.setBorder(loginBorder);
        // login.revalidate();
        usernameBox.setText("");
        // usernameBox.revalidate();
        validate();
    }
    
    public void usernameIllegalUpdate() {
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        TitledBorder loginBorder = BorderFactory.createTitledBorder(
                emptyBorder, "Username has illegal characters");
        loginBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        loginBorder.setTitleColor(Color.white);
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(
                Font.BOLD));

        login.setBorder(loginBorder);
        // login.revalidate();
        usernameBox.setText("");
        // usernameBox.revalidate();
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
                emptyBorder, "Group Chats");
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
        welcome.setText("<html><b>Welcome, "
                + this.user.getUsername() + "!</b></html>");
        welcome.setFont(new Font(welcome.getFont().getName(), Font.PLAIN, 16));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(DARK_BLUE);
        
        buttonPanel.setBorder(emptyBorder);
        
        JButton startChatButton = new JButton("Group Chat");

        buttonPanel.add(startChatButton);

        getContentPane().removeAll();

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

        getContentPane().add(postLoginBackground);

    }

    public void setUserList(List<User> userList) {
        users.removeAll();

        for (User user : userList) {
            if (user.equals(this.user)) {
                continue;
            }
            JLabel userLabel = new JLabel(user.getUsername());
            userLabels.put(user.getUsername(), userLabel);
            new UserListener(userLabel, model, user);
            users.add(userLabel);
            validate();
        }
        validate();
    }

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
