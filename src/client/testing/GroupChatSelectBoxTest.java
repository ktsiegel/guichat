package client.testing;
import java.util.Set;

import org.junit.Test; 

import user.User;

import client.ChatClient;
import client.ChatClientModel;
import client.GroupChatSelectBox;

/**
 * @category no_didit
 */

public class GroupChatSelectBoxTest {
	/**
	 * Manually test the group chat select feature by clicking "group chat" on the main
	 * ChatClient GUI.
	 */
	@Test
	public void basicTest() {
		ChatClientModel model = new ChatClientModel(new ChatClient());
		Set<User> userList = model.getUsers();
		userList.add(new User("casey"));
		userList.add(new User("katie"));
		userList.add(new User("alex"));
		GroupChatSelectBox box = new GroupChatSelectBox(model);
		box.setVisible(true);
	}
}
