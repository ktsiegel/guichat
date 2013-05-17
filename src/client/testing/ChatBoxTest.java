package client.testing;

/**
 * Manual Testing:
 * Testing for the ChatBox will be completed manually. This will essentially
 * consist of checking that the ChatBox is formatted properly and that it behaves
 * as expected. We want the ChatBox to appear as follows:
 * 
 *    -----------------------
 *    |                     |
 *    | display             |
 *    |                     |
 *    -----------------------
 *    | message             |
 *    -----------------------
 *    
 * Things to check for:
 *     - display and message are formatted as depicted above.
 *     - The text in display cannot be edited
 *     - The text in display wraps around
 *     - A scroll bar appears when display contains sufficient text
 *     - The default position of the display scroll bar is at the bottom
 *     - The text in message can be edited
 *     - The text in message wraps around
 *     - A scroll bar appears when the user types sufficient text
 *     - The default position of the message scroll bar is at the bottom
 *     - The height of message does not change as the size of the window increases
 *     - All other dimensions increase proportionately with size of window
 *     - Initial window size is 300 x 300
 *     - message has black foreground
 *     - message has white background
 *     - message has a bevel border
 *     - message is padded with a DARK_BLUE border (10 on top, left, right)
 *     - message has larger border on bottom for "user is typing..."
 *     - When another user is typing in the window, "user is typing..." message appears
 *     - display has a white background
 *     - display is padded with a DARK_BLUE border (10 on all sides)
 *     - In display text is black for the message that someone said, DARK_BLUE to say
 *       who said it
 *
 * @category no_didit
 */
public class ChatBoxTest {}
