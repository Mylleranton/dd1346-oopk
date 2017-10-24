package pack;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MyFrame extends JFrame{

	// Flag for logging positions or not
	public static Boolean LOG = false;
	
	public static void main(String[] args){
		new MyFrame();
	}
	
	public MyFrame(){
		super("Brownsk rörelse");
		// Super JFrame calls
		
		// MVC objects
		Model model = new Model(15000);
		View view = new View(model);
		Controller controller = new Controller(model, view);
		
		// Mac specific calls for UI 
		try {
		    UIManager.setLookAndFeel(
		            UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		// BorderLayout for JFrame and standard Swing calls
		this.setLayout(new BorderLayout());
		this.getContentPane().add(view, BorderLayout.NORTH);
		this.getContentPane().add(controller, BorderLayout.SOUTH);
		this.pack();

		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
}
