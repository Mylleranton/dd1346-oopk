package pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class MainDirTree extends JFrame implements ActionListener {

	private static final long serialVersionUID = 592140375690835306L;

	public MainDirTree() {
		try {
			checkXML(checkScanner);
		} catch (final Exception e1) {
			e1.printStackTrace();
			System.exit(0);
		}

		final Container c = getContentPane();
		// *** Build the tree and a mouse listener to handle clicks
		root = readNode();

		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		final MouseListener ml = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (box.isSelected()) {
					showDetails(tree.getPathForLocation(e.getX(), e.getY()));
				}
			}
		};
		tree.addMouseListener(ml);

		// *** panel the JFrame to hold controls and the tree
		controls = new JPanel();
		box = new JCheckBox(showString);
		init(); // ** set colors, fonts, etc. and add buttons
		c.add(controls, BorderLayout.NORTH);
		c.add(tree, BorderLayout.CENTER);
		setVisible(true); // ** display the framed window
	}

	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();
		if (cmd.equals(closeString)) {
			dispose();
		}
	}

	private void init() {
		tree.setFont(new Font("Dialog", Font.BOLD, 12));
		controls.add(box);
		addButton(closeString);
		controls.setBackground(Color.lightGray);
		controls.setLayout(new FlowLayout());
		setSize(400, 400);
	}

	private void addButton(String n) {
		final JButton b = new JButton(n);
		b.setFont(new Font("Dialog", Font.BOLD, 12));
		b.addActionListener(this);
		controls.add(b);
	}

	// Shows details about the bio
	private void showDetails(TreePath p) {
		if ((p == null) || !(p.getLastPathComponent() instanceof MyNode)) {
			return;
		}
		final MyNode n = (MyNode) p.getLastPathComponent();
		String msg = n.getNodeLevel() + ": " + n.getUserObject() + "\nBeskrivning: " + n.getText().trim()
				+ ",\nmen allt som är " + n.getNodeLevel() + "";
		for (final String s : getParentNames(n)) {
			msg += " är ".concat(s);
		}

		JOptionPane.showMessageDialog(this, msg);
	}

	// Returns a nodes parents in order
	private String[] getParentNames(MyNode n) {
		String names = "";
		MyNode parent;

		while ((parent = (MyNode) n.getParent()) != null) {
			names += parent.getUserObject();
			names += " ";
			n = parent;
		}

		return names.split(" ");
	}

	// Reads XML and creates nodes
	private MyNode readNode(String... s) {
		// Initialization
		String line = "";
		String openTag = "";
		MyNode retNode = new MyNode();

		// If no input s, go with next line in scanner
		if (scanner.hasNextLine() || (s.length > 0)) {
			if (s.length == 0) {
				line = scanner.nextLine();
			} else {
				line = s[0];
			}

			// Remove metadata
			if (line.startsWith("<?xml")) {
				line = scanner.nextLine();
			}

			// If line defines a new tag
			if (line.startsWith("<") && !line.startsWith("</")) {
				// Make the corresponding node and keep track of the creation
				retNode = makeNode(line);
				openTag = retNode.getNodeLevel();
			}
			// While children exist
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();

				// If th endtag matches the opentag, return
				if (line.startsWith("</".concat(openTag))) {
					System.out.println("Avslutar DFS, stötte på: " + "</".concat(openTag));
					return retNode;
				}
				// else, make the new tag a child
				else if (line.startsWith("<") && !line.startsWith("</")) {
					retNode.add(readNode(line));
				}

			}

		}

		return retNode;
	}

	// Helper fcn for making the nodes (handles formatting etc)
	private MyNode makeNode(String line) {
		final MyNode node = new MyNode();
		String text = "";
		String name = "";

		// Scanner for the line
		final Scanner inScanner = new Scanner(line);
		boolean encounteredStop = false;

		while (inScanner.hasNext()) {
			final String word = inScanner.next();

			// First word -> node.level
			if (word.startsWith("<")) {
				node.setNodeLevel(word.substring(1));
			}
			// second/third word -> node.name
			else if (!encounteredStop) {
				final int startIndex = word.indexOf("\"");
				final int endIndex = word.lastIndexOf("\"");

				// if-else block to handle whitespaces in name
				if (startIndex != endIndex) {
					node.setUserObject(word.substring(startIndex + 1, endIndex));
					encounteredStop = true;
				} else if (word.startsWith("namn=\"")) {
					name += word.substring(startIndex + 1).concat(" ");
				} else if (word.endsWith("\">")) {
					name += word.substring(0, word.length() - 2);
					node.setUserObject(name);
					encounteredStop = true;
				}
			}
			// the remainder -> description
			else {
				text += word.concat(" ");
			}
		}
		node.setText(text);
		System.out.println("Gjort nod " + node.getUserObject());
		return node;
	}

	// Parses and checks XML file for inconsistend open/close-tags
	private void checkXML(Scanner s) throws Exception {
		Stack<String> balance = new Stack<String>();
		final String MSG = "Parsed XML-file badly formatted.";
		int counter;
		
		while (s.hasNextLine()) {
			counter = 0;
			final String line = s.nextLine();

			
			// Mismatching <>/tags
			if (!line.startsWith("<") || !line.contains(">")) {
				throw new Exception(MSG);
			} else if (line.startsWith("<?xml")) {
				continue;
			}

			else if (line.startsWith("</")) {
				final String openTag = balance.peek();
				if (!openTag.equals(line.replaceAll("/", ""))) {
					throw new Exception(MSG);
				} else {
					balance.pop();
				}
			} else {
				final String openTag = (line.split(" ")[0]).concat(">");
				balance.push(openTag);
			}
			
			// Mismatching ""
			for (char c : line.toCharArray()) {
				if (c == '"'){
					counter++;
				}
			}
			if ((counter % 2) != 0) {
				throw new Exception(MSG);
			}
		}
		if (!balance.isEmpty()) {
			throw new Exception(MSG);
		}
		

	}

	public static void main(String[] args) {
		try {
			scanner = new Scanner(new File(System.getProperty("user.dir") + fileName));
			checkScanner = new Scanner(new File(System.getProperty("user.dir") + fileName));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		new MainDirTree();
	}

	private final JCheckBox box;
	private final JTree tree;
	private final DefaultMutableTreeNode root;
	private final DefaultTreeModel treeModel;
	private final JPanel controls;
	private static final String closeString = " Close ";
	private static final String showString = " Show Details ";
	private static final String fileName = "/src/pack/Liv.xml";
	private static Scanner scanner;
	private static Scanner checkScanner;
}
