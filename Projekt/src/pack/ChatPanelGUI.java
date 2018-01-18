package pack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import parsing.HTMLParser;
import parsing.Message;
import parsing.MessageParser;

/**
 *
 * @author anton
 *
 *         Each ChatPanel has a backend ChatPanelGUI which is the GUI to that
 *         chat. Displays all information and handles all graphical control of
 *         the chat.
 *
 */
public class ChatPanelGUI extends JPanel {

	private JTextPane chatDisplayPane;
	private JTextPane chatTypingPane;
	private JPanel optionPane;
	private JList<String> userList;

	JButton italicsButton;
	JButton boldButton;
	JButton colorButton;

	JButton chatSendButton;

	JButton endChatButton;
	JButton kickButton;

	private ChatPanel chatPanel;

	/**
	 * Initializes a new chatpanelgui with an associated name and chatthread
	 *
	 * @param name
	 *            The new name of the chat
	 * @param chat
	 *            The associated ChatPanel
	 */
	public ChatPanelGUI(String name, ChatPanel chat) {
		setName(name);
		chatPanel = chat;
		initializeGUI();
		setupOptionPane();
	}

	/**
	 * Initializes the GUI of the instance
	 */
	private void initializeGUI() {

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		// Chat Display Pane in ScrollPane
		chatDisplayPane = new JTextPane();
		chatDisplayPane.setEditable(false);
		chatDisplayPane.setContentType("text/html");
		chatDisplayPane.setText("<p></p>");

		JScrollPane scrollPaneDisplay = new JScrollPane(chatDisplayPane,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollPaneDisplay.setPreferredSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.6 * HEIGHT));
		scrollPaneDisplay.setMinimumSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.6 * HEIGHT));
		scrollPaneDisplay.setMaximumSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.55 * HEIGHT));

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(scrollPaneDisplay, c);
		c.gridwidth = 1;

		// Separator
		GridBagConstraints cSep = new GridBagConstraints();
		cSep.fill = GridBagConstraints.HORIZONTAL;
		cSep.gridy = 1;
		cSep.gridwidth = 2;
		cSep.insets = new Insets(10, 0, 10, 0);
		add(new JSeparator(SwingConstants.HORIZONTAL), cSep);

		// text formatting buttons and handler
		TextStyleEventHandler tsEventHandler = new TextStyleEventHandler();
		italicsButton = new JButton("i");
		italicsButton.addActionListener(tsEventHandler);

		boldButton = new JButton("b");
		boldButton.addActionListener(tsEventHandler);

		colorButton = new JButton("Färg");
		colorButton.addActionListener(tsEventHandler);

		c.gridy = 2;
		c.gridx = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(boldButton, c);

		c.gridy = 3;
		add(italicsButton, c);

		c.gridy = 4;
		add(colorButton, c);

		// Input pane within scrollpane
		chatTypingPane = new JTextPane();
		chatTypingPane.setContentType("text/html");

		// NOTE: THE INITIAL TEXT MUST BE NON_EMPTY IN ORDER FOR THE
		// HTML-FORMATTING TO BE CORRECT
		chatTypingPane.setText("--");
		chatTypingPane.setPreferredSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.4 * HEIGHT));
		chatTypingPane.setMinimumSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.4 * HEIGHT));
		chatTypingPane.setMaximumSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.45 * HEIGHT));

		JScrollPane scrollPane = new JScrollPane(chatTypingPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollPane.setPreferredSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.4 * HEIGHT));
		scrollPane.setMinimumSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.4 * HEIGHT));
		scrollPane.setMaximumSize(new Dimension((int) 0.6 * MainGUI.WIDTH, (int) 0.45 * HEIGHT));

		c.weighty = 0.15;
		c.weightx = 1;
		c.gridy = 2;
		c.gridx = 0;
		c.gridheight = 4;
		add(scrollPane, c);
		c.gridheight = 1;

		chatSendButton = new JButton("Skicka");
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 5;
		add(chatSendButton, c);
		chatSendButton.addActionListener(new SendMessageEventHandler());

	}

	/**
	 * Sets up the OptionPane of the GUI
	 */
	private void setupOptionPane() {
		optionPane = new JPanel();
		optionPane.setLayout(new GridBagLayout());
		optionPane.setPreferredSize(new Dimension((int) (0.4 * MainGUI.WIDTH), (int) (0.32 * MainGUI.HEIGHT)));
		optionPane.setMinimumSize(new Dimension((int) (0.4 * MainGUI.WIDTH), (int) (0.3 * MainGUI.HEIGHT)));
		optionPane.setMaximumSize(new Dimension((int) (0.4 * MainGUI.WIDTH), (int) (0.32 * MainGUI.HEIGHT)));

		kickButton = new JButton("Koppla ned användare");
		kickButton.setEnabled(false);
		endChatButton = new JButton("Avsluta chatt");

		userList = new JList<String>();
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		userList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(userList);
		listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.gridwidth = 2;
		optionPane.add(listScroller, c);

		c.weightx = 1;
		c.weighty = 0.2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		optionPane.add(kickButton, c);

		c.gridx = 1;
		optionPane.add(endChatButton, c);
		optionPane.setVisible(true);

		OptionPaneEventHandler opeh = new OptionPaneEventHandler();
		kickButton.addActionListener(opeh);
		userList.addFocusListener(opeh);
		userList.addListSelectionListener(opeh);
		endChatButton.addActionListener(opeh);
	}

	/**
	 * Returns the associated optionpane of the GUI
	 *
	 * @return The associated OptionPane
	 */
	public JPanel getOptionPane() {
		return optionPane;
	}

	/**
	 *
	 * @return Returns the JList<String> containing all users connected to the
	 *         current chat
	 */
	public JList<String> getUserList() {
		return userList;
	}

	/**
	 * Displays an incoming message to the chat GUI
	 *
	 * @param msg
	 *            - The message to be displayed
	 */
	public void displayMessage(Message msg) {
		HTMLParser displayParser;
		try {
			displayParser = new HTMLParser(chatDisplayPane.getText());
		} catch (SAXException e1) {
			System.out.println("Error in HTML-formatting");
			e1.printStackTrace();
			return;
		}
		displayParser.appendToBodyNode(msg.getHTMLRepresentation());
		chatDisplayPane.setText(displayParser.getHTMLText());
	}

	/**
	 * ActionListener that handles the style formatting of the chat. Converts
	 * text to and from the XML-representation of the Message instance to the
	 * HTML representation of the GUI-display.
	 *
	 * Listens to the Style and Formatting buttons of the current GUI.
	 */
	private class TextStyleEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			HTMLParser p = null;
			try {
				p = new HTMLParser(chatTypingPane.getText());
			} catch (SAXException e1) {
				System.out.println("Error in HTML-formatting");
				e1.printStackTrace();
				return;
			}

			if (e.getSource() == boldButton) {
				checkAndInsertTag("b", p);
			} else if (e.getSource() == italicsButton) {
				checkAndInsertTag("i", p);
			} else if (e.getSource() == colorButton) {
				Color col = JColorChooser.showDialog(MainGUI.getInstance(), "Välj chattfärg", Color.BLACK);
				Main.DEBUG("Valde färg " + col);
				if (col != null) {
					String hex = String.format("#%02X%02X%02X", col.getRed(), col.getGreen(), col.getBlue());
					Main.DEBUG(hex);
					((Element) p.getFontNode()).setAttribute("color", hex);
					Main.DEBUG(p.getHTMLText());
					chatTypingPane.setText(p.getHTMLText());
				}

			}
		}

		/**
		 * Utility method for checking a HTML-representation and inserting the
		 * appropiate tags for whichever style was selected for the highlighted
		 * text.
		 *
		 * @param tag
		 *            The tag to be inserted
		 * @param p
		 *            The HTMLParser instance that parsed the chat display.
		 */
		private void checkAndInsertTag(String tag, HTMLParser p) {
			try {
				int start = chatTypingPane.getSelectionStart() - 1;
				int end = chatTypingPane.getSelectionEnd() - 1;
				int offsetEnd = preceededTagIndices(p.getPlainBodyContent(), p.getBodyContent(), end);
				int offsetStart = preceededTagIndices(p.getPlainBodyContent(), p.getBodyContent(),
						(start - end) > 3 ? start + 3 : start);

				if (((start + offsetStart) < 0) || ((end + offsetEnd) < 0)) {
					return;
				}

				String openTag = "<" + tag + ">";
				String closeTag = "</" + tag + ">";
				String bodyContent = p.getBodyContent();
				Main.DEBUG(bodyContent);
				Main.DEBUG("offsetStart: " + (offsetStart + start) + " offsetEnd: " + (offsetEnd + end));
				Main.DEBUG("Selected word: " + bodyContent.substring(start + offsetStart, end + offsetEnd));

				// Selected word starts at index start+offset and ends at
				// end+offset
				// first end tag after word, first start tag before word
				int prevOpenTag = bodyContent.substring(0, (start + offsetStart) > 0 ? start + offsetStart : 3)
						.lastIndexOf(openTag);
				int prevCloseTag = bodyContent.substring(0, start + offsetStart).lastIndexOf(closeTag);
				int postCloseTag = bodyContent.indexOf(closeTag, end + offsetEnd);
				int postOpenTag = bodyContent.indexOf(openTag, end + offsetEnd);
				Main.DEBUG(prevOpenTag + " " + prevCloseTag + " " + postOpenTag + " " + postCloseTag + " ");

				// Selection size zero
				if ((end - start) <= 0) {
					// do nothing
				}
				// First check if word is contained within actual tags already
				else if (((prevOpenTag > prevCloseTag) && (postOpenTag > 0 ? postCloseTag < postOpenTag : true))
						&& (postCloseTag > prevOpenTag)) {
					Main.DEBUG("Word contained within same tags");
					bodyContent = bodyContent.substring(0, start + offsetStart) + closeTag
							+ bodyContent.substring(start + offsetStart, end + offsetEnd) + openTag
							+ bodyContent.substring(end + offsetEnd);
				} else if ((((start + offsetStart) - 4) > 0) && ((end + offsetEnd + 5) <= (bodyContent.length() - 1))) {
					// Case "<b>TEXT</b>"
					if (bodyContent.substring((start + offsetStart) - 3, end + offsetEnd + 4).trim().startsWith(openTag)
							&& bodyContent.substring((start + offsetStart) - 3, end + offsetEnd + 4).trim()
									.endsWith(closeTag)) {
						Main.DEBUG("Word has form <b>TEXT</b>");
						bodyContent = bodyContent.substring(0, (start + offsetStart) - 3)
								+ bodyContent.substring(start + offsetStart, end + offsetEnd)
								+ bodyContent.substring(end + offsetEnd + 4);
					}
					// Case " <b>TEXT</b> "
					else if (bodyContent.substring((start + offsetStart) - 4, end + offsetEnd + 5).trim()
							.startsWith(openTag)
							&& bodyContent.substring((start + offsetStart) - 4, end + offsetEnd + 5).trim()
									.endsWith(closeTag)) {
						Main.DEBUG("Word has form '<b>TEXT</b> '");
						bodyContent = bodyContent.substring(0, (start + offsetStart) - 3)
								+ bodyContent.substring(start + offsetStart, end + offsetEnd)
								+ bodyContent.substring(end + offsetEnd + 4);
					}
					// Case "blabla... TEXT ...blabla..."
					else {
						Main.DEBUG("Selection within text");
						bodyContent = bodyContent.substring(0, start + offsetStart) + openTag
								+ bodyContent.substring(start + offsetStart, end + offsetEnd) + closeTag
								+ bodyContent.substring(end + offsetEnd);
					}
				}
				// Case "TEXT ...blabla.." or ".... blabla... TEXT"
				else {
					Main.DEBUG("Selection within text at start or end");
					bodyContent = bodyContent.substring(0, start + offsetStart) + openTag
							+ bodyContent.substring(start + offsetStart, end + offsetEnd) + closeTag
							+ bodyContent.substring(end + offsetEnd);
				}

				// Remove any tags of the form <open></open>
				bodyContent = bodyContent.replaceAll("[<][b][>][<][/][b][>]", "");
				bodyContent = bodyContent.replaceAll("[<][b][>]\\s*[<][/][b][>]", " ");
				bodyContent = bodyContent.replaceAll("[<][/][b][>][<][b][>]", "");
				bodyContent = bodyContent.replaceAll("[<][/][b][>]\\s*[<][b][>]", " ");

				bodyContent = bodyContent.replaceAll("[<][i][>][<][/][i][>]", "");
				bodyContent = bodyContent.replaceAll("[<][i][>]\\s*[<][/][i][>]", " ");
				bodyContent = bodyContent.replaceAll("[<][/][i][>][<][i][>]", "");
				bodyContent = bodyContent.replaceAll("[<][/][i][>]\\s*[<][i][>]", " ");

				Main.DEBUG(bodyContent);

				Element newBodyNode = p.buildNode("<body>\n\t" + bodyContent + "\n</body>");

				p.replaceBodyNode(newBodyNode);

				chatTypingPane.setText(p.getHTMLText());
			} catch (SAXException e) {
				System.out.println(e.getMessage());
			} catch (StringIndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

		}

		/**
		 * Calculates the number of tag-characters that differ between the two
		 * html-strings
		 *
		 * @param plain
		 *            - The plain non-formatted text representation
		 * @param html
		 *            - The HTML representation of the text
		 * @param endIndex
		 *            - The index to end at
		 * @return The number of characters differing the plain and html
		 *         represenation
		 */
		private int preceededTagIndices(String plain, String html, int endIndex) {

			int pl = 0;
			int ht = 0;
			for (; pl < endIndex; pl++) {
				// System.out.println("Comparing " + plain.charAt(pl) + " with "
				// + html.charAt(ht));
				if (plain.charAt(pl) == html.charAt(ht)) {
					ht++;
					continue;
				} else if ((ht + 2) > (html.length() - 1)) {
					break;
				} else if ((html.charAt(ht) == '<') && ((html.charAt(ht + 1) == 'b') || (html.charAt(ht + 1) == 'i'))) {
					ht += 3;
					pl--;
					continue;
				} else if (((html.charAt(ht) == '<') && ((html.charAt(ht + 2) == 'b') || (html.charAt(ht + 2) == 'i')))
						&& (html.charAt(ht + 1) == '/')) {
					ht += 4;
					pl--;
					continue;
				}
			}
			// System.out.println(ht-pl);
			return ht - pl;
		}

	}

	/**
	 * EventHandler for the Send-button. Handles the send procedeure and
	 * produces a Message-instance to be broadcasted.
	 *
	 * @author anton
	 *
	 */
	private class SendMessageEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == chatSendButton) {
				HTMLParser displayParser = null;
				HTMLParser typingParser = null;
				try {
					displayParser = new HTMLParser(chatDisplayPane.getText());
					typingParser = new HTMLParser(chatTypingPane.getText());
				} catch (SAXException e1) {
					System.out.println("Error in HTML-formatting");
					e1.printStackTrace();
					return;
				}
				Message msg = new MessageParser().convertHTMLtoMessage(typingParser);
				// Only send non empty messages
				if (!msg.getMessageText().trim().equalsIgnoreCase("")) {
					// System.out.println(msg.getHTMLRepresentation());
					displayParser.appendToBodyNode(msg.getHTMLRepresentation());
					chatDisplayPane.setText(displayParser.getHTMLText());
					chatTypingPane.setText("");

					chatPanel.dispatchMessage(msg);
				}
			}
		}
	}

	/**
	 *
	 * OptionPanel Event-Handler that handles input from the OptionPane, that
	 * is, removing users/clients and closing down chats.
	 *
	 * @author anton
	 *
	 */
	private class OptionPaneEventHandler implements ActionListener, ListSelectionListener, FocusListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// If we want to end the current chat, close it and send a message
			// to all connected users.
			if (e.getSource() == endChatButton) {
				System.out.println("End Chat");
				
				// If disconnect, then send disconnect msg to all connected clients and close chat
				if (chatPanel.getClients().size() > 0) {
					// Notify clients
					Message disconnectMessage = new Message(new Message.MessageBuilder().disconnect()
							.setMessageSender(Main.CURRENT_CHAT_NAME).setText("User terminating connection"));
					chatPanel.dispatchMessage(disconnectMessage);
					chatPanel.disconnectAll();
					
					// Notify user
					displayMessage(new Message(new Message.MessageBuilder().setMessageSender(Main.CURRENT_CHAT_NAME)
							.setText("-- All users disconnected. --")));
					endChatButton.setText("Stäng chatt");
				}
				else {
					chatPanel.onDisconnectAll();
				}
				

			} else if (e.getSource() == kickButton) {
				System.out.println("Kick User");
				
				// Notify kicked client
				Message kickMessage = new Message(new Message.MessageBuilder().disconnect()
						.setMessageSender("SYSTEM (" + Main.CURRENT_CHAT_NAME + ")").setTextColor("#FF0000")
						.setText("---- You have been kicked from the session ----"));
				
				if (!userList.isSelectionEmpty()) {
					String qualifiedUsername = userList.getSelectedValuesList().get(0);
					String userID = chatPanel.getIDfromQualifiedName(qualifiedUsername);
					if (userID != null) {
						System.out.println("Kicking user: " + userID);
						chatPanel.sendMessageToClient(userID, kickMessage);
						
						// Notify other clients and user
						Message userHaveBeenKicked = new Message(new Message.MessageBuilder()
								.setMessageSender("SYSTEM (" + Main.CURRENT_CHAT_NAME + ")").setTextColor("#FF0000")
								.setText("---- User " + userID + " have been kicked from the session ----"));
						chatPanel.getChatPanelGUI().displayMessage(userHaveBeenKicked);
						chatPanel.dispatchMessage(userHaveBeenKicked);
						chatPanel.disconnectClient(userID);
					} else {
						System.err.println("Could not find a user with the qualified name " + qualifiedUsername);
					}

				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// If a user is selected and youre a server for a multi-part
			// conversation
			if (!userList.isSelectionEmpty() && (chatPanel.getClients().size() > 1)) {
				kickButton.setEnabled(true);
			} else {
				kickButton.setEnabled(false);
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (!userList.isSelectionEmpty() && (chatPanel.getClients().size() > 1)) {
				kickButton.setEnabled(true);
			} else {
				kickButton.setEnabled(false);
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getOppositeComponent() != kickButton) {
				kickButton.setEnabled(false);
				userList.clearSelection();
			}
		}
	}
}
