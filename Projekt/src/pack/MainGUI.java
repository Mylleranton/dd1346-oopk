package pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;

public class MainGUI extends JFrame {
	
	public static int WIDTH = 600;
	public static int HEIGHT = 400;
	
	private JTabbedPane chatPanel;
	private JPanel buttonPanel;
	
	public MainGUI() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Layout, och en knappanel samt en chatpanel
		setLayout(new BorderLayout());
				
		chatPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		chatPanel.setPreferredSize(new Dimension((int) (0.6*WIDTH), HEIGHT));
		chatPanel.setBackground(Color.ORANGE);
		
		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension((int) (0.4*WIDTH), HEIGHT));
		buttonPanel.setLayout(new GridBagLayout());
		
		
		setupButtonPanel();
		setupChatPanel();
		
		add(chatPanel, BorderLayout.EAST);
		
		add(buttonPanel, BorderLayout.WEST);
		
		pack();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void setupButtonPanel(){
		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraints cSep = new GridBagConstraints();

		
		// IP Address lables
		JLabel ipLabelValue = new JLabel(getIp().getHostAddress());
		JLabel ipLabelName = new JLabel("Nuvarande IP: ");
		
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(5,5,5,5);
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(ipLabelName, c);
		
		c.gridx = 1; c.gridy = 1;
		buttonPanel.add(ipLabelValue, c);
		
		// Separator
		cSep.fill = GridBagConstraints.BOTH;
		cSep.gridy = 2; cSep.gridwidth = 2;
		cSep.insets = new Insets(10,0,10,0);
		buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL),cSep);
		
		// Showing name labels and buttons
		JLabel nameLabelName = new JLabel("Visningsnamn: ");
		JTextField nameTextField = new JTextField(Main.CURRENT_CHAT_NAME);
		JButton nameChangeButton = new JButton("Ändra");
		
		nameTextField.setEditable(false);
		nameTextField.setEnabled(true);
		nameChangeButton.setEnabled(true);
		nameChangeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(nameTextField.isEditable()) {
					nameChangeButton.setText("Ändra");
					nameTextField.setEditable(false);
					Main.CURRENT_CHAT_NAME = nameTextField.getText();
					
				} else {
					nameChangeButton.setText("Klar");
					nameTextField.setEditable(true);
				}
			}
		});
		
		c.gridx = 0; c.gridy = 3;
		buttonPanel.add(nameLabelName, c);
		c.gridx = 1; c.gridy = 4;
		buttonPanel.add(nameTextField, c);
		c.gridx = 0;
		buttonPanel.add(nameChangeButton, c);
		
		cSep.gridy = 5;
		buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL),cSep);
		
		// Connect buttons and labels
		JLabel connectLabel = new JLabel("Anslutningsparametrar");
		JButton connectButton = new JButton("Anslut");
		
		JLabel ipLabel = new JLabel("IP-address: ");
		JLabel portLabel = new JLabel("Portnummer: ");

		JTextField connectIpField = new JTextField(25);
		JTextField connectPortField = new JTextField(25);
		
		
		c.gridx = 0; c.gridwidth = 2;
		c.gridy = 6;
		buttonPanel.add(connectLabel, c);
		
		c.gridwidth = 1;
		c.gridx = 0; c.gridy = 7;
		buttonPanel.add(ipLabel, c);
		
		c.gridx = 1; c.gridy = 7;
		buttonPanel.add(connectIpField, c);
		
		c.gridx = 0; c.gridy = 8;
		buttonPanel.add(portLabel, c);
		
		c.gridx = 1; c.gridy = 8;
		buttonPanel.add(connectPortField, c);
		
		c.gridx = 0; c.gridy = 9;
		buttonPanel.add(connectButton, c);
	}
	
	private void setupChatPanel(){
		// Add a chatpanel instance to the main layout
		chatPanel.add("Test", new ChatPanelGUI());

	}
	
	/*
	 * Return the current local IP of the computer
	 */
	private InetAddress getIp(){
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}
