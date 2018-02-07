package pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import parsing.Message;
import parsing.MessageParser;

/**
 * Runnable main-class that initializes the MainGUI and serves as the backend
 * implementation of the MainGUI.
 * 
 * Singleton, with static instance access.
 * 
 * @author anton
 *
 */
public class Main {

	/**
	 * Holder class for the instance of Main
	 * 
	 * @author anton
	 *
	 */
	public static class MainHolder {
		private static final Main INSTANCE = new Main();
	}
	
	public static String CURRENT_CHAT_NAME = "Server";
	public static int CURRENT_PORT = 6666;
	public static boolean outgoingConnectionEnabled = false;

	public static final boolean DEBUG_FLAG = true;

	private ServerSocket serverSocket;

	/**
	 * Utility used for displaying DEBUG messages. Prints verbose iff debug_flag
	 * = true.
	 * 
	 * @param msg
	 *            - The string to be printed
	 */
	public static void DEBUG(String msg) {
		if (DEBUG_FLAG) {
			System.out.println("DEBUG: " + msg);
		}
	}

	/**
	 * Holder for the singleton instance of Main
	 * 
	 * @return
	 */
	public static Main getInstance() {
		return MainHolder.INSTANCE;
	}

	/**
	 * Main method. Holds reference to the MainGUI, that, when initiated,
	 * starts the GUI and program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MainGUI mainGUI_instance = MainGUI.getInstance();

	}

	/**
	 * Adds the provided Socket to the multi-part chat
	 *
	 * @param conn
	 *            - The socket to add
	 */
	public void addToMultiChat(Socket conn) {
		Main.DEBUG("Adding new client to multi-chat");

		if (MainGUI.getInstance().getChats().size() == 0) {
			// No existing chats
			Main.DEBUG("There existed no chats, creating a new one");
			createNewChat(conn);
		} else if (MainGUI.getInstance().getChats().size() == 1) {
			// One existing chat
			Main.DEBUG("There existed one current chat. Adding to that one.");
			ChatPanel chatPanel = MainGUI.getInstance().getChats().get(0);

			ClientThread clientThread = new ClientThread(conn, chatPanel);
			clientThread.start();
		} else {
			// Find multi-part chat
			Main.DEBUG("There existed multiple chats. User choice if all current chats are private.");
			ChatPanel multiChat = null;

			for (ChatPanel thread : MainGUI.getInstance().getChats()) {
				// If one chatthread has multiple users, add to that one
				if (thread.getClients().size() > 1) {
					Main.DEBUG("There existed one chat with multiple users. Adding to that one.");
					multiChat = thread;
					break;
				}
			}
			if (multiChat == null) {
				Main.DEBUG("There existed no chat with multiple users. Your choice");
				Object[] options = MainGUI.getInstance().getChatNames();
				Object answer = null;
				while (answer == null) {
					answer = JOptionPane.showInputDialog(MainGUI.getInstance(),
							"Choose which chat you would like to add the new connection to:", "Multi-chat",
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				}
				multiChat = MainGUI.getInstance().getChatByName((String) answer);

			}

			// If the selection for some reason failes, then we create a new
			// chat instead.
			if (multiChat == null) {
				System.err.println("Could not find chosen multi-chat. Reverting to private chat mode");
				createNewChat(conn);
				return;
			} else {
				ClientThread clientThread = new ClientThread(conn, multiChat);
				clientThread.start();
			}

		}

	}

	/**
	 * Connects to the provided host and returns the connection
	 *
	 * @param ip
	 *            - The IP address to connect with
	 * @param port
	 *            - The port to connect to
	 * @return The connection socket
	 */
	public Socket connectToHost(String ip, String port) {
		Socket connection = null;

		try {
			connection = new Socket(InetAddress.getByName(ip), Integer.parseUnsignedInt(port));
		} catch (UnknownHostException e) {
			System.err.println("The provided IP did not lead anywhere...: " + e.toString());

		} catch (IOException e) {
			System.err.println("I/O error on attemted outgoing connection: " + e.toString());

		} catch (NumberFormatException e) {
			System.err.println("Port has invalid format: " + port + ": " + e.toString());
		}

		System.out.println("Successfully connected to host " + ip + ":" + port);
		return connection;

	}
	
	/**
	 *  Same functionality as connectToHost except that this function
	 *  takes a requestText to send with the connection, and waits
	 *  for a response!
	 * @param conn
	 * @param requestText
	 */
	public void connectToUserWithRequest(Socket conn, String requestText) {
		Main.DEBUG("Creating a new chat and sending request message");
		ChatPanel chatPanel = new ChatPanel(
				conn.getInetAddress() == null ? "default" : conn.getInetAddress().getHostAddress());

		Main.DEBUG("ChatPanel " + conn.getInetAddress().getHostAddress() + " created. Current chatlist has "
				+ MainGUI.getInstance().getChats().size() + " chats.");

		ClientThread clientThread = new ClientThread(conn, chatPanel);
		clientThread.start();

		// Send request message once the clientThread has stated.
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(1 * 1000);

				Main.DEBUG("Sending request-message");
				Message requestMessage = new Message(
						new Message.MessageBuilder().setMessageSender(CURRENT_CHAT_NAME).setRequestText(requestText));
				clientThread.sendMessage(requestMessage);
				return null;
			}
		};
		worker.run();

	}

	/**
	 * Starts a new chat window with the socket provided
	 *
	 * @param conn
	 *            - The socket to start a new chat with
	 */
	public void createNewChat(Socket conn) {
		Main.DEBUG("Creating a new chat...");
		ChatPanel chatPanel = new ChatPanel(
				conn.getInetAddress() == null ? "default" : conn.getInetAddress().getHostAddress());

		Main.DEBUG("ChatPanel " + conn.getInetAddress().getHostAddress() + " created. Current chatlist has "
				+ MainGUI.getInstance().getChats().size() + " chats.");

		ClientThread clientThread = new ClientThread(conn, chatPanel);
		clientThread.start();
	}

	/**
	 * Called upon server startup. Should run on independent thread, as it will
	 * indefinitly listen for new Socket connection and handle them
	 * appropriatly. SHOULD NOT RUN ON SWING THREAD
	 */
	public void startServer() {
		try {
			serverSocket = new ServerSocket(CURRENT_PORT, 50);
			outgoingConnectionEnabled = true;
			while (true) {
				try {
					waitForConnections();

				} catch (EOFException eofe) {
					System.err.println("Server ended connection: " + eofe.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Server ended connection: " + e.getMessage());
			JOptionPane.showMessageDialog(MainGUI.getInstance(),
					"Failed to open a server on the provided port:\n" + e.getMessage(), "Could not open server",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			outgoingConnectionEnabled = false;
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Failed to close server: " + e.getMessage());
			} catch (NullPointerException e) {
				System.err.println("Failed to close server: " + e.getMessage());
			}
		}
	}

	/**
	 * Stops the running server (if running), and makes sure that no clients are
	 * disconnected unnessecarily
	 */
	public void stopServer() {
		if (MainGUI.getInstance().getChats().size() > 0) {
			int answer = JOptionPane.showConfirmDialog(MainGUI.getInstance(),
					"Det finns anslutna klienter till servern som körs.\n En nedstängning kommer leda till att samtliga kopplas från.\n Vill du fortsätta?",
					"Bekräfta avstängning", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				return;
			}
		}
		try {
			serverSocket.close();
			outgoingConnectionEnabled = false;
			for (ChatPanel chats : MainGUI.getInstance().getChats()) {
				chats.disconnectAll();
			}
		} catch (IOException e) {
			System.err.println("Failed to close server. " + e.getMessage());
		}
	}

	/**
	 *
	 * Called upon serverstart to listen and handle incoming connections SHOULD
	 * NOT RUN ON SWING THREAD
	 *
	 * @throws IOException
	 *             - forward from serversocket.accept()
	 */
	private void waitForConnections() throws IOException {
		Main.DEBUG("Waiting for connections...");
		// Accept incoming connections
		Socket inConn = serverSocket.accept();
		System.out.println("Incoming connection from " + inConn.getInetAddress().getHostAddress());

		// Start a new thread/connection to handle the incoming request
		ClientThread thread = new ClientThread(inConn);
		thread.start();

		// Check for answer every 1/2-second if we have not recieved one
		Timer timer = new Timer(1 * 500, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// As long as we have no answer, then we wait
				if (thread.approved != null) {
					if (thread.approved) {
						if (thread.simple) {
							thread.sendMessage(new Message(new Message.MessageBuilder().setMessageSender("SYSTEM")
									.setText("You're request was accepted!")));
						} else {
							thread.sendMessage(new Message(new Message.MessageBuilder().setMessageSender("SYSTEM")
									.setRequestAnswer(true).setRequestText("You're request was accepted!")));
						}
						((Timer) e.getSource()).stop();

						Object[] options = { "Private", "Multi-part" };
						// Ask server if private or multi-part chat
						int response = -1;
						while ((response != JOptionPane.YES_OPTION) && (response != JOptionPane.NO_OPTION)) {
							response = JOptionPane.showOptionDialog(MainGUI.getInstance(),
									"You accepted the connection from " + inConn.getInetAddress().getHostName()
											+ ".\nDo you want to start a private chat or a multi-part chat?",
									"Incoming connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
									null, options, options[0]);
						}
						if (response == JOptionPane.YES_OPTION) {
							// Add to private chat
							createNewChat(inConn);
						} else {
							// Add to multi-part chat
							addToMultiChat(inConn);
						}
					} else {
						if (thread.simple) {
							thread.sendMessage(new Message(new Message.MessageBuilder().setMessageSender("SYSTEM")
									.setText("You're request was DENIED!")));
						} else {
							thread.sendMessage(new Message(new Message.MessageBuilder().setMessageSender("SYSTEM")
									.setRequestAnswer(false).setRequestText("You're request was DENIED!")));
						}

						thread.endConnection();
						((Timer) e.getSource()).stop();
					}
				}

			}
		});
		timer.start();

		// After 45 s without user input, then we time-out.
		Timer timer_timeout = new Timer(45 * 1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (timer.isRunning()) {
					Main.DEBUG("Request timed out. Disconnecting.");
					thread.approved = false;
				}
			}
		});
		timer_timeout.setRepeats(false);
		timer_timeout.start();

	}

}
