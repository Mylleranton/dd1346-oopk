package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import parsing.Message;
import parsing.MessageParser;

/**
 * A clientthread is a thread that will initialize on a socket and listen for incoming messages.
 * It is the Client-part to a ChatPanel, and must be connected to a chat.
 * 
 * 
 * @author anton
 *
 */
public class ClientThread extends Thread {

	private Socket socket;
	private String name = "";
	private String ID;
	private BufferedWriter buffWriter;
	private BufferedReader buffReader;
	private ChatPanel chatPanel;

	/**
	 * 
	 * @param socket - The socket to open communications on
	 * @param chatPanel - The ChatPanel to connect with
	 */
	public ClientThread(Socket socket, ChatPanel chatPanel) {
		assert (socket != null) : "Socket cannot be null";
		assert (chatPanel != null) : "ChatPanel cannot be null";

		this.chatPanel = chatPanel;
		this.socket = socket;

	}

	/**
	 * Sends the provided message to the client
	 * @param message
	 */
	public void sendMessage(Message message) {
		try {
			buffWriter.write(message.getMessage());
			buffWriter.flush();
			Main.DEBUG("SENT: " + message.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (NullPointerException e2) {
			System.err.println(e2.getMessage());
		} 
	}

	/**
	 * Handles a recieved message. If the sender has a new displayName, we call chatPanel.onNameUpdate() which updates the UI,
	 * we then display the recieved message and if it contains a disconnec-tag, we disconnect.
	 * 
	 * We also relay the recieved message if the chatPanel have more than one user (-> we are a server), but without disconnect-tags.
	 * @param message
	 */
	public void recieveMessage(Message message) {
		if(message == null) {
			System.out.println("Cannot handle null message");
			return;
		}
		System.out.println("Recieved message: " + message.getMessage());
		if (!getDisplayName().equalsIgnoreCase(message.getSender()) && !message.getSender().equalsIgnoreCase("")) {
			if (!message.getSender().startsWith("SYSTEM")) {
				name = message.getSender();
				chatPanel.onNameUpdate();
			}
		}

		chatPanel.getChatPanelGUI().displayMessage(message);

		if (message.disconnect()) {
			// Do not relay the disconnect-tag!
			chatPanel.disconnectClient(this);
			if (chatPanel.getClients().size() > 0) {
				Message newMessage = new Message(new Message.MessageBuilder().setMessageSender(message.getSender())
						.setText(message.getMessageText()).setTextColor(message.getTextColor()));
				chatPanel.sendMessageToAllButOne(getID(), newMessage);
			}
			
		} else {
			if (chatPanel.getClients().size() > 0) {
				chatPanel.sendMessageToAllButOne(getID(), message);
			}
		}
	}

	/**
	 * Nicer method to finalize(). Closes all streams and sockets pending client disconnection
	 */
	public void endConnection() {
		try {
			buffWriter.flush();
			buffWriter.close();
			buffWriter = null;
			buffReader.close();
			buffReader = null;
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts a thread and initializes the communication sockets. Will wait indefinitely for packets
	 */
	@Override
	public void run() {
		try {
			buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Failed to setup streams in ClientThread");
			e.printStackTrace();
		}
		ID = socket.getInetAddress().getHostAddress();
		int i = 1;
		for (String s : chatPanel.getClientIDs()) {
			if (s.equalsIgnoreCase(ID)) {
				i++;
			}
		}
		if (i > 1) {
			ID = ID.concat("(" + i + ")");
		}
		Main.DEBUG("Created ClientThread with ID " + ID);
		// Add to the chats client-list
		chatPanel.addClientThread(this);
		runClientThread();
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
		socket.close();
		buffReader.close();
		buffWriter.close();
	}
	
	/**
	 * The loop waiting for incoming packets from connected clients. Starts with the thread.
	 */
	private void runClientThread() {
		String errorMsg = "";
		while (((buffReader != null) && (buffWriter != null)) && (socket != null)) {
			// Main.DEBUG("Entering loop to listen for messages");
			try {
				StringBuilder sb = new StringBuilder();
				int newChar;
				while ((newChar = buffReader.read()) != -1) {
					// Main.DEBUG("Inner loop run, buffered reader has read a
					// char: " + (char) newChar);
					sb.append((char) newChar);

					// Check if we have recieved a complete message
					if (sb.length() > 8) {
						if (sb.substring(0, 8).equalsIgnoreCase("<message")) {
							if (sb.length() > 18) {
								if (sb.substring(sb.length() - 10).equalsIgnoreCase("</message>")) {

									// Main.DEBUG("RECIEVED: " + sb.toString());
									if ((sb.toString() != null) && !sb.toString().equalsIgnoreCase(" ")) {
										Message msg = new MessageParser().parseInputStream(sb.toString());
										recieveMessage(msg);
										if (msg.disconnect()) {
											errorMsg = "Recieved disconnect message.";
										}
									}
									break;
								}
							}
						}
					}
				}

			} catch (SocketException e) {
				errorMsg = "Socket closed.";
				System.out.println(errorMsg);
				break;
			} catch (IOException e) {
				errorMsg = "I/O: Could not read line. Streams closed.";
				System.out.println(errorMsg);
				break;
			}
		}
		displayEndOfChatMessage(errorMsg);
	}

	/**
	 * Displays a JOPtionPane with the provided message at the end of a connection
	 * @param msg
	 */
	private void displayEndOfChatMessage(String msg) {
		if (!chatPanel.allClientsDisconnected) {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(MainGUI.getInstance(),
						"The connection to " + getDisplayName() + " has been terminated.\n" + msg, "Disconnected",
						JOptionPane.INFORMATION_MESSAGE);
			});
		}
		
	}

	/**
	 * returns the unique ID of the clientthread
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Returns the displayname of the thread, which depending on if any name have been provided,
	 * can either be a user specified name or the thread ID (default)
	 * @return
	 */
	public String getDisplayName() {
		if (name.equalsIgnoreCase("")) {
			return ID;
		}
		return name;
	}
}
