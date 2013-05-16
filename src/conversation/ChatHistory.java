package conversation;

import java.util.Set;

import user.User;

/**
 * A ChatHistory holds the text of a past group chat, as well as the
 * individuals who were in that chat.
 */

public class ChatHistory {
	private Set<User> participants; //The users that were in the chat.
	private String history; //The text of the chat history.
	
	public ChatHistory(Set<User> people, String history) {
		this.participants = people;
		this.history = history;
	}
	
	/**
	 * Detects whether a set of users matches the set of users in this conversation.
	 * 
	 * @param people The set of users that may or may not match the set
	 * 				of users in this conversation.
	 * @return true if the set of users matches; false if not.
	 */
	public boolean participantMatch(Set<User>people) {
		if (people.size() != this.participants.size()) {
			return false;
		}
		for (User person:people) {
			if (!this.participants.contains(person)) {
				return false;
			}
		}
		return true;
	}
	
	//ACCESSORS
	public String getHistory() {return history;}
	public Set<User> getParticipants() {return participants;}
}
