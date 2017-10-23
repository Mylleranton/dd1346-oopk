package pack.e;

/**
 * Applet version av MainE.java, ingen kodskillnad.
 * 
 */
import javax.swing.JApplet;
import javax.swing.JFrame;

import button.MyButton;

public class Main_applet extends JApplet {

	private static final long serialVersionUID = 1L;
	private static JFrame jframe;
	private static MyButton mybutton;
	public void init() {
		jframe = new JFrame("Laboration 2");
		mybutton = new MyButton();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		jframe.add(mybutton);
		jframe.pack();
		jframe.setVisible(true);
		
		
	}

}
