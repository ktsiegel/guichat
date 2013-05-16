package user.testing;

import static org.junit.Assert.*;

import org.junit.Test;

import user.User;

/**
 * To test User, we test every method for accuracy. User is immutable so there
 * is not a whole lot to test.
 * 
 * The most important part is that even though there is a second parameter
 * (avatar), all comparisons are done solely based on the username of the User.
 */
public class UserTest {
    /**
     * Makes sure that both constructors and both accessors work.
     */
    @Test
    public void constructorAndAccessorTest() {
        User user1 = new User("asdf");
        assertEquals("asdf", user1.getUsername());
        assertEquals(-1, user1.getAvatar());

        User user2 = new User("asdf", Integer.MAX_VALUE);
        assertEquals("asdf", user2.getUsername());
        assertEquals(Integer.MAX_VALUE, user2.getAvatar());
    }

    /**
     * Makes sure that all types of comparison results (negative, zero, and
     * positive) are okay, regardless of their avatar values.
     * 
     * Also tests equality even for users with non-equal avatars.
     */
    @Test
    public void compareToAndEqualsTest() {
        User user1 = new User("asdf", -1);
        User user2 = new User("adsf", 0);
        User user3 = new User("qasdf", -10);
        User user4 = new User("asdf", 10);
        User user5 = new User("asdf", -1);
        assertTrue(user1.compareTo(user2) > 0);
        assertTrue(user1.compareTo(user3) < 0);
        assertTrue(user1.compareTo(user4) == 0);
        assertTrue(user1.compareTo(user5) == 0);
    }

    /**
     * Ensures that hash codes of Users are equal if and only if their usernames are equal.
     */
    @Test
    public void hashCodeTest() {
        User user1 = new User("asdf", -1);
        User user2 = new User("adsf", 0);
        User user3 = new User("qasdf", -10);
        User user4 = new User("asdf", 10);
        User user5 = new User("asdf", -1);
        assertTrue(user1.hashCode() != user2.hashCode());
        assertTrue(user1.hashCode() != user3.hashCode());
        assertTrue(user1.hashCode() == user4.hashCode());
        assertTrue(user1.hashCode() == user5.hashCode());
    }
}
