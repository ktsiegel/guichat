package client.testing;

/**
 * Manual Testing:
 * Testing for the ChatClient will be completed manually. This will essentially
 * consist of checking that the ChatClient is formatted and functioning properly.
 * This will consist of three primary testing areas:
 *     1. LoginWindow is formatted appropriately
 *     2. Transition from LoginWindow to PostLoginWindow behaves as expected
 *     3. PostLoginWindow is formatted appropriately
 * For each of these areas we present a list of items we tested.
 *  
 * 1. We want the LoginWindow to appear as follows:
 *  
 *    --------------------------
 *    | icon                   |
 *    |                        |
 *    |                        |
 *    |                        |
 *    |                        | 
 *    |                        |                  
 *    |                        |
 *    --------------------------
 *    | login                  |       
 *    --------------------------          
 *    | avatars                |
 *    |                        |
 *    |                        |        
 *    --------------------------
 *    | loginButton            |
 *    --------------------------
 *    
 * Things to check for:
 *     - Overall layout:
 *         - The Panels are laid out as shown
 *         - The background is DARK_BLUE
 *         - Closing window does nothing additional
 *         - Window has size 250x500
 *         - Window is not resizable
 *     - icon:
 *         - Displays large logo
 *         - Logo is centered (horizonally and vertically in remaining space)
 *         - Logo is clear (not pixelated)
 *         - Logo matches background color
 *     - login:
 *         - Contains editable JTextField
 *         - Contains left aligned title "Enter Username"
 *         - When 'enter' is pressed within JTextField, should check the username,
 *           then either log the user in (send a login message to server), report
 *           that username is taken, or report that username is invalid.
 *         - To report that username is taken, clear JTextField and change title to
 *           "Username Taken"
 *         - Valid usernames are alphanumeric
 *         - To report that username is invalid, clear JTextField and change title to
 *           "Invalid Username"
 *      - avatars:
 *         - Should contain 12 distinct avatars aligned in a 3x4 grid
 *         - Each avatar should be 40x40
 *         - 11 avatars should have DARK_BLUE background, one avatar should have white
 *           background (the "chosen" avatar")
 *         - Hovering over an avatar which is not chosen should change background to
 *           LIGHT_BLUE
 *         - Clicking on an avatar should change background to White and change the
 *           and change background of previously chosen avatar to DARK_BLUE
 *         - Initially, one avatar should be chosen at random
 *     - loginButton:
 *         - Should be aligned to the right of the screen
 *         - Should say "Login"
 *         - When pressed, behave as pressing 'enter' in JTextField
 *         
 * 2. Logging In
 * 
 * A successful login should take the user from the Login window to the PostLogin window.
 *     - None of the features of the PostLoginWindow should be visible during the LoginWindow
 *     - None of the features of the LoginWindow should be visible during the PostLoginWindow
 *     - Upon login, the LoginWindow should immediately be replaced by the PostLoginWindow
 *     - The frame should remain constant (not change size or disappear/reappear)
 *     - The information gathered from the LoginWindow (username and avatar) should appear in
 *       the welcomePanel of the PostLoginWindow   
 *  
 * 3. We want the PostLoginWindow to appear as follows:
 * 
 *    --------------------------
 *    | welcomePanel           |
 *    --------------------------
 *    | userPanel              |
 *    | ---------------------- |        
 *    | | userWindow         | |
 *    | |                    | |
 *    | |                    | |
 *    | |                    | |
 *    | ---------------------- |       
 *    | ---------------------- |          
 *    | | conversationWindow | |
 *    | |                    | |
 *    | ---------------------- |        
 *    --------------------------
 *    | buttonPanel            |
 *    --------------------------
 *    
 * Things to check for:
 *     - Overall Layout:
 *         - The Panels are laid out as shown
 *         - The background is DARK_BLUE
 *         - Closing window sends logout message to server
 *         - Initial window has size 250x500
 *     - welcomePanel:
 *         - Contains a message of the form "Welcome, 'username'!"
 *         - Contains user icon to the right
 *         - Welcome message is centered in remaining space
 *         - Welcome message has enlarged font
 *         - Icon has sufficient spacing from frame edges
 *         - Height of welcomePanel remains constant 50 when window is resized
 *         - Width of welcomePanel always equals width of window
 *         - DARK_BLUE background
 *         - White foreground
 *     - userPanel:
 *         - Contains a userWindow and conversationWindow, laid out as above
 *         - Adequate padding between edges of windows and edges of frame
 *         - userWindow:
 *             - Contains left aligned title "Friends" (white foreground)
 *             - Lowered bevel border
 *             - Displays users as avatar, username pairs (left aligned within list)
 *             - Scroll bar appears when there are enough friends
 *             - White background
 *             - Black foreground
 *             - Usernames are highlighted in LIGHT_BLUE when mouse hovers
 *             - ChatBox appears corresponding to Conversation with that user when
 *               username is clicked
 *             - Height of userWindow increases as much as window size increases
 *             - Width of userWindow increases as much as window size increases
 *         - conversationWindow:
 *             - Contains left aligned title "Group Chats"
 *             - Lowered bevel border
 *             - Displays names of chats "Chat with user1, user2, ..."
 *             - Scroll bars appears when there are enough conversations
 *             - White background
 *             - Black foreground
 *             - Chat names are highlighted in LIGHT_BLUE when mouse hovers
 *             - HistoryBox appears corresponding to that Conversation when
 *               Conversation name is clicked
 *             - Height of conversationWindow remains constant 150 when window is resized
 *             - Width of conversationWindow increases as much as window size increases
 *     - buttonPanel:
 *         - Contains logoutButton and startChatButton, aligned horizontally
 *         - Pressing logout button sends logout message to server and closes window
 *         - Pressing startChatButton opens GroupChatSelectBox
 *         - Height of buttonPanel remains constant 25 when window is resized
 *              
 *     
 * @category no_didit
 */
public class ChatClientTest {

}
