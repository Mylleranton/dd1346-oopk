package parsing;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

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

public class MessageParser {

	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder documentBuilder = null;
	@SuppressWarnings("unused")
	private String fileName = "/src/pack/ExampleMessage.xml";
	private Message.MessageBuilder messageBuilder;

	public MessageParser() {
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

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
			// text = text.replaceAll("<b>", "<fetstil>");
			// text = text.replaceAll("</b>", "</fetstil>");
			// text = text.replaceAll("<i>", "<kursiv>");
			// text = text.replaceAll("</i>", "</kursiv>");

			messageBuilder.setText(text);
		}
		return new Message(messageBuilder);

	}

	@SuppressWarnings("unused")
	public Message parseInputStream(File bfs) {
		Document doc = null;
		try {
			doc = documentBuilder.parse(bfs);
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		NodeList nodes = doc.getChildNodes();
		messageBuilder = new Message.MessageBuilder();
		buildMessageFromXML(nodes);
		Message m = new Message(messageBuilder);
		return m;

	}

	public Message parseInputStream(String inputString) {
		Document doc = null;
		try {
			doc = documentBuilder.parse(new InputSource(new StringReader(inputString)));
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException e) {
			System.err.println("Recieved badly formatted message: " + inputString);
		}

		NodeList nodes = doc.getChildNodes();
		messageBuilder = new Message.MessageBuilder();
		buildMessageFromXML(nodes);
		Message m = new Message(messageBuilder);
		return m;

	}

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
			// Disconnect-tag
			else if (node.getNodeName().equalsIgnoreCase("disconnect")) {
				messageBuilder.disconnect();
			}

			if (node.hasChildNodes()) {
				buildMessageFromXML(node.getChildNodes());
			}
		}
	}

	/*
	 * https://stackoverflow.com/questions/3300839/get-a-nodes-inner-xml-as-
	 * string-in-java-dom
	 */
	public static String getInnerXML(Node node, boolean html) {
		DOMImplementationLS implementationLS = (DOMImplementationLS) node.getOwnerDocument().getImplementation()
				.getFeature("LS", "3.0");
		LSSerializer lsSerializer = implementationLS.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("xml-declaration", false);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			sb.append(lsSerializer.writeToString(node.getChildNodes().item(i)));
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

}
