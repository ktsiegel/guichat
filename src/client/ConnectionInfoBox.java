package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Group;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * This class represents the initial window that allows the user to select a
 * hostname and port to connect to. Upon successful usage, a chat client will be
 * started to allow users to login.
 */
public class ConnectionInfoBox extends JFrame {
    /**
     * Default serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Various swing elements that are a part of the GUI.
     */
    private JPanel background;
    private JLabel IPLabel;
    private JTextField IPField;
    private JLabel portLabel;
    private JTextField portField;
    private JButton submitButton;

    /**
     * Creates a basic connection box that will allow users to input hostname
     * and port values to connect to. Upon an invalid input the connection box
     * will display a prompt and ask the user to try again.
     */
    public ConnectionInfoBox() {
        this.setSize(300, 200);

        // Borders used in the ConnectionInfoBox GUI
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        IPLabel = new JLabel("Input IP Address of server host.");
        IPLabel.setForeground(Color.WHITE);
        IPField = new JTextField();
        IPField.setBorder(lineBorder);
        IPField.setText("192.30.35.221"); // a permanent server is set up on
                                          // this IP address

        portLabel = new JLabel("Inport port number.");
        portLabel.setForeground(Color.WHITE);
        portField = new JTextField();
        portField.setBorder(lineBorder);
        portField.setText("4567");

        submitButton = new JButton("Start Chat Client!");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new ChatClient(IPField.getText(), portField.getText());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "Cannot establish connection. Please try again.");
                    return;
                }
                dispose();
            }
        });

        // The background panel that will contain the text field that contains
        // the chat history.
        background = new JPanel();
        background.setBackground(new Color(0, 51, 102));
        background.setBorder(paddingBorder);
        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        createGroupLayout();

        this.setVisible(true);
    }

    /**
     * Create the layout of the ConnectionInfoBox.
     */
    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup(); // horizontal group
        h.addComponent(IPLabel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(IPField, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(portLabel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(portField, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(submitButton, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup(); // vertical group
        v.addComponent(IPLabel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(IPField, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(portLabel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(portField, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(submitButton, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }
}
