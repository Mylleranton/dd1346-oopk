package parsing;

/**
 * 
 * Object that holds a representation of an XML-message.
 * Provides nice programming features. Instansiated by a 
 * MessageBuilder.
 * 
 * @author anton
 *
 */
public class Message {

	/**
	 * The static messageBuilder is used to build a message from
	 * scratch and can then be converted into a message object
	 * @author anton
	 *
	 */
	public static class MessageBuilder {
		private String M_SENDER;
		private String T_COLOR;
		private String TEXT;
		private boolean DISC = false;

		private Boolean REQ_ANS;
		private String REQ_TEXT;

		public MessageBuilder disconnect() {
			DISC = true;
			return this;
		}

		public String getText() {
			return TEXT;
		}

		public MessageBuilder setMessageSender(String s) {
			M_SENDER = s;
			return this;
		}

		public MessageBuilder setRequestAnswer(boolean b) {
			this.REQ_ANS = b;
			return this;
		}

		public MessageBuilder setRequestText(String s) {
			this.REQ_TEXT = s;
			return this;
		}

		public MessageBuilder setText(String s) {
			TEXT = s;
			return this;
		}

		public MessageBuilder setTextColor(String c) {
			T_COLOR = c;
			return this;
		}
	}
	
	public Message(MessageBuilder m) {
		MESSAGE_SENDER = m.M_SENDER;
		TEXT = m.TEXT;
		TEXT_COLOR = m.T_COLOR;
		DISCONNECT = m.DISC;
		REQ_TEXT = m.REQ_TEXT;
		REQ_ANSWER = m.REQ_ANS;
	}
	private final String MESSAGE_SENDER;
	private final String TEXT_COLOR;
	private final String TEXT;
	private final boolean DISCONNECT;
	private final String REQ_TEXT;

	private final Boolean REQ_ANSWER;

	public boolean disconnect() {
		return DISCONNECT;
	}

	public String getHTMLRepresentation() {
		StringBuilder sb = new StringBuilder();
		sb.append(MESSAGE_SENDER + ": ");
		if (TEXT != null) {
			sb.append("<font" + (TEXT_COLOR == null ? ">" : " color=\"" + TEXT_COLOR + "\">"));
			sb.append(TEXT);
			sb.append("</font>");
		} else if (REQ_TEXT != null) {
			sb.append("<font" + (TEXT_COLOR == null ? ">" : " color=\"" + TEXT_COLOR + "\">"));
			sb.append(REQ_TEXT);
			sb.append("</font>");
		}
		if (DISCONNECT) {
			// sb.append("\n------ END OF CHAT ------ ");
		}
		String text = sb.toString();

		// STYLE IMPLEMENTATION
		text = text.replaceAll("<fetstil>", "<b>");
		text = text.replaceAll("</fetstil>", "</b>");
		text = text.replaceAll("<kursiv>", "<i>");
		text = text.replaceAll("</kursiv>", "</i>");

		return text;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("<message" + (MESSAGE_SENDER == null ? ">" : " sender=\"" + MESSAGE_SENDER + "\">"));
		if (TEXT != null) {
			sb.append("<text" + (TEXT_COLOR == null ? ">" : " color=\"" + TEXT_COLOR + "\">"));
			sb.append(TEXT.trim());
			sb.append("</text>");
		}
		if (REQ_TEXT != null) {
			sb.append("<request" + (REQ_ANSWER == null ? ">" : " reply=\"" + (REQ_ANSWER ? "yes" : "no") + "\">"));
			sb.append(REQ_TEXT.trim());
			sb.append("</request>");
		}
		if (DISCONNECT) {
			sb.append("<disconnect/>");
		}

		sb.append("</message>");

		return sb.toString();
	}

	public String getMessageText() {
		return TEXT;
	}

	public Boolean getRequestAnswer() {
		return REQ_ANSWER;
	}

	public String getRequestText() {
		return REQ_TEXT;
	}

	public String getSender() {
		return MESSAGE_SENDER == null ? "" : MESSAGE_SENDER;
	}

	public String getTextColor() {
		return TEXT_COLOR;
	}

	@Override
	public String toString() {
		return "Avs: " + MESSAGE_SENDER + ", Text: " + TEXT + ", Textfärg: " + TEXT_COLOR;
	}

}
