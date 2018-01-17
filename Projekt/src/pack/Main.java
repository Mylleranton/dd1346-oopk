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

	public static void DEBUG(String msg) {
		if (DEBUG_FLAG) {
			System.out.println("DEBUG: " + msg);
		}
	}

	/**
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
					Main.DEBUG("Server ended connection");
				}
			}
			// serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outgoingConnectionEnabled = false;
		}
	}

	/**
	 * SHOULD NOT RUN ON SWING THREAD
	 * 
	 * @throws IOException
	 */
	private void waitForConnections() throws IOException {
		Main.DEBUG("Waiting for connections...");
		Socket inConn = serverSocket.accept();
		Object[] options = { "Private", "Multi-part" };
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
			// TODO: Add to multi-part chat
			addToMultiChat(inConn);
		}

	}

	public static Main getInstance() {
		return MainHolder.INSTANCE;
	}

	/**
	 * Starts a new chat with the ip and port provided SHOULD NOT RUN ON SWING
	 * THREAD
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
			System.out.println("The provided IP did not lead anywhere...: " + e.toString());

		} catch (IOException e) {
			System.out.println("I/O error on attemted outgoing connection: " + e.toString());

		} catch (NumberFormatException e) {
			System.out.println("Port has invalid format: " + port + ": " + e.toString());
		}

		return connection;

	}

	/**
	 * Starts a new chat window with the socket provided
	 * 
	 * @param conn
	 *            - The socket to start a new chat with
	 */
	public void createNewChat(Socket conn) {
		Main.DEBUG("Creating a new chat");
		ChatThread chatThread = new ChatThread(
				conn.getInetAddress() == null ? "default" : conn.getInetAddress().getHostAddress());
		MainGUI.getInstance().getChats().add(chatThread);

		Main.DEBUG("ChatThread created. Current chatlist has " + MainGUI.getInstance().getChats().size() + " chats.");
		
		ClientThread clientThread = new ClientThread(conn, chatThread);
		clientThread.start();

		
		chatThread.addClientThread(clientThread);

		

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
		} else if (MainGUI.getInstance().getChats().size() == 1) {
			// One existing chat
			Main.DEBUG("There existed one current chat. Adding to that one.");
			ChatThread chatThread = MainGUI.getInstance().getChats().get(0);
			ClientThread clientThread = new ClientThread(conn, chatThread);
			clientThread.start();
			chatThread.addClientThread(clientThread);
		} else {
			// Find multi-part chat
			Main.DEBUG("There existed multiple chats. User choice if all current chats are private.");
			ChatThread multiChat = null;
			for (ChatThread thread : MainGUI.getInstance().getChats()) {
				// If one chatthread has multiple users, add to that one
				if (thread.getClients().size() > 1) {
					multiChat = thread;
				}
			}
			if (multiChat == null) {
				Object[] options = MainGUI.getInstance().getChatNames();
				String answer = null;
				while (answer != null) {
					answer = (String) JOptionPane.showInputDialog(MainGUI.getInstance(),
							"Choose which chat you would like to add the new connection to:", "Multi-chat",
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				}
				multiChat = MainGUI.getInstance().getChatByName(answer);
				assert (multiChat == null) : "ERROR: Could not find multi-chat";

				ClientThread clientThread = new ClientThread(conn, multiChat);
				clientThread.start();

				multiChat.addClientThread(clientThread);
			}

		}

	}

}
