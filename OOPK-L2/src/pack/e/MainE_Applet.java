package pack.e;

/**
 * Applet version av MainE.java, ingen kodskillnad.
 * Körs i AppletViewer med playknappen ovan
 */
import javax.swing.JApplet;
import javax.swing.JFrame;

import button.MyButton;

public class MainE_Applet extends JApplet {

	private JFrame jframe;
	private MyButton mybutton;
	public void init() {
		jframe = new JFrame("Laboration 2");
		mybutton = new MyButton();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		jframe.add(mybutton);
		jframe.pack();
		jframe.setVisible(true);
		
		
	}

}
