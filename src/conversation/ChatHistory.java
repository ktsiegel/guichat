package conversation;

import java.util.Set;

import user.User;

public class ChatHistory {
	private Set<User> participants;
	private String history;
	
	public ChatHistory(Set<User> people, String history) {
		this.participants = people;
		this.history = history;
	}
	
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
	
	public String getHistory() {
		return history;
	}
	
	public Set<User> getParticipants() {
		return participants;
	}
}
