package pack;

import java.net.Socket;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class ChatThread extends Thread {
	
	private ChatPanelGUI chatPanel;
	private ArrayList<ClientThread> clients;
	
	public ChatThread(String name, ArrayList<ClientThread> clients) {
		this.clients = clients;
		SwingUtilities.invokeLater(() -> {
			chatPanel = new ChatPanelGUI(name, this);
			MainGUI.getInstance().addChatPanel(chatPanel);
			chatPanel.getUserList().setListData(getClientNames());
			
			String[] a = {"A","B"};
			chatPanel.getUserList().setListData(a);
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
	
	public void dispatchMessage(Message message) {
		for(ClientThread t : clients) {
			t.sendMessage(message);
		}
		if(message.disconnect()) {
			MainGUI.getInstance().removeChatPanel(this);
		}
	}
	
	public void sendMessageToClient(String clientName, Message message) {
		ClientThread thread = null;
		for (ClientThread t : clients) {
			if (t.getID().equalsIgnoreCase(clientName)) {
				thread = t;
				break;
			}
		}
		if (thread != null) {
			thread.sendMessage(message);
		}
		else {
			System.out.println("Tried to send message to " + clientName + ". Could not find any client with that ID.");
		}
	}
	
	public ArrayList<ClientThread> getClients() {
		return this.clients;
	}
	
	public void addClientThread(ClientThread thread) {
		this.clients.add(thread);
	}
	
	public ChatPanelGUI getChatPanelGUI() {
		return this.chatPanel;
	}
	
	private String[] getClientNames() {
		String[] names = new String[clients.size()];
		for(int i = 0; i < clients.size(); i++) {
			names[i] = clients.get(i).getID();
		}
		return names;
	}

}
