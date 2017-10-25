/**
 * 
 */
package pack;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author anton
 *
 */
public class MenuBar extends JPanel{

	private static final int MENU_HEIGHT = 50;
	private static final int BUTTON_HEIGHT = 35;
	private static final int ADDRESS_WIDTH = 700;
	
	public JTextField addressField;
	public JButton fwdButton;
	public JButton backButton;
	
	public MenuBar(){
		super();
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		this.setLayout(layout);
		this.setPreferredSize(new Dimension(MainFrame.MAIN_WIDTH, MENU_HEIGHT));
		
		c.gridx = 2; c.gridy = 0; c.insets = new Insets(0,10,0,10);
		addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(ADDRESS_WIDTH,20));
		addressField.setText("http://");
		this.add(addressField, c);
		
		c.gridx = 1; c.gridy = 0;
		fwdButton = new JButton("->");
		fwdButton.setPreferredSize(new Dimension(BUTTON_HEIGHT,BUTTON_HEIGHT));
		fwdButton.setEnabled(true);
		this.add(fwdButton, c);
		
		c.gridx = 0; c.gridy = 0;
		backButton = new JButton("<-");
		backButton.setPreferredSize(new Dimension(BUTTON_HEIGHT,BUTTON_HEIGHT));
		backButton.setEnabled(true);
		this.add(backButton, c);

		
		this.setVisible(true);
	}
	
	
	public void setURLText(String url) {
		addressField.setText(url);
	}
	
	public void setActionListener(ActionListener listener) {
		this.backButton.addActionListener(listener);
		this.fwdButton.addActionListener(listener);
		this.addressField.addActionListener(listener);
	}
}
