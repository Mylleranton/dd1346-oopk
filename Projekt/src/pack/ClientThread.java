package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientThread extends Thread{
	
	private Socket socket;
	private String name;
	private BufferedWriter buffWriter;
	private BufferedReader buffReader;
	
	
	public ClientThread(Socket socket) {
		this.socket = socket;
		try {
			this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		name = socket.getInetAddress().getHostAddress();
	}
	
	public void sendMessage(Message message) {
		try {
			buffWriter.write(message.getMessage());
			buffWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(message.disconnect()) {
				endConnection();
			}
		}
	}
	
	public void endConnection() {
		
	}
	
	@Override
	public void run() {
		
	}
	
	public String getID() {
		return this.name;
	}

}
