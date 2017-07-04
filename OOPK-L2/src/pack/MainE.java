package pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import button.MyButton;

public class MainE {

	private static MyButton mybutton;
	public static void main(String[] args) {
		JFrame jframe = new JFrame("Laboration 2");
		MyButton mybutton = new MyButton();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		jframe.add(mybutton);
		jframe.pack();
		jframe.setVisible(true);
		
		
	}

}
