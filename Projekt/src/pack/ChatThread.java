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
			chatPanel = new ChatPanelGUI(name);
			MainGUI.getInstance().addChatPanel(chatPanel);
			chatPanel.getUserList().setListData(getClientNames());
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
