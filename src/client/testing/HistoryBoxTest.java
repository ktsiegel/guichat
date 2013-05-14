package client.testing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import client.HistoryBox;
import user.User;
import conversation.ChatHistory;

/**
 * 
 * @category no_didit
 */

public class HistoryBoxTest {
	

	/*
	 * This class should create and show new HistoryBoxes with various displays.
	 */
	public static void main(String[]args) {
		Set<User> users = new HashSet<User>();
		users.add(new User("casey"));
		
		//test whether text wrap works
		HistoryBox box = new HistoryBox(new ChatHistory(users,"blahblahblahblahblahblahblahblahblahblahblahblahblah"));
		box.setVisible(true);
		
		//test whether the display can handle multiple lines and a variety of characters
		box = new HistoryBox(new ChatHistory(users,"blahblahblahblah\n9183740q8927304q89347\ncharacters*(@*#&^$"));
		box.setVisible(true);
		
		//test whether scrolling works
		box = new HistoryBox(new ChatHistory(users,"test\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\n" +
				"test\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest"));
		box.setVisible(true);
	}

}
