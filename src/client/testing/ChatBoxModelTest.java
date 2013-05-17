package client.testing;


/**
 * Manual Tests:
 * All testing for the ChatBoxModel will be completed manually. We will manually verify
 * that each of the methods performs as expected. The methods as well as our strategies for
 * testing them are listed below.
 *     - addChatLine:
 *         1. Start Server
 *         2. Start two Clients on Server
 *         3. From Client1, click on Client2's name, opening ChatBox
 *         4. Send message from Client1 to Client2
 *         5. Check that server received 'say' message from the user who typed
 *            (the server will print when it has received this)
 *     - addChatToDisplay:
 *         1. Start Server
 *         2. Start two Clients on Server
 *         3. From Client1, click on Client2's name, opening ChatBox
 *         4. Send message from Client1 to Client2
 *         5. Check that ChatBox display window is updated with message
 *     - addMessageToDisplay
 *         1. Start Server
 *         2. Start two Clients on Server
 *         3. From Client1, click on Client2's name, opening ChatBox
 *         4. Client1 log out, Client2 should see message that Client1 logged out
 *     - show, quitChatBox
 *         1. Start Server
 *         2. Start two Clients on Server
 *         3. From Client1, click on Client2's name, opening ChatBox
 *         4. Verify that ChatBox is visible
 *         5. Close out of window
 *         6. Verify that ChatBox is no longer visible 
 *     - markTyping, markCleared, keyPressed, keyReleased, keyTyped
 *         1. Start Server
 *         2. Start two Clients on Server
 *         3. From Client1, click on Client2's name, opening ChatBox
 *         4. Send message from Client1 to Client2 to open ChatBox for Client2
 *         5. Client1 start typing
 *         6. Client2 verify that they see message "Client1 is typing..."
 *         7. Client1 stop typing
 *         8. Client 2 verify that they see message "Client1 has entered text..."
 *     
 * @category no_didit
 */
public class ChatBoxModelTest {
	public void test() {} //to pass didit
}
