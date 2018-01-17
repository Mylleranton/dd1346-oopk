package pack;

import java.net.Socket;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


/**
 * 
 * ChatThread is the Thread backend implementation of a ChatPanelGUI that holds a number of
 * clients (ClientThreads) and all in all represents a single chat.
 * @author anton
 *
 */
public class ChatThread extends Thread {
	
	private ChatPanelGUI chatPanel;
	private ArrayList<ClientThread> clients;
	
	public ChatThread(String name, ArrayList<ClientThread> clients) {
		this.clients = clients;
		SwingUtilities.invokeLater(() -> {
			chatPanel = new ChatPanelGUI(name, this);
			MainGUI.getInstance().addChatPanel(chatPanel);
			chatPanel.getUserList().setListData(getClientDisplayNames());
			
			MainGUI.getInstance().setOptionPanel(chatPanel.getOptionPane());
		});
	}
	
	public ChatThread(String name) {
		this(name, new ArrayList<ClientThread>());
	}
	
	@Override
	public void run(){
		System.out.println("Thread created");
	}
	
	
	/**
	 * Sends a message to all but one client (for the pourpose when a client sends the server a message)
	 * @param clientName - The client the msg shouldn't be sent to
	 * @param message - The message to be sent
	 */
	public void sendMessageToAllButOne(String clientID, Message message) {
		for(ClientThread t : clients) {
			if (t.getID() != clientID) {
				t.sendMessage(message);
			}
		}
	}
	
	/**
	 * Dispatches a message to all clients connected 
	 * @param message the message to be dispatched.
	 */
	public void dispatchMessage(Message message) {
		for(ClientThread t : clients) {
			t.sendMessage(message);
		}
		if(message.disconnect()) {
			MainGUI.getInstance().removeChatPanel(this);
		}
	}
	
	/**
	 * Dispatch a message to a particular client connected
	 * @param clientName - The recieving client
	 * @param message - The message to be sent
	 */
	public void sendMessageToClient(String clientID, Message message) {
		ClientThread thread = getClientThread(clientID);
		if (thread != null) {
			thread.sendMessage(message);
		}
		else {
			System.out.println("Tried to send message to " + clientID + ". Could not find any client with that ID.");
		}
	}
	
	/**
	 * Disconnects a certain client
	 * @param clientName - The client to be disconnected
	 */
	public void disconnecClient(String clientID) {
		ClientThread thread = getClientThread(clientID);
		if(thread != null) {
			removeClientThread(thread);
			thread.endConnection();
			dispatchMessage(new Message(new Message.MessageBuilder().setMessageSender(Main.CURRENT_CHAT_NAME).setText("Client " + clientID + " has disconnected")));
		}
		onDisconnect();
	}
	
	/**
	 * Disconnects all active clientthreads.
	 */
	public void disconnectAll() {
		for (ClientThread th : this.clients) {
			removeClientThread(th);
			th.endConnection();
		}
		onDisconnect();
	}
	
	/**
	 * Called on any type of disconnect happening withing the chat. If there are no clients left, then the chat closes.
	 */
	public void onDisconnect() {
		if (clients.isEmpty()) {
			MainGUI.getInstance().removeChatPanel(this);
		}
	}
	/**
	 * Called whenever an associated ClientThread gets an updated display name
	 */
	public void onNameUpdate() {
		// One client, then we name the chat the name of that client
		String newName = "";
		if(clients.size() == 1) {
			newName = clients.get(0).getDisplayName();
		} else {
			newName = clients.get(0).getDisplayName().concat(" m.fl.");
		}
		int index = MainGUI.getInstance().getTabbedPane().indexOfComponent(this.getChatPanelGUI());
		if (index >= 0) {
			MainGUI.getInstance().getTabbedPane().setTitleAt(index, newName);
		}
	}
	
	public ArrayList<ClientThread> getClients() {
		return this.clients;
	}
	
	public void addClientThread(ClientThread thread) {
		this.clients.add(thread);
	}
	
	public void removeClientThread(ClientThread thread) {
		this.clients.remove(thread);
	}
	
	public ChatPanelGUI getChatPanelGUI() {
		return this.chatPanel;
	}
	
	/**
	 * Returns an array with the connected ClientThreads displaynames, which can be 
	 * either their IP or a user set name.
	 * @return
	 */
	private String[] getClientDisplayNames() {
		String[] names = new String[clients.size()];
		for(int i = 0; i < clients.size(); i++) {
			names[i] = clients.get(i).getDisplayName();
		}
		return names;
	}
	
	/**
	 * Returns an array with the connected ClientThreads unique IDs
	 * @return
	 */
	public String[] getClientIDs() {
		String[] names = new String[clients.size()];
		for(int i = 0; i < clients.size(); i++) {
			names[i] = clients.get(i).getID();
		}
		return names;
	}
	
	/**
	 * Return the clientthread with name name
	 * @param name
	 * @return The assiociated ClientThread if found, otherwise null
	 */
	private ClientThread getClientThread(String ID) {
		ClientThread thread = null;
		for (ClientThread t : clients) {
			if (t.getID().equalsIgnoreCase(ID)) {
				thread = t;
				break;
			}
		}
		return thread;
	}

	@Override
	public String toString() {
		return this.getChatPanelGUI().getName();
	}
}
