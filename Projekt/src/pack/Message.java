package pack;

import java.awt.Color;

public class Message {

	private final String MESSAGE_SENDER;
	private final String TEXT_COLOR;
	private final String TEXT;
	private final boolean DISCONNECT;
	
	public Message(MessageBuilder m) {
		this.MESSAGE_SENDER = m.M_SENDER;
		this.TEXT = m.TEXT;
		this.TEXT_COLOR = m.T_COLOR;
		this.DISCONNECT = m.DISC;
	}
	
	@Override
	public String toString() {
		return "Avs: " + MESSAGE_SENDER + ", Text: " + TEXT + ", Textfärg: " + TEXT_COLOR;
	}
	
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("<message" + ( MESSAGE_SENDER == null ? ">" : " sender=\"" + MESSAGE_SENDER + "\">"));
		if (TEXT != null) {
			sb.append("<text" +  ( TEXT_COLOR == null ? ">" : " color=\"" + TEXT_COLOR + "\">") );
			sb.append(TEXT);
			sb.append("</text>");
		}
		if(DISCONNECT) {
			sb.append("<disconnect/>");
		}
		
		sb.append("</message>");
		
		return sb.toString();
	}
	
	
	
	public static class MessageBuilder {
		private String M_SENDER;
		private String T_COLOR;
		private String TEXT;
		private boolean DISC;
		
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
		
		public MessageBuilder disconnect() {
			this.DISC = true;
			return this;
		}
	}
	
	
	

}

