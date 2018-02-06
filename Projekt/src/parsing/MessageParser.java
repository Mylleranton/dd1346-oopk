package parsing;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pack.Main;
import pack.MainGUI;


/**
 * 
 * MessageParser implements the functions for reading and parsing probable
 * XML-strings and files by the Document classes in java.
 * 
 * Uses a javax.xml.parsers.DocumentBuilder to build a org.w3c.dom.Document corresponding to the
 * XML-string provided. Uses the DOM/SAX implementation.
 * 
 * 
 * @author anton
 *
 */
public class MessageParser {

	/*
	 * TAKEN FROM:
	 * https://stackoverflow.com/questions/3300839/get-a-nodes-inner-xml-as-
	 * string-in-java-dom
	 * 
	 * With slight modification by @anton
	 */
	/**
	 * Static method for extracting the inner XML data as a pure string 
	 * from a DOM Document Node.
	 * 
	 * @param node - The node to extract the XML from
	 * @param html - Are we reading pure HTML? (false for XML)
	 * @return A string containing the XML representation of the Node
	 */
	public static String getInnerXML(Node node, boolean html) {
		DOMImplementationLS implementationLS = (DOMImplementationLS) node.getOwnerDocument().getImplementation()
				.getFeature("LS", "3.0");
		LSSerializer lsSerializer = implementationLS.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("xml-declaration", false);
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {	
			// Only accept tags that we can handle
			Node child = node.getChildNodes().item(i);
			//Main.DEBUG("Node: " + node.getNodeName() + " with child " + child.getNodeName());
			
			if (node.getNodeName().equalsIgnoreCase("text") && (child.getNodeName().equalsIgnoreCase("fetstil")
					|| child.getNodeName().equalsIgnoreCase("kursiv")
					|| child.getNodeName().equalsIgnoreCase("#text"))) {
				sb.append(lsSerializer.writeToString(child));
			} else if (node.getNodeName().equalsIgnoreCase("request")) {
				sb.append(lsSerializer.writeToString(child));
			} else if (html) {
				sb.append(lsSerializer.writeToString(child));
			} else {
				// System.out.println("Recieved unknown tag: " +
				// child.getNodeName());
			}
			// System.out.println(lsSerializer.writeToString(node.getChildNodes().item(i)));
		}
		String retString = sb.toString();
		if (!html) {
			// retString.substring(retString.indexOf(">"),
			// retString.length()-1-10);
		} else {
			retString = retString.trim();
		}
		return retString;

	}
	
	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder documentBuilder = null;
	@SuppressWarnings("unused")
	private String fileName = "/src/pack/ExampleMessage.xml";

	private Message.MessageBuilder messageBuilder;

	/**
	 * Instansiates the parser with a documentbuilder.
	 */
	public MessageParser() {
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	/**
	 *  Recursive building of a message by parsing all subnodes and extracting 
	 *  the relevant tag data. 
	 * @param nodes - The nodelist containing the nodes to be parsed
	 */
	private void buildMessageFromXML(NodeList nodes) {

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			// Öppnande message-tag
			if (node.getNodeName().equalsIgnoreCase("message")) {
				if (node.getAttributes().getNamedItem("sender") != null) {
					messageBuilder.setMessageSender(node.getAttributes().getNamedItem("sender").getNodeValue());
				} else {
					// messageBuilder.setMessageSender("null");
				}
			}
			// Text-tag, extrahera färg, annars välj svart
			else if (node.getNodeName().equalsIgnoreCase("text")) {
				if ((node.getAttributes().getNamedItem("color") != null)
						&& node.getAttributes().getNamedItem("color").getNodeValue().matches("[#][A-F0-9]{6}")) {

					messageBuilder.setTextColor(node.getAttributes().getNamedItem("color").getNodeValue());
				} else {
					messageBuilder.setTextColor("#000000");
				}
				messageBuilder.setText(getInnerXML(node, false));
				// System.out.println(getInnerXML(node));
			}
			// Request-tag
			else if (node.getNodeName().equalsIgnoreCase("request")) {
				if (node.getAttributes().getNamedItem("reply") != null
						&& node.getAttributes().getNamedItem("reply").getNodeValue().matches("([y][e][s]|[n][o])")) {
					String reply = node.getAttributes().getNamedItem("reply").getNodeValue();
					messageBuilder.setRequestAnswer((reply.equalsIgnoreCase("yes") ? true : false));

				}
				messageBuilder.setRequestText(getInnerXML(node, false));
			}
			// Disconnect-tag
			else if (node.getNodeName().equalsIgnoreCase("disconnect")) {
				messageBuilder.disconnect();
			}

			if (node.hasChildNodes()) {
				buildMessageFromXML(node.getChildNodes());
			}
		}
	}

	/**
	 * Converts a HTML-string to a Message (used when sending a message on the extracted HTML from
	 * the JTextArea) by parsing it!
	 * 
	 * @param parser - The HTMLParser which have parsed the HTML string that contains the message
	 * @return A message containg the data the parser did
	 */
	public Message convertHTMLtoMessage(HTMLParser parser) {
		messageBuilder = new Message.MessageBuilder();
		messageBuilder.setMessageSender(Main.CURRENT_CHAT_NAME);
		if (parser.getFontNode().hasAttributes()) {
			if (parser.getFontNode().getAttributes().getNamedItem("color") != null) {
				messageBuilder.setTextColor(parser.getFontNode().getAttributes().getNamedItem("color").getNodeValue());
			}
		}
		if (parser.getBodyContent() != null) {
			String bodyContent = parser.getBodyContent();
			// Main.DEBUG(text);
			if (bodyContent.startsWith("<body>")) {
				bodyContent = bodyContent.substring(6);
			}
			if (bodyContent.endsWith("</body>")) {
				bodyContent = bodyContent.substring(0, bodyContent.length() - 7);
			}
			String text = bodyContent;
			// Main.DEBUG(text);

			// STYLE IMPLEMENTATION
			text = text.replaceAll("<b>", "<fetstil>");
			text = text.replaceAll("</b>", "</fetstil>");
			text = text.replaceAll("<i>", "<kursiv>");
			text = text.replaceAll("</i>", "</kursiv>");

			messageBuilder.setText(text);
		}
		return new Message(messageBuilder);

	}

	/**
	 * Overloaded function. Same functionality as parseInputStream(String), but parses a file instead.
	 * 
	 * @param bfs
	 * @return
	 */
	public Message parseInputStream(File bfs) {
		Document doc = null;
		try {
			doc = documentBuilder.parse(bfs);
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException e) {
			System.err.println("Recieved badly formatted message: ");
			JOptionPane.showMessageDialog(MainGUI.getInstance(), "Recieved a broken message from:\n", "Broken message",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		NodeList nodes = doc.getChildNodes();
		messageBuilder = new Message.MessageBuilder();
		buildMessageFromXML(nodes);
		Message m = new Message(messageBuilder);
		return m;

	}

	/**
	 * Parses the provided String to a Message
	 *  
	 * @param inputString - The string to parse
	 * @return A Message object containing the data from the string.
	 */
	public Message parseInputStream(String inputString) {
		Document doc = null;
		try {
			doc = documentBuilder.parse(new InputSource(new StringReader(inputString)));
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException e) {
			System.err.println("Recieved badly formatted message: " + inputString);
			JOptionPane.showMessageDialog(MainGUI.getInstance(), "Recieved a broken message from:\n" + inputString,
					"Broken message", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		NodeList nodes = doc.getChildNodes();
		messageBuilder = new Message.MessageBuilder();
		buildMessageFromXML(nodes);
		Message m = new Message(messageBuilder);
		return m;

	}

}
