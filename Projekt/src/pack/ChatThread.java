package pack;

import java.net.Socket;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


/**
 * 
 * ChatThread is the Thread backend implementation of a ChatPanelGUI that holds a number of
 * clients (ClientThreads) and all in all represents a single chat.
 * 
 * On creation of a ChatThread, it will automatically create a ChatPanelGUI connected with it. Then ClientThreads can be
 * connected to the chat by passing on a ChatThread object on ClientThread creation.
 * @author anton
 *
 */
public class ChatThread extends Thread {
	
	private ChatPanelGUI chatPanel;
	private ArrayList<ClientThread> clients;
	
	public ChatThread(String name, ArrayList<ClientThread> clients) {
		this.clients = clients;
		//SwingUtilities.invokeLater(() -> {
			chatPanel = new ChatPanelGUI(name, this);
			MainGUI.getInstance().addChatPanel(chatPanel);
			chatPanel.getUserList().setListData(getClientDisplayNames());
			
			MainGUI.getInstance().setOptionPanel(chatPanel.getOptionPane());
		//});
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
	public void disconnectClient(ClientThread thread) {
		removeClientThread(thread);
		thread.endConnection();
		onDisconnect();
	}
	
	/**
	 * Disconnect a certain client
	 * @param userID - The thread ID or displayname.
	 */
	public void disconnectClient(String userID) {
		boolean sucess = false;
		for(ClientThread t : clients) {
			if(t.getID().equalsIgnoreCase(userID)) {
				sucess = true;
				disconnectClient(t);
				return;
			}
		}
		for (ClientThread t : clients) {
			if (t.getDisplayName().equalsIgnoreCase(userID)) {
				sucess = true;
				disconnectClient(t);
				return;
			}
		}
		if (sucess){
			System.err.println("Could not disconnect user " + userID + ". No thread with that ID/name could be found.");
		}
		return;
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
		} 
		// Several clients
		else if (clients.size() > 1) {
			newName = clients.get(0).getDisplayName().concat(" m.fl.");
		}
		int index = MainGUI.getInstance().getTabbedPane().indexOfComponent(this.getChatPanelGUI());
		if (index >= 0 && !newName.equalsIgnoreCase("")) {
			MainGUI.getInstance().getTabbedPane().setTitleAt(index, newName);
			chatPanel.setName(newName);
		}
	
		// Display as much information about the connected clients as possible!
		
		chatPanel.getUserList().setListData(this.getClientQualifiedNames());
	}
	
	public ArrayList<ClientThread> getClients() {
		return this.clients;
	}
	
	/**
	 * Adds the provided thread to chatthreads internal list of clients. Should ONLY be called from ClientThreads internal method call.
	 * @param thread
	 */
	public void addClientThread(ClientThread thread) {
		this.clients.add(thread);
		onNameUpdate();
		if ( clients.size() >= 1 && chatPanel != null) {
			chatPanel.getUserList().setListData(getClientDisplayNames());
		}
		this.dispatchMessage(new Message(new Message.MessageBuilder().setMessageSender(Main.CURRENT_CHAT_NAME).setText("-- User " + thread.getDisplayName() + " connected. --")));
		Main.DEBUG("ChatThread now has " + clients.size() + " clients");
	}
	
	public void removeClientThread(ClientThread thread) {
		this.clients.remove(thread);
		onNameUpdate();
		chatPanel.getUserList().setListData(getClientDisplayNames());
		this.dispatchMessage(new Message(new Message.MessageBuilder().setMessageSender(Main.CURRENT_CHAT_NAME).setText("-- User " + thread.getDisplayName() + " disconnected. --")));
		Main.DEBUG("ChatThread now has " + clients.size() + " clients");
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
	 * Return a list of all the qualified names of a client. 
	 * Have the form "displayname (IP)" or "IP"
	 * @return
	 */
	public String[] getClientQualifiedNames() {
		String[] dispNames = getClientDisplayNames();
		String[] IDs = getClientIDs();
		assert(dispNames.length == IDs.length): "Error in clientthreads arraylengths. ID and Names mismatch.";
		String[] qualifiedNames = new String[IDs.length];
		for(int i = 0; i < dispNames.length; i++) {
			if(dispNames[i].equalsIgnoreCase(IDs[i])) {
				qualifiedNames[i] = IDs[i];
			} 
			else {
				qualifiedNames[i] = dispNames[i].concat(" (" + IDs[i] + ")");
			}
		}
		return qualifiedNames;
	}
	
	/**
	 * Get client ID from a qualified name
	 * @param qual - The qualified name
	 * @return
	 */
	public String getIDfromQualifiedName(String qual) {
		String[] dispNames = getClientDisplayNames();
		String[] IDs = getClientIDs();
		for(String ip : IDs) {
			if(qual.equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		
		for(int i = 0; i < IDs.length; i++) {
			if (qual.startsWith(dispNames[i]) && qual.endsWith("(" + IDs[i] + ")")) {
				return IDs[i];
			}
		}
		return null;
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
		// If we cant find user based on ID, try displayname
		if (thread == null) {
			for(ClientThread t : clients) {
				if (t.getDisplayName().equalsIgnoreCase(ID)) {
					thread = t;
					break;
				}
			}
		}
		return thread;
	}

	@Override
	public String toString() {
		return this.getChatPanelGUI().getName();
	}
}
