package message;

import java.sql.Time;

import user.User;

public class ChatMessage {

    private User origin;
    private String message;
    private Time time;
    
    /**
     * Create a new ChatMessage.
     * 
     * @param origin The User from which the ChatMessage originated.
     * @param message The text comprising the ChatMessage.
     */
    public ChatMessage(User origin, String message) {
        this.origin = origin;
        this.message = message;
        this.time = new Time(System.currentTimeMillis());
    }
    
    public User getOrigin() {
        return this.origin;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Time getTime() {
        return new Time(time.getTime());
    }
    
}
