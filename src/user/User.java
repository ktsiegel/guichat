package user;

/**
 * Corresponds to a single User of the Chat. Each User will choose a unique
 * username upon login.
 * 
 * Two Users are identical if and only if they have the same username.
 */
public class User implements Comparable<User> {

    private final String username; // The username of the user.
    private int avatar; // The integer ID of the avatar that corresponds to the
                        // user.

    /**
     * Creates a new User. Each user has a String username which is used to
     * identify them. Sets the avatar to -1 to signify that there is no avatar.
     * 
     * @param username
     *            The username given to the user.
     */
    public User(String username) {
        this.username = username;
        this.avatar = -1;
    }

    /**
     * Create a new User. Each user has a String username which is used to
     * identify them.
     * 
     * @param username
     *            The username given to the user.
     * @param avatar
     *            The avatar ID given to the user, which must be a positive
     *            integer.
     */
    public User(String username, int avatar) {
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Compares this user to another user using the username of each user.
     */
    public int compareTo(User other) {
        return this.username.compareTo(other.getUsername());
    }

    /**
     * Computes a hash that represents this user.
     * 
     * @return The numeric hash that represents this user.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    /**
     * Determines whether this user is equal to something.
     * 
     * @param obj
     *            The object to which this user may or may not be equal.
     * @return true if this user is equal to obj; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    // ACCESSORS
    public String getUsername() {
        return this.username;
    }

    public int getAvatar() {
        return this.avatar;
    }
}
