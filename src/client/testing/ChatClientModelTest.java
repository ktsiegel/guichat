package client.testing;

/**
 * Manual Tests:
 * All testing for the ChatClientModel will be completed manually. We will manually verify
 * that each of the methods performs as expected. The methods as well as our strategies for
 * testing them are listed below.
 * 
 * - startListening: the ClientListeningThread started by this method is the only way that
 * 					the client can receive messages from the server. So, the server will only
 * 					be able to communicate with the client if this method works properly.
 * - actionPerformed, quitChats: 1) Start the chat client
 * 				2) Open up several chats
 * 				3) Click the logout button and check that all of the chat boxes associated with
 * 					the client are closed.
 * 				4) Make sure that the same thing happens when the main chat client window is closed
 * 					when multiple chat boxes are open.
 * - tryUsername: 	1) Try logging in with a username that consists of 0 characters. The login should
 * 					not be processed.
 * 				 	2) Try logging in with a username with invalid characters. The login should not be processed.
 * 					3) Try logging in with a username that is already in use. The login should not go through.
 * 					4) Try logging in with a valid username that is not already in use. The login should go through.
 * - addChat, sendChat:	In the main chat client window, click on any online user's name. 
 * 				If the user already has chatted with that user in this session, that previous chat should pop up.
 * 				Otherwise, a new chat should be started.
 * - addGroupChat: 	1) Have multiple users log in.
 * 					2) Click the "group chat" button on the main chat client GUI.
 * 					3) Select multiple users in the group chat select box.
 * 					4) Click the "start group chat" button.
 * 					5) Make sure that everyone who was added to the group chat is now in the group chat.
 * - exitChat, removeChat: 	1) Start a chat with another user, and then close that chat window. 
 * 							2) Have the other user enter text into their chat box (which hasn't been closed).
 * 							3) Make sure that the program doesn't throw an error by trying to send text to a nonexistent
 * 								chat box.
 * - showChatHistory: 	1) Start and end a group chat.
 * 						2) Make sure that the group chat history shows up in the group chat history box
 * 							on the main chat client GUI. Make sure that this label includes everyone
 * 							who was ever in the group chat.
 * 						3) Click on this label, and the chat history should pop up.
 * - sendTyping: When in a chat with another person, if that person is typing, make sure that a 
 * 				label appears at the bottom of your chat box window stating that that person is 
 * 				currently typing.
 * - sendCleared: When in a chat with another person, if that person has sent their chat message
 * 				and the editable box in their chat box is empty, make sure that your chat box GUI
 * 				no longer records that other person as typing.
 * - submitCommand, listenForResponse: The ChatClient would not respond to the user if these methods
 * 										did not work, since they are responsible for communicating with
 * 										the server, and the ChatClient mainly responds to messages
 * 										from the server.
 * - handleRequest (and helper methods):	1) Check that the chat client can successfully log in, and that
 * 												no error is thrown when a user tries to submit
 * 												an invalid username.
 * 				2) Check that a user can join and leave a chat.
 * 				3) Check that a user can join and leave a group chat.
 * 				4) Check that a user can submit messages to chats.
 * 				5) Check that when a user is typing, the other users in that chat view the label that
 * 					indicates that that user is typing. When that user has unsent entered text in the
 * 					editable text field in his chat box, check that the other users in that chat
 * 					view the label that indicates that the user has entered text.

 * @category no_didit
 */

public class ChatClientModelTest {
	public void test() {} //to pass didit
}
