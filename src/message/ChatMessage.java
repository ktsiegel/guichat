package message;

import java.sql.Time;

import user.User;

/**
 * A ChatMessage represents a message sent by a user in a chat.
 */
public class ChatMessage {

    private User origin; //The user that the message is from.
    private String message; //The message text.
    private Time time; //The time at which the message was sent.
    
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
    
    //ACCESSORS
    public User getOrigin() {return this.origin;}
    public String getMessage() {return this.message;}
    public Time getTime() {return new Time(time.getTime());}
}
