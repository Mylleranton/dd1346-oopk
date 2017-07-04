package pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;

import button.MyButton;

public class MainC1 {
	
	private static int NUMBER_OF_BUTTONS;
	public static void main(String[] args) {
		try {
			NUMBER_OF_BUTTONS = Integer.parseInt(args[0]);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		JFrame jframe = new JFrame("Laboration 2 Del C");
		JPanel jpanel = new JPanel(new FlowLayout());
		jframe.add(jpanel);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// jframe.add(mybutton);
		MyButton[] buttons = new MyButton[NUMBER_OF_BUTTONS];
		for(int i = 0; i < NUMBER_OF_BUTTONS; i++){
			buttons[i] = new MyButton(
					randomColor(),
					randomColor(),
					randomString(),
					randomString());
			jpanel.add(buttons[i]);
		}
		
		jframe.pack();
		jframe.setVisible(true);
		
		
	}
	private static Color randomColor(){
		int r = (int) (255*Math.random());
		int b = (int) (255*Math.random());
		int g = (int) (255*Math.random());
		return new Color(r,b,g);
	}
	private static String randomString(){
		return UUID.randomUUID().toString();
	}

}
