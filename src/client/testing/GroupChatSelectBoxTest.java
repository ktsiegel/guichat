package client.testing;
import java.util.Set;

import main.Client;
import main.Server;

import org.junit.Before;
import org.junit.Test; 

import server.ChatServer;
import user.User;

import client.ChatClient;
import client.ChatClientModel;
import client.GroupChatSelectBox;

/**
 * Manual Testing:
 * Testing for the GroupChatSelectBoxTest will be completed manually. This will
 * essentially consist of checking that the ChatClient is formatted and
 * functioning properly. We want the GroupChatSelectBox to appear as follows.
 * 
 *    -----------------------
 *    | friends             |
 *    |                     |
 *    |                     |
 *    -----------------------
 *    | startChatButton     |
 *    -----------------------
 *    
 * Things to check for:
 *     - Overall Layout:
 *         - friends and startChatButton are laid out as shown above
 *     - friends:
 *         - contains list of all online friends
 *         - lists users as avatar, username
 *         - list of users is left aligned and spaced appropriately
 *         - hovering over a username should change foreground to LIGHT_BLUE
 *         - leaving a username should change foreground back to black
 *         - clicking a username should change foreground to LIGHT_BLUE
 *         - contains bevel border
 *         - contains DARK_BLUE padding border (size 10)
 *         - should increase both height and width as size of window is increased
 *     - startChatButton:
 *         - should contain a button labeled "Start Group Chat"
 *         - when pressed, the button should dispose of the GroupChatSelectBox and open
 *           a new ChatBox corresponding to a Conversation with all the users whose
 *           usernames were highlighted.
 *         - If no usernames are highlighted, pressing the button should do nothing
 *           (can't start a group chat with only yourself)
 *         - Width of button should always equal width of friends
 *         - Height of button should remain a constant 25 as window is resized     
 *    
 * @category no_didit
 */

public class GroupChatSelectBoxTest {

    
}
