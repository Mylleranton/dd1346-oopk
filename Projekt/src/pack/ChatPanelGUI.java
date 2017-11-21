package pack;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ChatPanelGUI extends JPanel {
	
	private JTextPane chatDisplayPane;
	private JTextPane chatTypingPane;
	private JPanel optionPane;
	private JList<String> userList;
	
	JButton italicsButton;
	JButton boldButton;
	JButton colorButton;
	
	JButton chatSendButton;
	
	public ChatPanelGUI(String name) {
		this.setName(name);
		initializeGUI();
		setupOptionPane();
		
	}
	
	private void initializeGUI() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(5,5,5,5);
	
		// Chat Display Pane in ScrollPane
		chatDisplayPane = new JTextPane();
		chatDisplayPane.setEditable(false);
		chatDisplayPane.setContentType("text/html");
		chatDisplayPane.setText("<p></p>");
		
		JScrollPane scrollPaneDisplay = new JScrollPane(chatDisplayPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 4;
		add(scrollPaneDisplay, c);
		c.gridwidth = 1;
		
		// Separator
		GridBagConstraints cSep = new GridBagConstraints();
		cSep.fill = GridBagConstraints.BOTH;
		cSep.gridy = 1; cSep.gridwidth = 4;
		cSep.insets = new Insets(10,0,10,0);
		add(new JSeparator(SwingConstants.HORIZONTAL), cSep);
		
		// text formatting buttons and handler
		TextStyleEventHandler tsEventHandler = new TextStyleEventHandler();
		italicsButton = new JButton("i");
		italicsButton.addActionListener(tsEventHandler);
		
		boldButton = new JButton("B");
		boldButton.addActionListener(tsEventHandler);
		
		colorButton = new JButton("Färg");
		colorButton.addActionListener(tsEventHandler);
		
		c.gridy = 2; c.gridx = 0;
		c.weightx = 0; c.weighty = 0;
		add(boldButton,c);
		
		c.gridx = 1;
		add(italicsButton,c);
		
		c.gridx = 2;
		add(colorButton,c);
		
		// Input pane within scrollpane
		chatTypingPane = new JTextPane();
		chatTypingPane.setContentType("text/html");
		
		chatTypingPane.setText("Hejsan, lite <b>bold</b> och lite <i>sad</i> sådärja.");
		chatTypingPane.setPreferredSize(new Dimension((int) 0.6*MainGUI.WIDTH, (int) 0.2*HEIGHT));

		JScrollPane scrollPane = new JScrollPane(chatTypingPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension((int) 0.6*MainGUI.WIDTH, (int) 0.3*HEIGHT));
		
		c.weighty = 0.5; c.weightx = 1;
		c.gridy = 3; c.gridx = 0;
		c.gridwidth = 3;
		add(scrollPane,c);
		c.gridwidth = 1;
		
		chatSendButton = new JButton("Skicka");
		c.weightx = 0; c.weighty = 0;
		c.gridx = 3;
		add(chatSendButton,c);
		chatSendButton.addActionListener(new SendMessageEventHandler());
		
	}
	
	private void setupOptionPane() {
		optionPane = new JPanel();
		optionPane.setLayout(new GridBagLayout());
		optionPane.setPreferredSize(new Dimension((int) (0.4*MainGUI.WIDTH), (int) (0.4*MainGUI.HEIGHT)));
		
		JButton kickButton = new JButton("Koppla ned användare");
		JButton endChatButton = new JButton("Avsluta chatt");
		
		userList = new JList<String>(); 
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		userList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(userList);
		listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0; c.gridy = 0;
		c.gridheight = 2;
		optionPane.add(listScroller, c);
		
		c.gridheight = 1;
		c.gridx = 1;
		optionPane.add(kickButton, c);
		
		c.gridy = 1;
		optionPane.add(endChatButton, c);
		optionPane.setVisible(true);
	}
	
	public JPanel getOptionPane() {
		return this.optionPane;
	}
	public JList<String> getUserList() {
		return this.userList;
	}
	

	
	/*
	 * Text format button eventhandler
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
			}
			else if (e.getSource() == italicsButton) {
				checkAndInsertTag("i", p);
			}
			else if (e.getSource() == colorButton) {
				
			}
		}
		private void checkAndInsertTag(String tag, HTMLParser p) {
			try{
				int start = chatTypingPane.getSelectionStart()-1;
				int end = chatTypingPane.getSelectionEnd()-1;
				int offset = preceededTagIndices(p.getPlainBodyContent() ,p.getBodyContent(),end);
				
				if((start+offset < 0) || (end+offset < 0)) return;
				
				String openTag = "<" + tag + ">";
				String closeTag = "</" + tag + ">";
				String bodyContent = p.getBodyContent();
				//System.out.println("Selected word: " + bodyContent.substring(start+offset, end+offset));
				
				// Selected word starts at index start+offset and ends at end+offset
				// first end tag after word, first start tag before word
				int prevOpenTag = bodyContent.substring(0, start+offset).lastIndexOf(openTag);
				int prevCloseTag = bodyContent.substring(0, start+offset).lastIndexOf(closeTag);
				int postCloseTag = bodyContent.indexOf(closeTag, end+offset);
				int postOpenTag = bodyContent.indexOf(openTag, end+offset);
				//System.out.println(prevOpenTag + " " + prevCloseTag + " " + postOpenTag + " " + postCloseTag + " ");
				
				//Selection size zero
				if(end-start <= 0){
					// do nothing
				}
				// First check if word is contained within actual tags already
				else if((prevOpenTag > prevCloseTag && (postOpenTag > 0 ? postCloseTag < postOpenTag : true)) 
						&& postCloseTag > prevOpenTag) {
					//System.out.println("1");
					bodyContent = bodyContent.substring(0, start+offset) 
							+ closeTag
							+ bodyContent.substring(start+offset, end+offset) 
							+ openTag
							+ bodyContent.substring(end+offset);
				}	
				else if (start+offset-4 > 0 && end+offset+5 <= bodyContent.length()-1) {
					//System.out.println("2");
					// Case "<b>TEXT</b>"
					if (bodyContent.substring(start+offset-3, end+offset+4).trim().startsWith(openTag)
							&& bodyContent.substring(start+offset-3, end+offset+4).trim().endsWith(closeTag)) {
						//System.out.println("2.1");
						bodyContent = bodyContent.substring(0, start+offset-3) 
								+ bodyContent.substring(start+offset, end+offset) 
								+ bodyContent.substring(end+offset+4);
					} 
					// Case " <b>TEXT</b> "
					else if (bodyContent.substring(start+offset-4, end+offset+5).trim().startsWith(openTag)
							&& bodyContent.substring(start+offset-4, end+offset+5).trim().endsWith(closeTag)) {
						//System.out.println("2.2");
						bodyContent = bodyContent.substring(0, start+offset-3) 
								+ bodyContent.substring(start+offset, end+offset) 
								+ bodyContent.substring(end+offset+4);
					} 
					// Case "blabla... TEXT ...blabla..."
					else {
						//System.out.println("2.3");
						bodyContent = bodyContent.substring(0, start+offset) 
								+ openTag
								+ bodyContent.substring(start+offset, end+offset) 
								+ closeTag
								+ bodyContent.substring(end+offset);
					}
				}
				// Case "TEXT ...blabla.." or ".... blabla... TEXT"
				else {
					//System.out.println("3");
					bodyContent = bodyContent.substring(0, start+offset) 
							+ openTag
							+ bodyContent.substring(start+offset, end+offset) 
							+ closeTag
							+ bodyContent.substring(end+offset);
				}
				
				// Remove any tags of the form <open></open>
				bodyContent = bodyContent.replaceAll("[<][bi][>]\\s*[<][/][bi][>]", "");
				
				Element newBodyNode = p.buildNode("<body>\n\t" + bodyContent + "\n</body>");
				
				p.replaceBodyNode(newBodyNode);
				
				chatTypingPane.setText(p.getHTMLText());
			} catch(SAXException e) {
				System.out.println(e.getMessage());
			}
			

		}
		
		private int preceededTagIndices(String plain, String html, int endIndex) {
			//System.out.println(plain);
			//System.out.println(html);
			//System.out.println(endIndex);
			
			int pl = 0;
			int ht = 0;
			for (; pl < endIndex; pl++) {
				//System.out.println("Comparing " + plain.charAt(pl) + " with " + html.charAt(ht));
				if(plain.charAt(pl) == html.charAt(ht)) {
					ht++;
					continue;
				}
				else if (ht+2 > html.length() - 1 ) {
					break;
				}
				else if (html.charAt(ht) == '<' && (html.charAt(ht+1) == 'b' || html.charAt(ht+1) == 'i')) {
					ht += 3;
					pl--;
					continue;
				}
				else if ((html.charAt(ht) == '<' && (html.charAt(ht+2) == 'b' || html.charAt(ht+2) == 'i')) && html.charAt(ht+1) == '/') {
					ht += 4;
					pl--;
					continue;
				}
			}
			//System.out.println(ht-pl);
			return ht - pl;
		}
		
	}
	
	private class SendMessageEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == chatSendButton) {
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
				displayParser.appendToBodyNode(typingParser.getBodyContent());
				chatDisplayPane.setText(displayParser.getHTMLText());
				
				chatTypingPane.setText("");
			}
		}
	}
}
