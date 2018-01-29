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
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import parsing.Message;
import parsing.MessageParser;

/**
 * A clientthread is a thread that will initialize on a socket and listen for
 * incoming messages. It is the Client-part to a ChatPanel, and must be
 * connected to a chat.
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
	public Boolean approved = null;
	public boolean simple = true;

	private Timer timer;

	/**
	 * Constructor for initial contact with client before validation
	 * 
	 * @param socket
	 *            - The incoming socket
	 */
	public ClientThread(Socket socket) {
		assert (socket != null) : "Socket cannot be null";
		this.socket = socket;
	}

	/**
	 * 
	 * @param socket
	 *            - The socket to open communications on
	 * @param chatPanel
	 *            - The ChatPanel to connect with
	 */
	public ClientThread(Socket socket, ChatPanel chatPanel) {
		assert (socket != null) : "Socket cannot be null";
		assert (chatPanel != null) : "ChatPanel cannot be null";

		this.chatPanel = chatPanel;
		this.socket = socket;
		this.approved = true;

	}

	/**
	 * Try to accept request-tags!
	 */
	private void approveClient() {
		String errorMsg;
		while ((((buffReader != null) && (buffWriter != null)) && (socket != null)) && approved == null) {
			try {
				StringBuilder sb = new StringBuilder();
				int newChar;
				while ((newChar = buffReader.read()) != -1 && approved == null) {
					sb.append((char) newChar);

					// Main.DEBUG("RECIEVED CHARACTER: " + (char)(newChar));

					// Check if we have recieved a complete message
					if (sb.length() > 8) {
						if (sb.substring(0, 8).equalsIgnoreCase("<message")) {
							if (sb.length() > 18) {
								if (sb.substring(sb.length() - 10).equalsIgnoreCase("</message>")) {

									Main.DEBUG("RECIEVED_APPROVE: " + sb.toString());
									if ((sb.toString() != null) && !sb.toString().equalsIgnoreCase(" ")) {
										Message msg = new MessageParser().parseInputStream(sb.toString());

										// If we are waiting for a <request>
										if (msg.getRequestAnswer() == null) {
											if (msg.getRequestText() != null) {
												timer.stop();
												setConnectionPromptAnswer(msg, false);
												break;
											} else if (msg.getMessageText() != null || msg.getSender() != null) {
												// Simple client
												timer.stop();
												setConnectionPromptAnswer(msg, true);
												break;
											}
										}
										// We have sent and recieved an answer
										// to our request
										else {
											Main.DEBUG("HEJADKBJLHDÖIALNK");
										}

									}
									break;
								}
							}
						}
					}
				}

			} catch (SocketException e) {
				errorMsg = "Socket closed in approveClient.";
				System.out.println(errorMsg);
				e.printStackTrace();
				break;
			} catch (IOException e) {
				errorMsg = "I/O: Could not read line. Streams closed.";
				System.out.println(errorMsg);
				break;
			}
		}
	}

	/**
	 * Displays a JOPtionPane with the provided message at the end of a
	 * connection
	 * 
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
	 * Nicer method to finalize(). Closes all streams and sockets pending client
	 * disconnection
	 */
	public void endConnection() {
		try {
			Main.DEBUG("Closing connections to clientthread " + this.getDisplayName());
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

	@Override
	public void finalize() throws Throwable {
		super.finalize();
		socket.close();
		buffReader.close();
		buffWriter.close();
	}

	/**
	 * Returns the displayname of the thread, which depending on if any name
	 * have been provided, can either be a user specified name or the thread ID
	 * (default)
	 * 
	 * @return
	 */
	public String getDisplayName() {
		if (name.equalsIgnoreCase("")) {
			return ID;
		}
		return name;
	}

	/**
	 * returns the unique ID of the clientthread
	 * 
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Handles a recieved message. If the sender has a new displayName, we call
	 * chatPanel.onNameUpdate() which updates the UI, we then display the
	 * recieved message and if it contains a disconnec-tag, we disconnect.
	 * 
	 * We also relay the recieved message if the chatPanel have more than one
	 * user (-> we are a server), but without disconnect-tags.
	 * 
	 * @param message
	 */
	public void recieveMessage(Message message) {
		if (message == null) {
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

		if (message.getRequestAnswer() != null && !message.getRequestAnswer()) {
			chatPanel.disconnectClient(this);
		}
	}

	/**
	 * Starts a thread and initializes the communication sockets. Will wait
	 * indefinitely for packets
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

		if (this.approved != null) {
			// If client have been approved
			if (this.approved) {
				Main.DEBUG("Client have been approved. Starting regular chat");
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
			// If client have not been approved
			else {
				Main.DEBUG("Client have been denied approval.");
			}
		} else {
			Main.DEBUG("Client have not been approved. Approving...");
			// If we have no qualified request within 10 seconds, then we treat
			// like simple client
			timer = new Timer(10 * 1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (approved == null && !socket.isClosed()) {
						setConnectionPromptAnswer(true);
					}
					Main.DEBUG("No <request> recieved. Treating client like a simple one.");
				}

			});
			timer.setRepeats(false);
			timer.start();

			while (this.approved == null && !socket.isClosed()) {
				approveClient();
			}
			System.out.println("Approval answer was: " + this.approved);
		}

	}

	/**
	 * The loop waiting for incoming packets from connected clients. Starts with
	 * the thread.
	 */
	private void runClientThread() {
		String errorMsg = "";
		while (((buffReader != null) && (buffWriter != null)) && (!socket.isClosed()) && approved) {
			// Main.DEBUG("Entering loop to listen for messages");
			try {
				StringBuilder sb = new StringBuilder();
				int newChar;
				while ((newChar = buffReader.read()) != -1 && approved) {
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

				errorMsg = "Socket closed in runClientThread.";
				System.out.println(errorMsg);
				// e.printStackTrace();
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
	 * Sends the provided message to the client
	 * 
	 * @param message
	 */
	public void sendMessage(Message message) {
		try {
			buffWriter.write(message.getMessage());
			buffWriter.flush();
			Main.DEBUG("SENT: " + message.getMessage());
		} catch (IOException e) {
			System.err.println("IO in ClientThread sendMessage: " + e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e2) {
			System.err.println("NullPointer in ClientThread sendMessage: " + e2.getMessage());
			e2.printStackTrace();
		}
	}

	private void setConnectionPromptAnswer(boolean simple) {
		Message msg = new Message(
				new Message.MessageBuilder().setMessageSender(socket.getInetAddress().getHostAddress()));
		setConnectionPromptAnswer(msg, simple);
	}

	private void setConnectionPromptAnswer(Message msg, boolean simple) {
		Main.DEBUG("Validating answer for message: " + msg.getMessage() + " from client simlpe: " + simple);
		int answer = JOptionPane.showConfirmDialog(MainGUI.getInstance(),
				"An incoming connection from: " + socket.getInetAddress().getHostAddress() + " with display name \""
						+ msg.getSender() + "\" has been detected."
						+ (simple ? ("\nClient is running a simplified version of the program.")
								: ("\n\"" + msg.getRequestText() + "\""))
						+ "\n" + "Do you want to accept the connection?",
				"Incoming connection", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			this.approved = true;
		} else {
			this.approved = false;
		}
		this.simple = simple;
	}
}
