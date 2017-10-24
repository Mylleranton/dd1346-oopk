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
public class MenuBar extends JPanel implements ActionListener {

	private static final int MENU_HEIGHT = 100;
	
	private static final int ADDRESS_WIDTH = 700;
	
	private JTextField addressField;
	private JButton fwdButton;
	private JButton backButton;
	
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
		addressField.addActionListener(this);
		addressField.setText("http://");
		this.add(addressField, c);
		
		c.gridx = 1; c.gridy = 0;
		fwdButton = new JButton("->");
		fwdButton.setPreferredSize(new Dimension(MENU_HEIGHT/2,MENU_HEIGHT/2));
		fwdButton.setEnabled(true);
		fwdButton.addActionListener(this);
		this.add(fwdButton, c);
		
		c.gridx = 0; c.gridy = 0;
		backButton = new JButton("<-");
		backButton.setPreferredSize(new Dimension(MENU_HEIGHT/2,MENU_HEIGHT/2));
		backButton.setEnabled(true);
		backButton.addActionListener(this);
		this.add(backButton, c);

		
		this.setVisible(true);
	}
	
	
	public void setURLText(String url) {
		addressField.setText(url);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addressField) {
			System.out.println("Addressfältet uppdaterades");
		}
		else if (e.getSource() == fwdButton) {
			System.out.println("FWD button klickades");
			
		} else if (e.getSource() == backButton) {
			System.out.println("BACK button klickades");
		}
		
				
	}
	

}
