package conversation;

import java.util.HashSet;
import java.util.Set;

import user.User;

/**
 * A Conversation represents a conversation between some number of
 * users greater than zero.
 */

public class Conversation {
    private final int id; //the integer ID associated with this conversation.
    private final Set<User> users; //the set of users in this conversation.
    private final Set<User> inactiveUsers; //the set of inactive users that were, 
    										//at some point, in this conversation.
    private final boolean isGroupChat; //true only if the conversation is a group chat.

    /**
     * Create a new Conversation representing a group chat. 
     * A Conversation must have at least one user.
     * 
     * @param users
     *            The Users to be added to the Conversation. users.size() > 0
     */
    public Conversation(Set<User> users, int id) {
        this.users = users;
        this.id = id;
        this.inactiveUsers = new HashSet<User>();
        this.isGroupChat = true;
    }

    /**
     * Create a new Conversation representing a private conversation between two users.
     * 
     * @param a The first user in the conversation.
     * @param b The second user in the conversation.
     * @param id The ID of the conversation.
     */
    public Conversation(User a, User b, int id) {
        this.users = new HashSet<User>();
        users.add(a);
        users.add(b);
        this.inactiveUsers = new HashSet<User>();
        this.id = id;
        this.isGroupChat = false;
    }

    /**
     * Add a new user to a Conversation. Do nothing if the user is already part
     * of the Conversation.
     * 
     * @param user
     *            The User to be added to the Conversation.
     */
    public void addUser(User user) {
        if (!this.isGroupChat) {
            throw new IllegalStateException("Only group chats can addUser()");
        }
        synchronized (this.users) {
            users.add(user);
            synchronized (this.inactiveUsers) {
                if (this.inactiveUsers.contains(user)) {
                    inactiveUsers.remove(user);
                }
            }
        }
    }

    /**
     * Remove a user from a Conversation. Do nothing if the user is not part of
     * the Conversation.
     * 
     * @param user
     *            The User to be removed from the Conversation.
     */
    public void removeUser(User user) {
        if (!this.isGroupChat) {
            throw new IllegalStateException("Only group chats can removeUser()");
        }
        synchronized (this.users) {
            users.remove(user);
        }
    }

    /**
     * Remove a user from a Conversation, but store that user's identity in the
     * list of inactive users (users that were once part of the conversation
     * but are no longer part of it)
     * 
     * @param user The user to remove from the conversation and store in the
     * 				list of inactive users.
     */
    public void deactivateUser(User user) {
        if (!this.isGroupChat) {
            throw new IllegalStateException(
                    "Only group chats can deactivateUser()");
        }
        synchronized (this.users) {
            this.users.remove(user);
            synchronized (this.inactiveUsers) {
                this.inactiveUsers.add(user);
            }
        }
    }

    /**
     * An accessor method that retrieves the list of users currently in the conversation.
     * 
     * @return The list of users currently in the conversation.
     */
    public synchronized Set<User> getUsers() {
        synchronized (this.users) {
            return new HashSet<User>(this.users);
        }
    }

    /**
     * The accessor method that retrieves the list of users that are not
     * currently in the conversation, but were in the conversation at some point.
     * 
     * @return The list of users no longer in the conversation but were once
     * 		in the conversation.
     */
    public synchronized Set<User> getInactiveUsers() {
        synchronized (this.inactiveUsers) {
            return new HashSet<User>(this.inactiveUsers);
        }
    }
    
    /**
     * Accessor method that returns whether or not this conversation is a group chat.
     * 
     * @return true if this conversation is a group chat; false if not
     */
    public boolean isGroupChat() {
        return this.isGroupChat;
    }

    /**accesses ID**/
    public int getID() {return this.id;}

}
