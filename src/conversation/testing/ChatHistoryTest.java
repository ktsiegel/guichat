package conversation.testing;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import conversation.ChatHistory;

import user.User;

/**
 * Test the accessor methods and the construction of a ChatHistory object
 */

public class ChatHistoryTest {

	//Test the accessor methods and construction of the ChatHistory class.
	//Test multiple line chats.
	@Test
	public void basicInitializationTest() {
		User casey = new User("casey");
		User alex = new User("alex");
		User katie = new User("katie");
		Set<User> users = new HashSet<User>();
		users.add(casey);
		users.add(alex);
		users.add(katie);
		ChatHistory history = new ChatHistory(users,"hello this is a chat\n and another\n line blah.");
		assertEquals(history.getHistory(),"hello this is a chat\n and another\n line blah.");
		assertEquals(history.getParticipants(),new HashSet<User>(Arrays.asList(alex,casey,katie)));
	}

	//Test the participantMatch method of the ChatHistory class.
	//Test that sets of different but equal User objects match.
	//Test that participantMatch works with an empty HashSet of users.
	//Test that participantMatch works with a smaller HashSet of users.
	//Test that participantMatch works with a larger HashSet of users.
	@Test
	public void participantMatchTest() {
		User casey = new User("casey");
		User alex = new User("alex");
		User katie = new User("katie");
		User blah = new User("blah");
		Set<User> users = new HashSet<User>(Arrays.asList(casey,alex,katie,blah));
		ChatHistory history = new ChatHistory(users, "chatchat 1234567890!@#$%^&*()");
		assertTrue(history.participantMatch(new HashSet<User>(Arrays.asList(new User("blah"), new User("katie"),
				new User("alex"), new User("casey")))));
		assertFalse(history.participantMatch(new HashSet<User>()));
		assertFalse(history.participantMatch(new HashSet<User>(Arrays.asList(casey))));
		assertFalse(history.participantMatch(new HashSet<User>(Arrays.asList(new User("a"), new User("b"),
				new User("c"), new User("d"), new User("e")))));
	}
}