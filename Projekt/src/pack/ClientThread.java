package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.CharBuffer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ClientThread extends Thread {
	
	private Socket socket;
	private String name = "";
	private String ID;
	private BufferedWriter buffWriter;
	private BufferedReader buffReader;
	private ChatThread chatThread;
		
	
	public ClientThread(Socket socket, ChatThread chatThread) {
		assert(socket != null):"Socket cannot be null";
		assert(chatThread != null):"ChatThread cannot be null";
		
		this.chatThread = chatThread;
		this.socket = socket;

	}
	
	public void sendMessage(Message message) {
		try {
			buffWriter.write(message.getMessage());
			buffWriter.flush();
			Main.DEBUG("SENT: " + message.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (NullPointerException e2) {
			System.err.println(e2.getMessage());
		} finally {
			if((message.disconnect() && message.getSender().equalsIgnoreCase(Main.CURRENT_CHAT_NAME)) && !message.getMessageText().equalsIgnoreCase("User terminating connection")) {
				Main.DEBUG("Message sent contained disconnect - ending streams");
				chatThread.disconnectClient(this);
			}
		}
	}
	
	public void recieveMessage(Message message) {
		System.out.println("Recieved message: " + message.getMessage());
		if(!getDisplayName().equalsIgnoreCase(message.getSender()) && !message.getSender().equalsIgnoreCase("")) {
			if (!message.getSender().startsWith("SYSTEM")) {
				this.name = message.getSender();
				chatThread.onNameUpdate();
			}
		}
		
		chatThread.getChatPanelGUI().displayMessage(message);
		
		
		
		if (message.disconnect()) {
			// Do not relay the disconnect-tag!
			chatThread.disconnectClient(this);
			Message newMessage = new Message(new Message.MessageBuilder().setMessageSender(message.getSender()).setText(message.getMessageText()).setTextColor(message.getTextColor()));
			chatThread.sendMessageToAllButOne(getID(), newMessage);
		} else {
			chatThread.sendMessageToAllButOne(getID(), message);
		}
	}
	
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
	
	@Override
	public void run() {
		try {
			this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Failed to setup streams in ClientThread");
			e.printStackTrace();
		}
		this.ID = socket.getInetAddress().getHostAddress();
		int i = 1;
		for (String s : chatThread.getClientIDs()) {
			if (s.equalsIgnoreCase(ID)) {
				i++;
			}
		}
		if (i > 1) {
			this.ID = ID.concat("(" + i + ")");
		}
		
		Main.DEBUG("Created ClientThread with ID " + ID);
		// Add to the chats client-list
		chatThread.addClientThread(this);
		runClientThread();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		this.socket.close();
		this.buffReader.close();
		this.buffWriter.close();
	}
	
	private void runClientThread() {
		String errorMsg = "";
		while((buffReader != null && buffWriter != null) && socket != null) {
		//Main.DEBUG("Entering loop to listen for messages");
			try{
				StringBuilder sb = new StringBuilder();
				int newChar;
				while((newChar = buffReader.read()) != -1) {
					//Main.DEBUG("Inner loop run, buffered reader has read a char: " + (char) newChar);
					sb.append((char) newChar);
					
					// Check if we have recieved a complete message
					if (sb.length() > 8) {
						if (sb.substring(0, 8).equalsIgnoreCase("<message")) {
							if (sb.length() > 18) {
								if(sb.substring(sb.length()-10).equalsIgnoreCase("</message>")) {
									
									//Main.DEBUG("RECIEVED: " + sb.toString());
									if (sb.toString() != null && !sb.toString().equalsIgnoreCase(" ")) {
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
		this.displayEndOfChatMessage(errorMsg);
	}
	
	/**
	 * WORKS IF MESSAGE IS TERMINATED WITH NEWLINE
	 */
	private void runClientThread2(){
		while((buffReader != null && buffWriter != null) && socket != null) {
		Main.DEBUG("Entering loop to listen for messages");
			//while (true){
			try{
				StringBuilder sb = new StringBuilder();
				String line;
				while((line = buffReader.readLine()) != null) {
					Main.DEBUG("Inner loop run, buffered reader has read a line: " + line);
					sb.append(line);
				}
				Main.DEBUG("Recieved " + sb.toString());
				if (sb.toString() != null && !sb.toString().equalsIgnoreCase(" ")) {
					recieveMessage(new MessageParser().parseInputStream(sb.toString()));
				}
				
			} catch (IOException e) {
				System.out.println("Could not read line...");
				e.printStackTrace();
			}
		} 
		System.out.println("Client ended connection without saying goodbye...");
	}
	
	public void displayEndOfChatMessage(String msg) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(MainGUI.getInstance(), "The connection to " + getDisplayName() + " has been terminated.\n" + msg, "Disconnected", JOptionPane.INFORMATION_MESSAGE);
		});
	}
	
	public String getID() {
		return this.ID;
	}
	
	public String getDisplayName() {
		if (name.equalsIgnoreCase("")) {
			return this.ID;
		}
		return this.name;
	}

}
