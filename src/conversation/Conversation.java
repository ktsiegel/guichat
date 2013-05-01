package conversation;

import java.util.Set;

import user.User;

public abstract class Conversation {

    int id;
    Set<User> users;

    /**
     * Create a new Conversation. A Conversation must
     * have at least one user.
     * 
     * @param users The Users to be added to the Conversation.
     */
    public Conversation(Set<User> users) {
        this.users = users;
    }
    
    /**
     * Add a new user to a Conversation. Do nothing if the user is
     * already part of the Conversation.
     * 
     * @param user The User to be added to the Conversation.
     */
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);   
        }
    }
    
    /**
     * Remove a user from a Conversation. Do nothing if the user is
     * not part of the Conversation. 
     * 
     * @param user The User to be removed from the Conversation.
     */
    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
        }
    }

    public int getID() {
        return this.id;
    }

}
