package client.testing;

/**
 * Manual Testing:
 * Testing for the ChatClient will be completed manually. This will essentially
 * consist of checking that the ChatClient is formatted properly. We want the
 * ChatClient to appear as follows:
 * 
 *    -----------------------
 *    | welcome             |
 *    -----------------------
 *    | text                |
 *    |                     |
 *    |  onlineUsers        |
 *    |                     |
 *    |                     |
 *    |                     |
 *    |                     |
 *    |                     |
 *    |                     |
 *    |                     |
 *    |                     |
 *    |                     |
 *    -----------------------
 *    
 * Things to check for:
 *     - welcome displayed above onlineUsers
 *     - welcome displays the message "Welcome, username!"
 *     - welcome message is centered
 *     - welcome has white foreground and grey background
 *     - onlineUsers displays usernames (one per line)
 *     - Scroll bar appears for onlineUsers once there are sufficient users
 *     - onlineUsers has black foreground and white background
 *     - Appropriate spacing between text and edges of window
 *     - Usernames are tabbed over slightly from instructional text
 *     - Window has initial size 200 x 400
 *     - Height of welcome does not change as window is resized
 *     - All other dimensions increase proportionally as window is resized  
 *     
 * @category no_didit
 */
public class ChatClientTest {

}
