package pack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author anton
 *
 *         This class holds the main JFrame and acts as container for all chats
 *         available, represended by one ChatPanelGUI.
 *
 *         Singleton, with static access through MainGUI.getInstance().
 */
public class MainGUI extends JFrame {

	/**
	 *  Width of the final GUI
	 */
	public static int WIDTH = 800;
	/**
	 * Height of the final GUI
	 */
	public static int HEIGHT = 540;

	private JTabbedPane chatPanel;
	private JPanel buttonPanel;
	private JPanel optionPanel;
	private ArrayList<ChatPanel> chats = new ArrayList<ChatPanel>();

	/**
	 * Holder class for the singleton instance of MainGUI
	 * @author anton
	 *
	 */
	public static class MainGUIHolder {
		private static final MainGUI INSTANCE = new MainGUI();
	}

	/**
	 * Initialize graphics and register all listeners
	 */
	private MainGUI() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Layout, och en knappanel samt en chatpanel
		setLayout(new GridBagLayout());

		// CHATPANEL (which holds an instant of ChatPanelGUI)
		chatPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		chatPanel.setPreferredSize(new Dimension((int) (0.6 * WIDTH), HEIGHT));
		chatPanel.setMaximumSize(new Dimension((int) (0.7 * WIDTH), HEIGHT));
		chatPanel.setMinimumSize(new Dimension((int) (0.6 * WIDTH), HEIGHT));

		
		// BUTTONPANEL for settings
		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension((int) (0.4 * WIDTH), (int) (0.65 * HEIGHT)));
		buttonPanel.setMinimumSize(new Dimension((int) (0.4 * WIDTH), (int) (0.65 * HEIGHT)));
		buttonPanel.setMaximumSize(new Dimension((int) (0.4 * WIDTH), (int) (0.7 * HEIGHT)));
		buttonPanel.setLayout(new GridBagLayout());
		TitledBorder b = BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 2, new Color(184, 207, 229)), "ANSLUTNINGSINSTÄLLNINGAR",
				TitledBorder.RIGHT, TitledBorder.TOP, null, Color.GRAY);
		buttonPanel.setBorder(b);

		// OPTIONPANEL (which holds an instant of ChatPanelGUI.getOptionPane())
		optionPanel = new JPanel();
		optionPanel.setPreferredSize(new Dimension((int) (0.4 * MainGUI.WIDTH), (int) (0.3 * MainGUI.HEIGHT)));
		optionPanel.setMinimumSize(new Dimension((int) (0.4 * MainGUI.WIDTH), (int) (0.3 * MainGUI.HEIGHT)));
		optionPanel.setMaximumSize(new Dimension((int) (0.4 * MainGUI.WIDTH), (int) (0.30 * MainGUI.HEIGHT)));
		optionPanel.setLayout(new GridBagLayout());
		TitledBorder b2 = BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 2, new Color(184, 207, 229)), "CHATTINSTÄLLNINGAR",
				TitledBorder.RIGHT, TitledBorder.TOP, null, Color.GRAY);
		optionPanel.setBorder(b2);

		// Setup the buttonpanel
		setupButtonPanel();

		// Add the buttonpanel
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0.3;
		c.weighty = 0.6;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		add(buttonPanel, c);

		// Add the optionpanel
		c.weightx = 0.3;
		c.weighty = 1;
		c.gridy = 1;
		add(optionPanel, c);

		// Add the chatpanel
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0, 0, 0, 0);
		add(chatPanel, c);

		// Make sure that the OptionPanel changes with the tabs of the chatpanel
		chatPanel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if ((chatPanel.getSelectedIndex() >= 0) && (chats.size() > 0)) {
					ChatPanel newChat = chats.get(chatPanel.getSelectedIndex());
					MainGUI.getInstance().setOptionPanel(newChat.getChatPanelGUI().getOptionPane());
				}
			}

		});

		// pack and show
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Sets up the button-panel with user-changable program parameters
	 */
	private void setupButtonPanel() {
		GridBagConstraints c = new GridBagConstraints();

		GridBagConstraints cSep = new GridBagConstraints();

		// IP Address lables
		JLabel ipLabelValue = new JLabel(getIp().getHostAddress());
		JLabel ipLabelName = new JLabel("Nuvarande IP: ");

		// Port info and changing buttons
		JLabel portLabelName = new JLabel("Port: ");
		JTextField portTextField = new JTextField(new Integer(Main.CURRENT_PORT).toString());
		JButton portChangeButton = new JButton("Ändra");
		JButton startServerButton = new JButton("Starta server");

		JLabel serverStatus = new JLabel();
		serverStatus.setText("Server AV");
		serverStatus.setForeground(Color.RED);
		serverStatus.setHorizontalAlignment(SwingConstants.CENTER);
		serverStatus.setVerticalAlignment(SwingConstants.CENTER);

		portTextField.setEditable(false);
		portTextField.setEnabled(true);

		portChangeButton.setEnabled(true);
		// Only accept good ports when server not running.
		portChangeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (portTextField.getText().matches("[1-9]\\d{2,5}")) {
					if (Integer.parseInt(portTextField.getText()) < 65535 && Integer.parseInt(portTextField.getText()) > 1023 ) {
						portTextField.setForeground(Color.BLACK);
						if (!Main.outgoingConnectionEnabled) {
							if (portTextField.isEditable()) {
								portChangeButton.setText("Ändra");
								portTextField.setEditable(false);
								Main.CURRENT_PORT = Integer.parseInt(portTextField.getText());

							} else {
								portChangeButton.setText("Klar");
								portTextField.setEditable(true);
							}
						}
					} else {
						portTextField.setForeground(Color.RED);
					}
				} else {
					portTextField.setForeground(Color.RED);
				}

			}
		});

		startServerButton.setEnabled(true);
		// Constantly running timer to check server status and update UI thereafter
		Timer timer = new Timer(1000 * 5, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Main.outgoingConnectionEnabled) {
					startServerButton.setEnabled(true);
					startServerButton.setText("Starta server");
					serverStatus.setText("Server AV");
					serverStatus.setForeground(Color.RED);
				} else {
					startServerButton.setEnabled(true);
					startServerButton.setText("Stäng ned server");
					serverStatus.setText("Server PÅ");
					serverStatus.setForeground(Color.GREEN);
				}
			}

		});
		
		// Starting server button. No server -> Start one. Server running -> stop it and release UI control
		startServerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Main.outgoingConnectionEnabled) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Main.getInstance().startServer();
						}
					}).start();
					timer.start();
					portChangeButton.setEnabled(false);
					portTextField.setEditable(false);
					startServerButton.setEnabled(false);

				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Main.getInstance().stopServer();
						}
					}).start();
					portChangeButton.setEnabled(true);
					startServerButton.setEnabled(false);
					portTextField.setEditable(false);

				}

			}

		});

		//////////////////////////////////
		// Add all buttons/labels to UI //
		//////////////////////////////////
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(15, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(ipLabelName, c);

		c.gridx = 1;
		c.gridy = 0;
		buttonPanel.add(ipLabelValue, c);

		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 5, 5, 5);
		buttonPanel.add(portLabelName, c);

		c.gridx = 0;
		c.gridy = 3;
		buttonPanel.add(portChangeButton, c);

		c.gridx = 1;
		buttonPanel.add(portTextField, c);

		c.gridx = 0;
		c.gridy = 4;
		buttonPanel.add(startServerButton, c);

		c.gridx = 1;

		buttonPanel.add(serverStatus, c);

		// Separator
		cSep.fill = GridBagConstraints.HORIZONTAL;
		cSep.gridy = 5;
		cSep.gridwidth = 2;
		cSep.insets = new Insets(5, 10, 5, 10);
		buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL), cSep);

		// Showing name labels and buttons
		JLabel nameLabelName = new JLabel("Visningsnamn: ");
		JTextField nameTextField = new JTextField(Main.CURRENT_CHAT_NAME);
		JButton nameChangeButton = new JButton("Ändra");

		nameTextField.setEditable(false);
		nameTextField.setEnabled(true);
		nameChangeButton.setEnabled(true);
		nameChangeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (nameTextField.isEditable()) {
					nameChangeButton.setText("Ändra");
					nameTextField.setEditable(false);
					Main.CURRENT_CHAT_NAME = nameTextField.getText();

				} else {
					nameChangeButton.setText("Klar");
					nameTextField.setEditable(true);
				}
			}
		});

		c.gridx = 0;
		c.gridy = 6;
		buttonPanel.add(nameLabelName, c);
		c.gridx = 1;
		c.gridy = 7;
		buttonPanel.add(nameTextField, c);
		c.gridx = 0;
		buttonPanel.add(nameChangeButton, c);

		cSep.gridy = 8;
		buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL), cSep);

		// Connect buttons and labels
		JLabel connectLabel = new JLabel("Anslutningsparametrar");
		JButton connectButton = new JButton("Anslut");

		JLabel ipLabel = new JLabel("IP-address: ");
		JLabel portLabel = new JLabel("Portnummer: ");

		JTextField connectIpField = new JTextField(25);
		JTextField connectPortField = new JTextField(25);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);

		// Only allow connections if valid input is provided
		connectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Wrong IP
				if (!connectIpField.getText().matches("([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})")) {
					connectIpField.setText("");
					JOptionPane.showMessageDialog(MainGUI.getInstance(),
							"IP-address is not valid. Must have the format XXX.XXX.XXX.XXX", "Invalid IP",
							JOptionPane.ERROR_MESSAGE);
				} 
				// Wrong port
				else if (!connectPortField.getText().matches("[1-9]\\d{2,5}")) {
					connectPortField.setText("");
					JOptionPane.showMessageDialog(MainGUI.getInstance(),
							"Port is not valid. Must be in range 1024-65535", "Invalid Port", JOptionPane.ERROR_MESSAGE);
				} 
				// Try connecting through a SwingWorker thread that calls Main
				else {
					progressBar.setVisible(true);
					connectButton.setEnabled(false);

					SwingWorker<Socket, Void> worker = new SwingWorker<Socket, Void>() {

						@Override
						protected Socket doInBackground() throws Exception {
							return Main.getInstance().connectToHost(connectIpField.getText(),
									connectPortField.getText());
						}

						@Override
						protected void done() {
							try {
								if (get() != null) {
									Main.getInstance().createNewChat(get());
								} else {
									System.out.println("Connection error");
									JOptionPane.showMessageDialog(MainGUI.getInstance(),
											"Connection Error, please see logs.", "Connection Error",
											JOptionPane.ERROR_MESSAGE);
									progressBar.setVisible(false);
									connectButton.setEnabled(true);
								}
							} catch (InterruptedException e) {
								// Do nothing
							} catch (ExecutionException e1) {
								e1.printStackTrace();
							} finally {
								progressBar.setVisible(false);
								connectButton.setEnabled(true);
							}
						}

					};
					worker.execute();
					
					// Time-out timer
					Timer timer = new Timer((60 * 1000), new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (!worker.isDone()) {
								System.out.println("Connection timeout...");
								JOptionPane.showMessageDialog(MainGUI.getInstance(), "Outgoing connection timed out.",
										"Connection Time-out", JOptionPane.INFORMATION_MESSAGE);
								progressBar.setVisible(false);
								connectButton.setEnabled(true);
								worker.cancel(true);
							}
						}
					});
					timer.setRepeats(false);
					timer.start();

				}

			}

		});

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 9;
		buttonPanel.add(connectLabel, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 10;
		buttonPanel.add(ipLabel, c);

		c.gridx = 1;
		c.gridy = 10;
		buttonPanel.add(connectIpField, c);

		c.gridx = 0;
		c.gridy = 11;
		buttonPanel.add(portLabel, c);

		c.gridx = 1;
		c.gridy = 11;
		buttonPanel.add(connectPortField, c);

		c.gridx = 0;
		c.gridy = 12;
		c.insets = new Insets(5, 5, 10, 5);
		buttonPanel.add(connectButton, c);

		c.gridx = 1;
		buttonPanel.add(progressBar, c);

		c.gridy = 13;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 10, 0);
		buttonPanel.add(new JLabel(), c);

	}

	/**
	 * Return the current local IP of the computer
	 */
	private InetAddress getIp() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Adds a ChatPanelGUI to the server. Called on creation of a ChatPanel.
	 * Makes sure that the appropriate optionpane is displayed as well.
	 *
	 * @param gui
	 *            - the ChatPanelGUI instance to be added
	 */
	public void addChatPanel(ChatPanelGUI gui) {
		chatPanel.add(gui.getName(), gui);
		setOptionPanel(gui.getOptionPane());
		chatPanel.setSelectedIndex(chatPanel.getSelectedIndex() + 1 < chatPanel.getTabCount() ? chatPanel.getSelectedIndex() + 1 : chatPanel.getSelectedIndex());
	}

	/**
	 * Removes a chat panel without ending the internet connectivity from the
	 * server
	 *
	 * @param chat
	 *            - the ChatPanel to remove
	 */
	public void removeChatPanel(ChatPanel chat) {
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

	/**
	 * Sets the current optionPanel when switching chats
	 *
	 * @param optionPanel
	 *            - the optionpanel to set active
	 */
	public void setOptionPanel(JPanel newPanel) {

		optionPanel.removeAll();
		optionPanel.repaint();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;

		optionPanel.add(newPanel, c);
		optionPanel.revalidate();
		newPanel.setVisible(true);

	}

	/**
	 * Returns the names of the current ChatPanels
	 *
	 * @return
	 */
	public String[] getChatNames() {
		String[] strs = new String[chats.size()];
		for (int i = 0; i < chats.size(); i++) {
			strs[i] = (chats.get(i).getChatPanelGUI().getName());
		}
		return strs;
	}

	/**
	 * Returns the ChatPanel with provided name if it exists, otherwise it
	 * returns null.
	 *
	 * @param name
	 * @return
	 */
	public ChatPanel getChatByName(String name) {
		ChatPanel retThread = null;
		for (ChatPanel th : chats) {
			if (th.getChatPanelGUI().getName().equalsIgnoreCase(name)) {
				retThread = th;
			}
		}
		return retThread;
	}

	/**
	 * Singleton instance of MainGUI
	 *
	 * @return the instance
	 */
	public static MainGUI getInstance() {
		return MainGUIHolder.INSTANCE;
	}
	

	/**
	 * 
	 * @return an arraylist containing all active ChatPanels
	 */
	public ArrayList<ChatPanel> getChats() {
		return chats;
	}

	/**
	 * 
	 * @return the JTabbedPane that hold all ChatPanelGUIs
	 */
	public JTabbedPane getTabbedPane() {
		return chatPanel;
	}

}
