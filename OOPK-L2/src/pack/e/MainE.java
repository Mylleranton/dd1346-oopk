package pack.e;


import javax.swing.JFrame;

import button.MyButton;

public class MainE {

	public static void main(String[] args) {
		JFrame jframe = new JFrame("Laboration 2");
		MyButton mybutton = new MyButton();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Skapar och lägger till en knapp typ
		
		jframe.add(mybutton);
		jframe.pack();
		jframe.setVisible(true);
		
		
	}

}
