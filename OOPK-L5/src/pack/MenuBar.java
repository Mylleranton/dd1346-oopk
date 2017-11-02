package pack;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The Panel for the MenuBar with back/forward buttons, address field and the history button
 * 
 * @author anton
 *
 */
public class MenuBar extends JPanel {

	/**
	 * Height of the MenuBar
	 */
	private static final int MENU_HEIGHT = 50;
	/**
	 * Width/Heigth of the MenuBar buttons
	 */
	private static final int BUTTON_HEIGHT = 35;
	/**
	 * Width of the MenuBar AddressField
	 */
	private static final int ADDRESS_WIDTH = 700;

	/**
	 * Browser Address Field
	 */
	public JTextField addressField;
	/**
	 * Browser Forward Button
	 */
	public JButton fwdButton;
	/**
	 * Browser Backwards Button
	 */
	public JButton backButton;
	/**
	 * Browser History Button. Toggles the History pane visibility
	 */
	public JButton historyButton;

	/**
	 * Initiates the Menubar with layout and components.
	 */
	public MenuBar() {
		super();

		// Layout and dimensions
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		setLayout(layout);
		setPreferredSize(new Dimension(MainFrame.MAIN_WIDTH, MENU_HEIGHT));

		// Addressfield
		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(0, 10, 0, 10);
		addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(ADDRESS_WIDTH, 20));
		addressField.setText("http://");
		this.add(addressField, c);

		// Forward Button
		c.gridx = 2;
		c.gridy = 0;
		fwdButton = new JButton("->");
		fwdButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
		fwdButton.setEnabled(true);
		this.add(fwdButton, c);

		// Back Button
		c.gridx = 1;
		c.gridy = 0;
		backButton = new JButton("<-");
		backButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
		backButton.setEnabled(true);
		this.add(backButton, c);

		// History Button
		c.gridx = 0;
		c.gridy = 0;
		historyButton = new JButton("Historik");
		historyButton.setPreferredSize(new Dimension(3 * BUTTON_HEIGHT, BUTTON_HEIGHT));
		historyButton.setEnabled(true);
		this.add(historyButton, c);

		setVisible(true);
	}

	/**
	 * Set the textfield of the addressbar to the provided one.
	 * 
	 * @param url
	 *            the url that should be set as the text
	 */
	public void setURLText(String url) {
		addressField.setText(url);
	}

	/**
	 * Add ActionListener for all components within the MenuBar 
	 * 
	 * @param listener
	 *            the listener that should be added
	 */
	public void setActionListener(ActionListener listener) {
		backButton.addActionListener(listener);
		fwdButton.addActionListener(listener);
		addressField.addActionListener(listener);
		historyButton.addActionListener(listener);
	}
}
