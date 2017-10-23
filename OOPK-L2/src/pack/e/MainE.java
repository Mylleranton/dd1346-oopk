package pack.e;


import java.awt.event.ActionListener;

import javax.swing.JFrame;

import button.MyButton;

/**
 * 
 * @author anton
 * L2 main class file for E-task
 *
 */
public class MainE {

	public static void main(String[] args) {
		JFrame jframe = new JFrame("Laboration 2");
		// Instansiera min fabulösa knapp
		MyButton mybutton = new MyButton();
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Skapar och lägger till en knapp 
		jframe.add(mybutton);
		jframe.pack();
		jframe.setVisible(true);
		
		
	}

}
