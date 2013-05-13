package conversation;

import java.util.HashSet;
import java.util.Set;

import user.User;

public class Conversation {

    private final int id;
    private final Set<User> users;
    private final Set<User> inactiveUsers;

    /**
     * Create a new Conversation. A Conversation must have at least one user.
     * 
     * @param users
     *            The Users to be added to the Conversation.
     */
    public Conversation(Set<User> users, int id) {
        this.users = users;
        this.id = id;
        this.inactiveUsers = new HashSet<User>();
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
            synchronized (this.inactiveUsers){
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
        synchronized (this.users) {
            users.remove(user);
        }
    }
    
    public void deactivateUser(User user) {
    	synchronized (this.users) {
    		this.users.remove(user);
    		synchronized(this.inactiveUsers) {
    			this.inactiveUsers.add(user);
    		}
    	}
    }

    public synchronized Set<User> getUsers() {
        synchronized (this.users) {
            return new HashSet<User>(this.users);
        }
    }
    
    public synchronized Set<User> getInactiveUsers() {
    	synchronized(this.inactiveUsers) {
    		return new HashSet<User>(this.inactiveUsers);
    	}
    }

    public int getID() {
        return this.id;
    }

}
