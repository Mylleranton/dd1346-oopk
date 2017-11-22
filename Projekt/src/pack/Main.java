package pack;

public class Main {

	protected static String CURRENT_CHAT_NAME = "Server";
	protected static int CURRENT_PORT = 6666;
	protected static boolean outgoingConnectionEnabled = false;
	protected static final boolean DEBUG_FLAG = false;
	
	public static void main(String[] args) {
		MainGUI instance = MainGUI.getInstance();
		
		
		//MessageParser p = new MessageParser();
		//System.out.println(p.getParsedMessage().getMessage());
	}
	
	public static void DEBUG(String msg) {
		if(DEBUG_FLAG) {
			System.out.println("DEBUG: " + msg);
		}
	}

}
