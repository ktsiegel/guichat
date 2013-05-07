package conversation.testing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import conversation.Conversation;

import user.User;

import static org.junit.Assert.*;

/**
 * To test the Conversation class we need to ensure that adding and remove users
 * from a Conversation is done safely. Two the methods to be tested are addUser
 * and removeUser.
 *     - addUser:
 *         - If the new User is not in the Conversation then it should be added.
 *         - If the new User is in the Conversation then nothing should happen.
 *     - removeUser:
 *         - If the User is in the Conversation then it should be removed.
 *         - If the User is not in the Conversation then nothing should happen.
 * 
 * Finally both of these methods will be tested for thread safety.
 *     - addUser: If two threads attempt to add a new User to the at the same time,
 *       the User should be added only once.
 *     - removeUser: If two threads attempt to remove a User at the same time, the
 *       User should be removed.
 *
 */
public class ConversationTest {
    
    // Test that a User not already in a Conversation is added to the Conversation
    @Test
    public void addUserTest() {
        Set<User> users = new HashSet<User>();
        users.add(new User("Casey"));
        users.add(new User("Katie"));
        Conversation c = new Conversation(users, 0);
        
        Set<User> expected = new HashSet<User>();
        expected.add(new User("Casey"));
        expected.add(new User("Katie"));
        
        assertEquals(expected, users);
        
        c.addUser(new User("Alex"));
        expected.add(new User("Alex"));
        
        assertEquals(expected, users);
    }
    
    // Test that nothing changes when a User already in the Conversation is added
    @Test
    public void addNonUserTest() {
        Set<User> users = new HashSet<User>();
        users.add(new User("Casey"));
        users.add(new User("Katie"));
        Conversation c = new Conversation(users, 0);
        
        Set<User> expected = new HashSet<User>();
        expected.add(new User("Casey"));
        expected.add(new User("Katie"));
        
        assertEquals(expected, users);
        
        c.addUser(new User("Casey"));
        
        assertEquals(expected, users);
    }
    
    // Test that a User is removed from the Conversation when already present
    @Test
    public void removeUserTest() {
        User casey = new User("Casey");
        User katie = new User("Katie");
        User alex = new User("Alex");
        User katie2 = new User("Katie");
        
        Set<User> users = new HashSet<User>();
        users.add(casey);
        users.add(katie);
        users.add(alex);
        Conversation c = new Conversation(users, 0);
        
        Set<User> expected = new HashSet<User>();
        expected.add(casey);
        expected.add(katie);
        expected.add(alex);
        
        assertEquals(expected, users);
        
        c.removeUser(katie2);
        
        expected.remove(katie);
        
        assertEquals(expected, users); 
    }
    
    // Test that nothing happens when a User not in a Conversation is removed
    @Test
    public void removeNonUserTest() {
        Set<User> users = new HashSet<User>();
        users.add(new User("Casey"));
        users.add(new User("Katie"));
        Conversation c = new Conversation(users, 0);
        
        Set<User> expected = new HashSet<User>();
        expected.add(new User("Casey"));
        expected.add(new User("Katie"));
        
        assertEquals(expected, users);
        
        c.removeUser(new User("Alex"));
        
        assertEquals(expected, users);
    }
    
    // Test that addUser is thread-safe
    @Test
    public void addUserConcurrencyTest() {
        
    }
    
    // Test that removeUser is thread-safe
    @Test
    public void removeUserConcurrencyTest() {
        
    }

}
