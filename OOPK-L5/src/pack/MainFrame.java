package pack;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Runnable class that holds the top level container for the webbrowser, and upon initialization,
 *  starts the browser and sets up all nessecary things.
 * 
 * @author anton
 *
 */
public class MainFrame extends JFrame {

	/**
	 * Width of the main window
	 */
	public static final int MAIN_WIDTH = 1000;
	/**
	 * Heigth of the main window
	 */
	public static final int MAIN_HEIGHT = 800;

	/**
	 * Runs the webbrowser
	 * @param args not used
	 */
	public static void main(String[] args) {
		new MainFrame();
	}
	
	/**
	 * Sets up all container panes and registers controller
	 * Also sets up the top level container
	 */
	public MainFrame() {
		// Mbar and Cpane references and a controller for them
		MenuBar mbar = new MenuBar();
		ContentPane cpane = new ContentPane();
		EventController controller = new EventController(mbar, cpane, this);

		// Set title and dimensions
		setTitle("Webbläsare");
		setPreferredSize(new Dimension(MAIN_WIDTH, MAIN_HEIGHT));

		// Mac specific calls for UI
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Add Menubar and ContentPane
		getContentPane().add(mbar, BorderLayout.PAGE_START);
		getContentPane().add(cpane, BorderLayout.CENTER);
		pack();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Set default webpage
		controller.loadWebpage("http://google.com");
		controller.buttonActivated = true;

	}

	/**
	 * Display connectivity issues in a JOptionPane
	 * 
	 * @param text
	 *            The error text to be displayed
	 */
	public void errorPane(String text) {
		String msg = "Detta beror mest troligt på en dålig ansluting eller felaktig webbaddress.";
		JOptionPane.showMessageDialog(this, msg + "\n" + text, "Anslutningen kunde inte upprättas",
				JOptionPane.ERROR_MESSAGE);
	}

}
