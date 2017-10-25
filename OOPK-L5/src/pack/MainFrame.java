package pack;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MainFrame extends JFrame {

	public static final int MAIN_WIDTH = 1000;
	public static final int MAIN_HEIGHT = 800;
	
	
	public static void main(String[] args) {
		new MainFrame();
	}
	
	public MainFrame(){
		MenuBar mbar = new MenuBar();
		ContentPane cpane = new ContentPane();
		EventController controller = new EventController(mbar,cpane);
		
		
		this.setTitle("Webbläsare");
		this.setPreferredSize(new Dimension(MAIN_WIDTH, MAIN_HEIGHT));
		
		// Mac specific calls for UI
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getContentPane().add(mbar, BorderLayout.PAGE_START);
		this.getContentPane().add(cpane,BorderLayout.CENTER);
		this.pack();
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
		cpane.setWebpage("http://google.com");
		
	}

}
