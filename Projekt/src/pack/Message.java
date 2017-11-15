package pack;

import java.awt.Color;

public class Message {

	private final String MESSAGE_SENDER;
	private final String TEXT_COLOR;
	private final String TEXT;
	
	public Message(MessageBuilder m) {
		this.MESSAGE_SENDER = m.M_SENDER;
		this.TEXT = m.TEXT;
		this.TEXT_COLOR = m.T_COLOR;
	}
	
	@Override
	public String toString() {
		return "Avs: " + MESSAGE_SENDER + ", Text: " + TEXT + ", Textfärg: " + TEXT_COLOR;
	}
	
	
	
	public static class MessageBuilder {
		private String M_SENDER;
		private String T_COLOR;
		private String TEXT;
		
		public MessageBuilder setTextColor(String c) {
			this.T_COLOR = c;
			return this;
		}
		public MessageBuilder setMessageSender(String s) {
			this.M_SENDER = s;
			return this;
		}
		public MessageBuilder setText(String s) {
			this.TEXT = s;
			return this;
		}
		public String getText(){
			return this.TEXT;
		}
	}
	
	
	

}

