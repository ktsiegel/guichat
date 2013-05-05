package client;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import server.ChatServer;
import user.User;

public class ChatClient extends JFrame {

    private final User user;
    private final Map<String, JLabel> userLabels;
    private final JPanel users;
    private final JPanel onlineUsers;
    private final JScrollPane userScroll;
    private final JLabel welcome;
    private final JPanel welcomePanel;

    private final ChatClientModel model;

    public ChatClient() {

        this.model = new ChatClientModel(this);
        this.setSize(200, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            	model.quitChats();
            	System.exit(0);
            }
        });

        userLabels = new HashMap<String, JLabel>();

        onlineUsers = new JPanel();
        onlineUsers.setLayout(new BoxLayout(onlineUsers, BoxLayout.PAGE_AXIS));

        users = new JPanel();
        users.setLayout(new BoxLayout(users, BoxLayout.PAGE_AXIS));
        users.add(new JLabel("Click on a friend to chat!"));
        users.add(onlineUsers);

        userScroll = new JScrollPane(users);

        welcome = new JLabel("Welcome!"); // , " + user.getUsername() + "!");
        welcome.setHorizontalAlignment(JLabel.CENTER);

        welcomePanel = new JPanel();
        welcomePanel.add(welcome);

        // Add color
        users.setBackground(Color.white);
        onlineUsers.setBackground(Color.white);
        welcomePanel.setBackground(Color.DARK_GRAY);
        welcome.setForeground(Color.white);

        // Add padding
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        onlineUsers.setBorder(BorderFactory.createCompoundBorder(
                onlineUsers.getBorder(), paddingBorder));
        users.setBorder(BorderFactory.createCompoundBorder(users.getBorder(),
                paddingBorder));
        welcome.setBorder(BorderFactory.createCompoundBorder(
                welcome.getBorder(), paddingBorder));

        createGroupLayout();
        this.setTitle("GUI CHAT");
        this.setVisible(true);

        this.model.startListening();

        String username = welcomePane("Enter a username:");
        while (!this.model.tryUsername(username)) {
            username = welcomePane("Sorry, that username has already been taken! Enter a username:");
        }
        this.user = new User(username);

        welcome.setText("Welcome, " + this.user.getUsername() + "!");
        
    }

    public void addUser(User user) {
        JLabel userLabel = new JLabel(user.getUsername());
        userLabels.put(user.getUsername(), userLabel);
        new UserListener(userLabel, model, user);
        onlineUsers.add(userLabel);
        pack();
    }

    public void removeUser(String username) {
        onlineUsers.remove(userLabels.get(username));
        onlineUsers.revalidate();
    }

    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(welcomePanel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(userScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(welcomePanel, 40, 40, 40);
        v.addComponent(userScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);

    }

    public String welcomePane(String message) {
        ImageIcon icon = new ImageIcon("icons/chat.png");
        String username = (String) JOptionPane.showInputDialog(null, message,
                "Welcome!", JOptionPane.CLOSED_OPTION, icon, null, null);
        return username;
    }

    public static void main(final String[] args) {
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        // ChatClient main = new ChatClient();
        //
        // main.addUser(new User("Casey"));
        // main.addUser(new User("Katie"));
        // main.addUser(new User("Alex"));
        // //main.setVisible(true);
        // }
        // });
    }
}
