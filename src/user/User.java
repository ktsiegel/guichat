package user;

/**
 * Corresponds to a single User of the Chat. Each User will choose a
 * unique username upon login.
 *
 */
public class User {
    
    private final String username;
    private boolean isActive;
    
    /**
     * Create a new User. Each user has a String username which is
     * used to identify them.
     * 
     * @param username
     */
    public User(String username) {
        this.username = username;
        this.isActive = true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        return result;
    }

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
    
    public String getUsername() {
        return this.username;
    }
}
