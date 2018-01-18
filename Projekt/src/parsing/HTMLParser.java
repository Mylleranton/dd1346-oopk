package parsing;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HTMLParser {

	private String htmlString;
	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder documentBuilder = null;
	private Document document = null;

	public HTMLParser(String html) throws SAXException {
		htmlString = html;
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
			document = documentBuilder.parse(new InputSource(new StringReader(htmlString)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		document.getDocumentElement().normalize();
	}

	public void printStructure() {
		printStructure(document.getChildNodes());
	}

	private void printStructure(NodeList nl) {
		for (int i = 0; i < nl.getLength(); i++) {
			System.out.println(nl.item(i).getNodeName());
			printStructure(nl.item(i).getChildNodes());
		}
	}

	public String getBodyContent() {
		// getBodyNode().normalize();
		return MessageParser.getInnerXML(getFontNode(), true);
	}

	public String getPlainBodyContent() {
		return getBodyNode().getTextContent().trim();
	}

	public Node getBodyNode() {
		NodeList rootNode = document.getDocumentElement().getElementsByTagName("body");
		if (rootNode.getLength() == 0) {
			return null;
		}
		return rootNode.item(0);
	}

	public Element buildNode(String input) throws SAXException {
		Document localDoc = null;
		try {
			localDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(input)));
			return localDoc.getDocumentElement();
		} catch (IOException | ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Replaces the current <body>-node with the provided newNode
	 *
	 * @param newNode
	 *            The new body node.
	 */
	public void replaceBodyNode(Node newNode) {
		Node parentNode = getBodyNode().getParentNode();
		document.adoptNode(newNode);
		parentNode.replaceChild(newNode, getBodyNode());
	}

	/**
	 * Returns this document represented as a string
	 */
	public String getHTMLText() {
		return "<html>\n" + MessageParser.getInnerXML(document.getDocumentElement(), true) + "\n</html>";
	}

	/**
	 * Appends the formatted HTML line to the end of this documents body section
	 * enclosed within
	 * <p>
	 * -tags
	 */
	public void appendToBodyNode(String line) {
		if (!line.startsWith("<p>")) {
			line = "<p>" + line;
		}
		if (!line.endsWith("</p>")) {
			line = line + "</p>";
		}
		Element node = null;
		try {
			node = buildNode(line);
		} catch (SAXException e) {
			e.printStackTrace();
		}

		Node newNode = document.importNode(node, true);
		getBodyNode().appendChild(newNode);

	}

	public Node getFontNode() {
		NodeList rootNodes = document.getDocumentElement().getElementsByTagName("font");
		if (rootNodes.getLength() == 0) {
			try {
				Element fontNode = buildNode("<font></font>");
				document.adoptNode(fontNode);
				Node parentNode = getBodyNode().getParentNode();
				fontNode.appendChild(getBodyNode());
				parentNode.appendChild(fontNode);
				return fontNode;
			} catch (SAXException e) {
				e.printStackTrace();
			}

			return null;
		}
		return rootNodes.item(0);
	}
}
