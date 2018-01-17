package pack;

public class Message {

	private final String MESSAGE_SENDER;
	private final String TEXT_COLOR;
	private final String TEXT;
	private final boolean DISCONNECT;

	public Message(MessageBuilder m) {
		MESSAGE_SENDER = m.M_SENDER;
		TEXT = m.TEXT;
		TEXT_COLOR = m.T_COLOR;
		DISCONNECT = m.DISC;
	}

	@Override
	public String toString() {
		return "Avs: " + MESSAGE_SENDER + ", Text: " + TEXT + ", Textfärg: " + TEXT_COLOR;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("<message" + (MESSAGE_SENDER == null ? ">" : " sender=\"" + MESSAGE_SENDER + "\">"));
		if (TEXT != null) {
			sb.append("<text" + (TEXT_COLOR == null ? ">" : " color=\"" + TEXT_COLOR + "\">"));
			sb.append(TEXT.trim());
			sb.append("</text>");
		}
		if (DISCONNECT) {
			sb.append("<disconnect/>");
		}

		// sb.append("</message>" + System.lineSeparator());
		sb.append("</message>");

		return sb.toString();
	}

	public String getSender() {
		return MESSAGE_SENDER == null ? "" : MESSAGE_SENDER;
	}

	public String getHTMLRepresentation() {
		StringBuilder sb = new StringBuilder();
		sb.append(MESSAGE_SENDER + ": ");
		if (TEXT != null) {
			sb.append("<font" + (TEXT_COLOR == null ? ">" : " color=\"" + TEXT_COLOR + "\">"));
			sb.append(TEXT);
			sb.append("</font>");
		}
		if (DISCONNECT) {
			sb.append("\n------ END OF CHAT ------ ");
		}
		String text = sb.toString();
		text = text.replaceAll("<fetstil>", "<b>");
		text = text.replaceAll("</fetstil>", "</b>");
		text = text.replaceAll("<kursiv>", "<i>");
		text = text.replaceAll("</kursiv>", "</i>");

		return text;
	}

	public boolean disconnect() {
		return DISCONNECT;
	}

	public static class MessageBuilder {
		private String M_SENDER;
		private String T_COLOR;
		private String TEXT;
		private boolean DISC = false;

		public MessageBuilder setTextColor(String c) {
			T_COLOR = c;
			return this;
		}

		public MessageBuilder setMessageSender(String s) {
			M_SENDER = s;
			return this;
		}

		public MessageBuilder setText(String s) {
			TEXT = s;
			return this;
		}

		public String getText() {
			return TEXT;
		}

		public MessageBuilder disconnect() {
			DISC = true;
			return this;
		}
	}

}
