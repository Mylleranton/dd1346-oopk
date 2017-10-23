package pack;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MyFrame extends JFrame{

	public static void main(String[] args){
		new MyFrame();
		
	}
	public MyFrame(){
		Model model = new Model(10);
		View view = new View(model);
		Controller controller = new Controller(model, view);
		try {
		    UIManager.setLookAndFeel(
		            UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		this.setTitle("Brownsk rörelse");
		
		this.setLayout(new BorderLayout());
		this.add(view, BorderLayout.NORTH);
		this.getContentPane().setBackground(Color.BLACK);
		this.add(controller, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
}
