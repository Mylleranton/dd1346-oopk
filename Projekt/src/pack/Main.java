package pack;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

	protected static String CURRENT_CHAT_NAME = "Server";
	protected static int CURRENT_PORT = 6666;
	protected static boolean outgoingConnectionEnabled = false;
	protected static final boolean DEBUG_FLAG = true;

	private ServerSocket serverSocket;

	public static class MainHolder {
		private static final Main INSTANCE = new Main();
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MainGUI mainGUI_instance = MainGUI.getInstance();

		// MessageParser p = new MessageParser();
		// System.out.println(p.getParsedMessage().getMessage());
	}

	/**
	 * Utility used for displaying DEBUG messages. Prints verbose iff debug_flag = true.
	 * @param msg - The string to be printed
	 */
	public static void DEBUG(String msg) {
		if (DEBUG_FLAG) {
			System.out.println("DEBUG: " + msg);
		}
	}

	/**
	 * Called upon server startup. Should run on independent thread, as it will indefinitly listen for new Socket connection
	 * and handle them appropriatly.
	 * SHOULD NOT RUN ON SWING THREAD
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
		} finally {
			outgoingConnectionEnabled = false;
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Failed to close server: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 * Called upon serverstart to listen and handle incoming connections
	 * SHOULD NOT RUN ON SWING THREAD
	 * 
	 * @throws IOException - forward from serversocket.accept()
	 */
	private void waitForConnections() throws IOException {
		Main.DEBUG("Waiting for connections...");
		// Accept incoming connections
		Socket inConn = serverSocket.accept();
		System.out.println("Incoming connection from " + inConn.getInetAddress().getHostAddress());
		
		Object[] options = { "Private", "Multi-part" };
		// Ask server if private or multi-part chat
		int response = -1;
		while ((response != JOptionPane.YES_OPTION) && (response != JOptionPane.NO_OPTION)) {
			response = JOptionPane.showOptionDialog(MainGUI.getInstance(),
					"Incoming connection detected from " + inConn.getInetAddress().getHostName()
							+ ".\nDo you want to start a private chat or a multi-part chat?",
					"Incoming connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
					options[0]);
		}
		if (response == JOptionPane.YES_OPTION) {
			// Add to private chat
			createNewChat(inConn);
		} else {
			// Add to multi-part chat
			addToMultiChat(inConn);
		}

	}
	/**
	 * Holder for the singleton instance of Main
	 * @return
	 */
	public static Main getInstance() {
		return MainHolder.INSTANCE;
	}

	/**
	 * Starts a new chat with the ip and port provided 
	 * as a client
	 * SHOULD NOT RUN ON SWING THREAD
	 *
	 * @param ip
	 *            - The IP address to connect with
	 * @param port
	 *            - The port to connect to
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
	 * Starts a new chat window with the socket provided
	 * 
	 * @param conn
	 *            - The socket to start a new chat with
	 */
	public void createNewChat(Socket conn) {
		Main.DEBUG("Creating a new chat...");
		ChatThread chatThread = new ChatThread(
				conn.getInetAddress() == null ? "default" : conn.getInetAddress().getHostAddress());
		MainGUI.getInstance().getChats().add(chatThread);

		Main.DEBUG("ChatThread "+ conn.getInetAddress().getHostAddress() +" created. Current chatlist has " + MainGUI.getInstance().getChats().size() + " chats.");
		
		ClientThread clientThread = new ClientThread(conn, chatThread);
		clientThread.start();
		

	}

	/**
	 * Adds the provided Socket to a multi-part chat
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
		} 
		else if (MainGUI.getInstance().getChats().size() == 1) {
			// One existing chat
			Main.DEBUG("There existed one current chat. Adding to that one.");
			ChatThread chatThread = MainGUI.getInstance().getChats().get(0);
			
			ClientThread clientThread = new ClientThread(conn, chatThread);
			clientThread.start();
		} 
		else {
			// Find multi-part chat
			Main.DEBUG("There existed multiple chats. User choice if all current chats are private.");
			ChatThread multiChat = null;
			
			for (ChatThread thread : MainGUI.getInstance().getChats()) {
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
			
			if(multiChat == null) {
				System.err.println("Could not find chosen multi-chat. Reverting to private chat mode");
				createNewChat(conn);
				return;
			} else {
				ClientThread clientThread = new ClientThread(conn, multiChat);
				clientThread.start();
			}

		}

	}

}
