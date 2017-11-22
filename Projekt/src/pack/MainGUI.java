package pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class MainGUI extends JFrame {
	
	public static int WIDTH = 800;
	public static int HEIGHT = 550;
	
	private JTabbedPane chatPanel;
	private JPanel buttonPanel;
	private JPanel optionPanel;
	private ArrayList<ChatThread> chats = new ArrayList<ChatThread>();
	
	
	public static class MainGUIHolder {
		private static final MainGUI INSTANCE = new MainGUI();
	}
	
	private MainGUI() {
		
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Layout, och en knappanel samt en chatpanel
		setLayout(new GridBagLayout());
				
		chatPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		chatPanel.setPreferredSize(new Dimension((int) (0.6*WIDTH), HEIGHT));
		
		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension((int) (0.4*WIDTH), (int) (0.6*HEIGHT)));
		buttonPanel.setLayout(new GridBagLayout());
		
		
		optionPanel = new JPanel();
		optionPanel.setPreferredSize(new Dimension((int) (0.4*WIDTH), (int) (0.4*HEIGHT)));
		optionPanel.setBorder(BorderFactory.createLineBorder(new Color(184,207,229), 2));
		optionPanel.setLayout(new GridBagLayout());
		optionPanel.setBackground(Color.orange);
		
		setupButtonPanel();
		HACKACHATPANEL("1");
		HACKACHATPANEL("2");

		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		add(buttonPanel, c);
		
		c.gridy = 1;
		add(optionPanel, c);
		
		c.gridx = 1; c.gridy = 0;
		c.gridheight = 2;
		add(chatPanel, c);
		
		chatPanel.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				if (chatPanel.getSelectedIndex() >= 0) {
					ChatThread newChat = chats.get(chatPanel.getSelectedIndex());
					MainGUI.getInstance().setOptionPanel(newChat.getChatPanelGUI().getOptionPane());
				}
				
			}
			
		});
		
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
		
		JLabel portLabelName = new JLabel("Port: ");
		JTextField portTextField = new JTextField(new Integer(Main.CURRENT_PORT).toString());
		JButton portChangeButton = new JButton("Ändra");
		
		portTextField.setEditable(false);
		portTextField.setEnabled(true);
		
		portChangeButton.setEnabled(true);
		portChangeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (portTextField.getText().matches("[1-9]\\d{2,5}")) {
					portTextField.setForeground(Color.BLACK);
					if (!Main.outgoingConnectionEnabled) {
						if(portTextField.isEditable()) {
							portChangeButton.setText("Ändra");
							portTextField.setEditable(false);
							Main.CURRENT_PORT = Integer.parseInt(portTextField.getText());
							
						} else {
							portChangeButton.setText("Klar");
							portTextField.setEditable(true);
						}
					}
				}
				else {
					portTextField.setForeground(Color.RED);
				}
				
			}
			
		});
		
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(5,5,5,5);
		c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(ipLabelName, c);
		
		c.gridx = 1; c.gridy = 0;
		buttonPanel.add(ipLabelValue, c);
		
		c.gridx = 0; c.gridy = 2;
		buttonPanel.add(portLabelName, c);
	
		c.gridx = 0; c.gridy = 3;
		buttonPanel.add(portChangeButton, c);
		
		c.gridx = 1;
		buttonPanel.add(portTextField, c);
		
		// Separator
		cSep.fill = GridBagConstraints.HORIZONTAL;
		cSep.gridy = 4; cSep.gridwidth = 2;
		cSep.insets = new Insets(5,0,5,0);
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
		
		c.gridx = 0; c.gridy = 5;
		buttonPanel.add(nameLabelName, c);
		c.gridx = 1; c.gridy = 6;
		buttonPanel.add(nameTextField, c);
		c.gridx = 0;
		buttonPanel.add(nameChangeButton, c);
		
		cSep.gridy = 7;
		buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL),cSep);
		
		// Connect buttons and labels
		JLabel connectLabel = new JLabel("Anslutningsparametrar");
		JButton connectButton = new JButton("Anslut");
		
		JLabel ipLabel = new JLabel("IP-address: ");
		JLabel portLabel = new JLabel("Portnummer: ");

		JTextField connectIpField = new JTextField(25);
		JTextField connectPortField = new JTextField(25);
		
		
		c.gridx = 0; c.gridwidth = 2;
		c.gridy = 8;
		buttonPanel.add(connectLabel, c);
		
		c.gridwidth = 1;
		c.gridx = 0; c.gridy = 9;
		buttonPanel.add(ipLabel, c);
		
		c.gridx = 1; c.gridy = 9;
		buttonPanel.add(connectIpField, c);
		
		c.gridx = 0; c.gridy = 10;
		buttonPanel.add(portLabel, c);
		
		c.gridx = 1; c.gridy = 10;
		buttonPanel.add(connectPortField, c);
		
		c.gridx = 0; c.gridy = 11;
		buttonPanel.add(connectButton, c);
		
		c.gridy = 12; c.weighty = 1;
		c.insets = new Insets(0,0,0,0);
		buttonPanel.add(new JLabel(), c);
		
	}
	
	public void HACKACHATPANEL(String in) {
		ChatThread t = new ChatThread(in);
		chats.add(t);
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
	/**
	 * Called on creation of a ChatThread
	 * @param gui
	 */
	public void addChatPanel(ChatPanelGUI gui){
		chatPanel.add(gui.getName(), gui);
	}
	
	public void setOptionPanel(JPanel optionPanel) {
		this.optionPanel.removeAll();
		this.optionPanel.validate();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		
		this.optionPanel.add(optionPanel, c);
		optionPanel.setVisible(true);
		Main.DEBUG("OptionPane changed with tab changed");
	}
	
	public void removeChatPanel(ChatThread chat) {
		System.out.println("Removing tab " + chat.getChatPanelGUI().getName());
		chats.remove(chat);
		chatPanel.remove(chat.getChatPanelGUI());
		if (chatPanel.getSelectedComponent() != null) {
			ChatPanelGUI gui = (ChatPanelGUI) chatPanel.getSelectedComponent();
			setOptionPanel(gui.getOptionPane());
		} else {
			setOptionPanel(new JPanel());
		}
	}
	
	
	public static MainGUI getInstance() {
		return MainGUIHolder.INSTANCE;
	}

}
