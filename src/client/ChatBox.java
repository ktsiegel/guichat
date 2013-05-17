package client;

import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
//import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Group;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import emoticons.Emoticon;

import user.User;

/**
 * A ChatBox is a GUI for a chat box that a user uses to talk to other users.
 */

public class ChatBox extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextPane display;
    private final JTextArea message;
    private final JScrollPane displayScroll;
    private final JScrollPane messageScroll;
    private final ChatBoxModel model;
    private final JPanel background;
    private final JPanel gap;
    private final JLabel bottomLabel;
    private Set<User> others; // The other users in the chat.
    private Set<User> leftChat; // The users who were in the chat and left.
    Color DARK_BLUE = new Color(0, 51, 102);

    /**
     * Constructs a ChatBox object.
     * 
     * @param chatClientModel
     *            The model corresponding to the user using the ChatBox
     * @param conversationID
     *            The ID corresponding to the conversation in this chat.
     * @param title
     *            The title that should go in the title bar of this chat.
     * @param isGroupChat
     *            Whether or not the chat in this ChatBox is a group chat.
     */
    public ChatBox(ChatClientModel chatClientModel, int conversationID,
            String title, boolean isGroupChat) {
        this.model = new ChatBoxModel(chatClientModel, this, conversationID,
                isGroupChat);
        others = new HashSet<User>();
        leftChat = new HashSet<User>();

        // Listener detects when the ChatBox window is closed and properly quits
        // the chat.
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.quitChatBox();
                dispose();
            }
        });

        this.setSize(300, 300);
        this.setTitle(title);

        // Construct GUI components
        display = new JTextPane();
        displayScroll = new JScrollPane(display);
        message = new JTextArea();
        messageScroll = new JScrollPane(message);
        gap = new JPanel();
        background = new JPanel();
        bottomLabel = new JLabel("");

        configureGUIElements();
        createGroupLayout();
    }

    /**
     * Configures the properties of the various GUI elements.
     */
    public void configureGUIElements() {
        display.setEditable(false);
        message.setLineWrap(true);
        message.addKeyListener(model);
        gap.setBackground(DARK_BLUE);
        background.setBackground(DARK_BLUE);
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border textBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);
        display.setBorder(textBorder);
        displayScroll.setBorder(lineBorder);
        message.setBorder(textBorder);
        messageScroll.setBorder(lineBorder);
        background.setBorder(paddingBorder);
        bottomLabel.setForeground(Color.WHITE);
        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
    }

    /**
     * Create the layout of the ChatBox.
     */
    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup(); // horizontal group
        h.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(gap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE);
        h.addComponent(messageScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(gap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE);
        h.addComponent(bottomLabel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup(); // vertical group
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(gap, 10, 10, 10);
        v.addComponent(messageScroll, 60, 60, 60);
        v.addComponent(gap, 10, 10, 10);
        v.addComponent(bottomLabel, 20, 20, 20);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }

    /**
     * Get the chat message stored in the editable text field in the ChatBox
     * GUI.
     * 
     * @return The chat message as a string.
     */
    public String sendMessage() {
        String currentMessage = message.getText().trim();
        message.setText("");
        return currentMessage;
    }

    /**
     * Record a sent chat line in the ChatBox GUI.
     * 
     * @param username
     *            The username of the user that sent the chat line.
     * @param message
     *            The text of the message that the user sent.
     */
    public void appendChatLine(String username, String message) {
        if (!this.isVisible()) { // Make the chat box visible if it was not
                                 // already
            this.setVisible(true);
        }

        // configure the text of what will be inserted into the display.
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String text = username + " [" + timestamp + "]:";

        // formatting
        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, DARK_BLUE);
        StyleConstants.setBold(keyWord, true);

        try { // insert into document
            doc.insertString(doc.getLength(), text, keyWord);
            doc.insertString(doc.getLength(), " ", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        processMessage(doc, message); // add message
        try { // insert the rest into the document
            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine whether a message contains any emoticons or laTeX commands, and
     * properly insert them into the document along with the rest of the
     * message.
     * 
     * @param doc
     *            The document into which the message will be inserted.
     * @param message
     *            The message that will eventually be inserted into the chat.
     */
    public void processMessage(StyledDocument doc, String message) {
        while (message.length() > 0) {
            boolean found = false;
            for (int i = 1; i <= message.length(); i++) {// test whether there
                                                         // are any emoticons in
                                                         // the message
                String substring = message.substring(0, i);
                if (Emoticon.isValid(substring)) { // if there is an emoticon,
                                                   // insert it accordingly into
                                                   // the doc
                    processEmoticon(doc, substring);
                    message = message.substring(i);
                    found = true;
                    break;
                }
            }
            
            //NOTE: Commented out section that handles laTeX. if
            /*int dollarIndex = message.indexOf("\\$"); // test whether there is a
                                                      // laTeX string in the
                                                      // message
            if (dollarIndex == 0 && !found) {
                int endIndex = message.indexOf("$", dollarIndex + 2);
                if (endIndex > -1) {
                    try { // process the laTeX command by inserting it
                          // accordingly into the doc
                        String latex = message.substring(dollarIndex + 2,
                                endIndex);
                        processLatex(doc, latex);
                        message = message.substring(endIndex + 1);
                        found = true;
                    } catch (org.scilab.forge.jlatexmath.ParseException e) { // handle
                                                                             // ill-formatted
                                                                             // laTeX
                                                                             // commands.
                    	e.printStackTrace();
                    }
                }
            }*/
            
            if (!found) {
                try {
                    doc.insertString(doc.getLength(), message.substring(0, 1),
                            null);
                    message = message.substring(1);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates the emoticon graphic from a string that represents an emoticon.
     * 
     * @param doc
     *            The StyledDocument into which the emoticon should be inserted.
     * @param substring
     *            The string that represents the emoticon.
     */
    public void processEmoticon(StyledDocument doc, String substring) {
        // create image
        Emoticon emoticon = new Emoticon(substring);

        ClassLoader cl = getClass().getClassLoader();
        URL url = cl.getResource("icons/" + emoticon.getURL());
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(
                url));

        StyleContext context = new StyleContext();
        Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
        JLabel label = new JLabel(icon);
        StyleConstants.setComponent(labelStyle, label);

        // insert into document
        try {
            doc.insertString(doc.getLength(), substring, labelStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the laTeX graphic from a string that represents laTeX code.
     * 
     * @param doc
     *            The StyledDocument into which the laTeX graphic should be
     *            inserted.
     * @param latex
     *            The string that represents laTeX code.
     */
    /*
    public void processLatex(StyledDocument doc, String latex) {
        // synthesize image
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.new TeXIconBuilder()
                .setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();
        icon.setInsets(new Insets(5, 5, 5, 5));
        BufferedImage image = new BufferedImage(icon.getIconWidth(),
                icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);
        ImageIcon icon1 = new ImageIcon(image);
        StyleContext context = new StyleContext();
        Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
        JLabel label = new JLabel(icon1);
        StyleConstants.setComponent(labelStyle, label);

        // insert into document
        try {
            doc.insertString(doc.getLength(), latex, labelStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    */

    /**
     * Add a message to the ongoing chat and update the GUI.
     * 
     * @param message
     *            The message that should be added to the ongoing chat.
     */
    public void appendMessage(String message) {
        // The timestamp for this message.
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String text = message + " [" + timestamp + "]\n";

        // Formatting
        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.MAGENTA);
        StyleConstants.setItalic(keyWord, true);

        try {
            doc.insertString(doc.getLength(), text, keyWord);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the bottom message on the chat box GUI.
     * 
     * @param message
     *            The new message that will be the new bottom message on the
     *            chat box GUI.
     */
    public void setBottomMessage(String message) {
        bottomLabel.setText(message);
    }

    /**
     * Generate a string from a set of users and the verbs that detail what
     * these users are currently doing.
     * 
     * @param users
     *            The set of users that are performing actions.
     * @param verb1
     *            The first verb detailing what users are doing.
     * @param verb2
     *            The second verb detailing what users are doing.
     * @return A string representing the list of users and what they are doing.
     */
    private String generateUserListWithVerb(Set<String> users, String verb1,
            String verb2) {
        String res = "";
        if (users.size() == 0) {
            return res;
        } else if (users.size() == 1) {
            Iterator<String> iterator = users.iterator();
            res += iterator.next();
            return res + " " + verb1;
        } else if (users.size() == 2) {
            Iterator<String> iterator = users.iterator();
            res += iterator.next() + " and " + iterator.next();
            return res + " " + verb2;
        } else {
            Iterator<String> iterator = users.iterator();
            int num = 0;
            while (num < users.size() - 1) {
                res += iterator.next() + ", ";
            }
            res += "and " + iterator.next();
            return res + " " + verb2;
        }
    }

    /**
     * Update the GUI based on information about which users are currently
     * typing in this conversation and which users have entered but not sent
     * text.
     * 
     * @param usersTyping
     *            The set of users that are currently typing in this
     *            conversation.
     * @param usersEnteredText
     *            The set of users that have entered but not sent text in this
     *            conversation.
     */
    public void updateStatus(Set<String> usersTyping,
            Set<String> usersEnteredText) {
        if (usersTyping.size() > 0) {
            this.setBottomMessage(this.generateUserListWithVerb(usersTyping,
                    "is typing...", "are typing..."));
        } else if (usersEnteredText.size() > 0) {
            this.setBottomMessage(this.generateUserListWithVerb(
                    usersEnteredText, "has entered text...",
                    "have entered text..."));
        } else {
            this.setBottomMessage("");
        }
    }

    /**
     * Adds a user to the list of users that are in the chat and updates the GUI
     * accordingly.
     * 
     * @param other
     *            The user to add to the chat.
     */
    public void addOther(User other) {
        others.add(other);
        updateTitle();
    }

    /**
     * Removes a user from the list of users that are in the chat. Add this user
     * to the list of users who have left the chat, but were in it previously.
     * Update the GUI accordingly.
     * 
     * @param other
     *            The user to remove from the chat.
     */
    public void removeOther(User other) {
        if (others.contains(other)) {
            others.remove(other);
            leftChat.add(other);
            updateTitle();
        }
    }

    /**
     * Updates the title displayed in the chat box window title bar.
     */
    public void updateTitle() {
        if (others.size() > 0) {
            String title = "Group chat including ";
            for (User friend : others) {
                title += friend.getUsername() + ", ";
            }
            this.setTitle(title.substring(0, title.length() - 2));
        } else {
            this.setTitle("Empty group chat.");
        }
    }

    // ACCESSORS
    public ChatBoxModel getModel() {
        return model;
    }

    public JTextArea getMessage() {
        return this.message;
    }

    public JTextPane getDisplay() {
        return this.display;
    }

    public Set<User> getOthers() {
        return others;
    }

    public Set<User> getLeftChat() {
        return leftChat;
    }
}
