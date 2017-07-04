package pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Main {

	
	private static JFrame jframe;
	private static MyButton mybutton;
	public static void main(String[] args) {
		jframe = new JFrame("Laboration 2");
		mybutton = new MyButton();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		jframe.add(mybutton);
		jframe.pack();
		jframe.setVisible(true);
		
		
	}

}
