package pack;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * @author anton
 * Main class of MVC. Initializes the objects and holds the main JFrame
 */
public class MyFrame extends JFrame {

	// Flag for logging positions or not
	public static Boolean LOG = false;

	public static void main(String[] args) {
		new MyFrame();
	}

	public MyFrame() {
		super("Brownsk rörelse");
		// Super JFrame calls

		// MVC objects
		Model model = new Model(15000);
		View view = new View(model);
		Controller controller = new Controller(model, view);

		// Mac specific calls for UI
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// BorderLayout for JFrame and standard Swing calls
		setLayout(new BorderLayout());
		getContentPane().add(view, BorderLayout.NORTH);
		getContentPane().add(controller, BorderLayout.SOUTH);
		pack();

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
}
