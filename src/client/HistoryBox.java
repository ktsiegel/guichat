package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import conversation.ChatHistory;

public class HistoryBox extends JFrame {
	public HistoryBox(ChatHistory history) {
		this.setSize(300, 300);
		JPanel background = new JPanel();
		JTextArea display = new JTextArea();
		display.setLineWrap(true);
		display.append(history.getHistory());
	    JScrollPane displayScroll = new JScrollPane(display);
	    background.add(display);
	    this.add(background);
	}
}
