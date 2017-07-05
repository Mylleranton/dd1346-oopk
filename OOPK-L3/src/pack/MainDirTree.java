package pack;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;

public class MainDirTree extends JFrame implements ActionListener {

	private static final long serialVersionUID = 592140375690835306L;


	public MainDirTree() {
      Container c = getContentPane();
      //*** Build the tree and a mouse listener to handle clicks
      root = readNode();
      
      treeModel = new DefaultTreeModel( root );
      tree = new JTree( treeModel );
      MouseListener ml = 
        new MouseAdapter() {
          public void mouseClicked( MouseEvent e ) {
            if ( box.isSelected() )
              showDetails( tree.getPathForLocation( e.getX(), 
                                                    e.getY() ) );
          }
        };
      tree.addMouseListener( ml );

      //*** panel the JFrame to hold controls and the tree
      controls = new JPanel();
      box = new JCheckBox( showString );
      init(); //** set colors, fonts, etc. and add buttons
      c.add( controls, BorderLayout.NORTH );
      c.add( tree, BorderLayout.CENTER );   
      setVisible( true ); //** display the framed window
   } 

   public void actionPerformed( ActionEvent e ) {
      String cmd = e.getActionCommand();
      if ( cmd.equals( closeString ) )
        dispose();
   }

   private void init() {
      tree.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
      controls.add( box );
      addButton( closeString );
      controls.setBackground( Color.lightGray );
      controls.setLayout( new FlowLayout() );    
      setSize( 400, 400 );
   }

   private void addButton( String n ) {
      JButton b = new JButton( n );
      b.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
      b.addActionListener( this );
      controls.add( b );
   }
   
   private void showDetails( TreePath p ) {
      if ( p == null )
        return;
      File f = new File( p.getLastPathComponent().toString() );
      JOptionPane.showMessageDialog( this, f.getPath() + 
                                     "\n   " + 
                                     getAttributes( f ) );
   }

   private String getAttributes( File f ) {
      String t = "";
      if ( f.isDirectory() )
        t += "Directory";
      else
        t += "Nondirectory file";
      t += "\n   ";
      if ( !f.canRead() )
        t += "not ";
      t += "Readable\n   ";
      if ( !f.canWrite() )
        t += "not ";
      t += "Writeable\n  ";
      if ( !f.isDirectory() )
        t += "Size in bytes: " + f.length() + "\n   ";
      else {
        t += "Contains files: \n     ";
        String[ ] contents = f.list();
        for ( int i = 0; i < contents.length; i++ )
           t += contents[ i ] + ", ";
        t += "\n";
      } 
      return t;
   }
   
   
   // Reads XML and creates nodes
   private MyNode readNode(String... s) {
	   	// Initialization
	    String line = ""; 
		String openTag = "";
		MyNode retNode = new MyNode();
		
		// If no input s, go with next line in scanner
		if (scanner.hasNextLine() || s.length > 0) {
			if(s.length == 0) {
				line = scanner.nextLine();
			}
			else {
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
			while(scanner.hasNextLine()) {
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
	   MyNode node = new MyNode();
	   String text = "";
	   String name = "";
	   
	   // Scanner for the line
	   Scanner inScanner = new Scanner(line);
	   boolean encounteredStop = false;
	   
	   while (inScanner.hasNext()) {
		   String word = inScanner.next();
		   
		   // First word -> node.level
			if (word.startsWith("<")) {
				node.setNodeLevel(word.substring(1));
			}
			// second/third word -> node.name
			else if (!encounteredStop) {
				int startIndex = word.indexOf("\"");
				int endIndex = word.lastIndexOf("\"");
				
				// if-else block to handle whitespaces in name
				if (startIndex != endIndex) {
					node.setUserObject(word.substring(startIndex+1, endIndex));
					encounteredStop = true;
				}
				else if (word.startsWith("namn=\"")) {
					name += word.substring(startIndex+1).concat(" ");
				}
				else if (word.endsWith("\">")){
					name += word.substring(0, word.length()-2);
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

   public static void main( String[ ] args ) {
	   try {
			scanner = new Scanner(new File(System.getProperty("user.dir") + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	  
       new MainDirTree();
   }

   private JCheckBox box;
   private JTree tree;
   private DefaultMutableTreeNode root;
   private DefaultTreeModel treeModel;
   private JPanel controls;
   private static final String closeString = " Close ";
   private static final String showString = " Show Details ";
   private static final String fileName = "/src/pack/Liv.xml";
   private static Scanner scanner;
}
