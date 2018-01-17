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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author anton
 *
 *         This class holds the main JFrame and acts as container for all chats
 *         available, represended by one ChatPanelGUI.
 *
 *         Singleton.
 */
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

	/**
	 * Initialize graphics
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

		chatPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		chatPanel.setPreferredSize(new Dimension((int) (0.6 * WIDTH), HEIGHT));

		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension((int) (0.4 * WIDTH), (int) (0.6 * HEIGHT)));
		buttonPanel.setLayout(new GridBagLayout());

		optionPanel = new JPanel();
		optionPanel.setPreferredSize(new Dimension((int) (0.4 * WIDTH), (int) (0.4 * HEIGHT)));
		optionPanel.setBorder(BorderFactory.createLineBorder(new Color(184, 207, 229), 2));
		optionPanel.setLayout(new GridBagLayout());
		optionPanel.setBackground(Color.orange);

		setupButtonPanel();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		add(buttonPanel, c);

		c.gridy = 1;
		add(optionPanel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		add(chatPanel, c);

		// Make sure that the OptionPanel changes with the tabs of the chatpanel
		chatPanel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (chatPanel.getSelectedIndex() >= 0 && chats.size() > 0) {
					System.out.println(chats.size());
					ChatThread newChat = chats.get(chatPanel.getSelectedIndex());
					MainGUI.getInstance().setOptionPanel(newChat.getChatPanelGUI().getOptionPane());
				}

			}

		});

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

		JLabel portLabelName = new JLabel("Port: ");
		JTextField portTextField = new JTextField(new Integer(Main.CURRENT_PORT).toString());
		JButton portChangeButton = new JButton("Ändra");
		JButton startServerButton = new JButton("Starta server");

		portTextField.setEditable(false);
		portTextField.setEnabled(true);

		portChangeButton.setEnabled(true);
		portChangeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (portTextField.getText().matches("[1-9]\\d{2,5}")) {
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

			}

		});

		startServerButton.setEnabled(true);
		startServerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Main.getInstance().startServer();
					}

				}).start();

				portChangeButton.setEnabled(false);
				startServerButton.setEnabled(false);
			}

		});

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(ipLabelName, c);

		c.gridx = 1;
		c.gridy = 0;
		buttonPanel.add(ipLabelValue, c);

		c.gridx = 0;
		c.gridy = 2;
		buttonPanel.add(portLabelName, c);

		c.gridx = 0;
		c.gridy = 3;
		buttonPanel.add(portChangeButton, c);

		c.gridx = 1;
		buttonPanel.add(portTextField, c);

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 4;
		buttonPanel.add(startServerButton, c);
		c.gridwidth = 1;

		// Separator
		cSep.fill = GridBagConstraints.HORIZONTAL;
		cSep.gridy = 5;
		cSep.gridwidth = 2;
		cSep.insets = new Insets(5, 0, 5, 0);
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

		connectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!connectIpField.getText().matches("([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})")) {
					connectIpField.setText("");
					JOptionPane.showMessageDialog(MainGUI.getInstance(),
							"IP-address is not valid. Must have the format XXX.XXX.XXX.XXX", "Invalid IP",
							JOptionPane.ERROR_MESSAGE);
				} else if (!connectPortField.getText().matches("[1-9]\\d{2,5}")) {
					connectPortField.setText("");
					JOptionPane.showMessageDialog(MainGUI.getInstance(),
							"Port is not valid. Must be in range 100-99999", "Invalid Port", JOptionPane.ERROR_MESSAGE);
				} else {
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
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
							progressBar.setVisible(false);
							connectButton.setEnabled(true);
						}

					};
					worker.execute();
					Timer timer = new Timer((60 * 1000), new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (!worker.isDone()) {
								System.out.println("Connection timeout...");
								JOptionPane.showMessageDialog(MainGUI.getInstance(), "Outgoing connection timed out.",
										"Connection Time-out", JOptionPane.INFORMATION_MESSAGE);
								progressBar.setVisible(false);
								connectButton.setEnabled(true);
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
		buttonPanel.add(connectButton, c);

		c.gridx = 1;
		buttonPanel.add(progressBar, c);

		c.gridy = 13;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
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
	 * Adds a ChatPanelGUI to the server. Called on creation of a ChatThread.
	 * 
	 * @param gui
	 *            - the ChatPanelGUI instance to be added
	 */
	public void addChatPanel(ChatPanelGUI gui) {
		chatPanel.add(gui.getName(), gui);
	}

	/**
	 * Removes a chat panel without ending the internet connectivity from the
	 * server
	 * 
	 * @param chat
	 *            - the chatthread to remove
	 */
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

	/**
	 * Sets the current optionPanel when switching chats
	 * 
	 * @param optionPanel
	 *            - the optionpanel to set active
	 */
	public void setOptionPanel(JPanel optionPanel) {
		this.optionPanel.removeAll();
		this.optionPanel.validate();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;

		this.optionPanel.add(optionPanel, c);
		optionPanel.setVisible(true);
		//Main.DEBUG("Tab changed");
	}

	/**
	 * Returns the names of the current chatthreads
	 * 
	 * @return
	 */
	public String[] getChatNames() {
		ArrayList<String> strs = new ArrayList<String>();
		for (ChatThread th : chats) {
			strs.add(th.getChatPanelGUI().getName());
		}
		return (String[]) strs.toArray();
	}

	/**
	 * Returns the ChatThread with provided name if it exists, otherwise it
	 * returns null.
	 * 
	 * @param name
	 * @return
	 */
	public ChatThread getChatByName(String name) {
		ChatThread retThread = null;
		for (ChatThread th : chats) {
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

	public ArrayList<ChatThread> getChats() {
		return chats;
	}

	public JTabbedPane getTabbedPane() {
		return chatPanel;
	}

}
