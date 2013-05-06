package conversation;

import java.util.Set;

import user.User;

public class Conversation {

    private final int id;
    private final Set<User> users;

    /**
     * Create a new Conversation. A Conversation must have at least one user.
     * 
     * @param users
     *            The Users to be added to the Conversation.
     */
    public Conversation(Set<User> users, int id) {
        this.users = users;
        this.id = id;
    }

    /**
     * Add a new user to a Conversation. Do nothing if the user is already part
     * of the Conversation.
     * 
     * @param user
     *            The User to be added to the Conversation.
     */
    public void addUser(User user) {
        synchronized (this.users) {
            users.add(user);
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
        synchronized (this.users) {
            users.remove(user);
        }
    }

    public synchronized Set<User> getUsers() {
        synchronized (this.users) {
            return this.users;
        }
    }

    public int getID() {
        return this.id;
    }

}
