package pack;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

public class ChatPanelGUI extends JPanel {
	
	private JTextPane chatDisplayPane;
	private JTextPane chatTypingPane;
	
	JButton italicsButton;
	JButton boldButton;
	JButton colorButton;
	
	public ChatPanelGUI() {
		initializeGUI();
		
	}
	public void initializeGUI() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(5,5,5,5);
	
		// Chat Display Pane in ScrollPane
		chatDisplayPane = new JTextPane();
		chatDisplayPane.setEditable(false);
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
		chatTypingPane.setText("Hejsan, lite <b>bold</b> och lite <i>sad</i> sa");
		chatTypingPane.setPreferredSize(new Dimension((int) 0.6*MainGUI.WIDTH, (int) 0.2*HEIGHT));
		
		JScrollPane scrollPane = new JScrollPane(chatTypingPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension((int) 0.6*MainGUI.WIDTH, (int) 0.3*HEIGHT));
		
		c.weighty = 0.5; c.weightx = 1;
		c.gridy = 3; c.gridx = 0;
		c.gridwidth = 3;
		add(scrollPane,c);
		c.gridwidth = 1;
		
		JButton chatSendButton = new JButton("Skicka");
		c.weightx = 0; c.weighty = 0;
		c.gridx = 3;
		add(chatSendButton,c);
	}
	
	
	/*
	 * Text format button eventhandler
	 */
	private class TextStyleEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == boldButton) {
				System.out.println(checkAndInsertTag("b"));
				chatTypingPane.setText(checkAndInsertTag("b"));
			}
			else if (e.getSource() == italicsButton) {
				checkAndInsertTag("i");
			}
			else if (e.getSource() == colorButton) {
				
			}
			
			
		}
		
		private String checkAndInsertTag(String tag) {			
			int start = chatTypingPane.getSelectionStart();
			int end = chatTypingPane.getSelectionEnd();
			int offset = 0;
			String plainText = "";
			String htmlText = chatTypingPane.getText();
			
			// Isolate the correct start/end indices
			try {
				plainText = chatTypingPane.getDocument().getText(0, chatTypingPane.getDocument().getLength()).trim();
				offset = htmlText.replaceAll("([<][bi][>]|[<][/][bi][>])", "").indexOf(plainText) -1;
								
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			

			char[] htmlChars = htmlText.substring(offset).toCharArray();
			char[] plainChars = plainText.substring(0, end-1).toCharArray();
			int i = 0;
			int j = 0;
			while (i < plainChars.length) {
				if (htmlChars[j] == '<' && htmlChars[j+1] == '/') {
					System.out.println("Sluttagg: " + htmlChars[j] + htmlChars[j+1] + htmlChars[j+2]);
					j += 4;
				} else if (htmlChars[j] == '<') {
					System.out.println("Starttagg: " + htmlChars[j] + htmlChars[j+1]);
					j += 3;
				}
				i++;
				j++;
			}
			int tagChars = j-i;
			
			System.out.println("S: " + start + " E: " +  end + " O: " + offset + " TC: " + tagChars);
			start = offset + start + tagChars;
			end = offset + end + tagChars;
		
			System.out.println(htmlText.substring(start, end));
			
			
			// check if text is wrapped with modifiers already
			if (start > 2 && end - offset - tagChars < plainText.length()-3) {
				System.out.println(htmlText.substring(start-3, end+3));
				if (htmlText.substring(start-3, end+3).startsWith("<" + tag + ">") && htmlText.substring(start-3, end+3).endsWith("</" + tag + ">")) {
					System.out.print("HEY BABERIBA");
					  // return text with removed tags
				}
			}
			// return rext with inserted tags
			return htmlText.substring(0,start) + "<" + tag + ">" + htmlText.substring(start, end) + "</" + tag + ">" + htmlText.substring(end);
			
			
			
			
		}

		
	}
	

}
