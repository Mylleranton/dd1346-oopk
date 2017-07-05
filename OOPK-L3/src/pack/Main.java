package pack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	private static final String fileName = "/src/pack/Liv.xml";
	private static Scanner scanner;
	
	public static void main(String[] args) {
		try {
			scanner = new Scanner(new File(System.getProperty("user.dir") + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		new Main();
	}
	
	public Main(){
		readNode();
	}
	
	private MyNode readNode(String...s) {
		String line; 
		String openTag = "";
		MyNode retNode = new MyNode();
		
		while (scanner.hasNextLine()) {
			
			//String[] nodeData = {"","",""};
			String text = "";
			if (s.length == 0) {
				line = scanner.nextLine();
			} else {
				line = s[0];
			}
			
			if(line.startsWith("<?xml")) {
				line = scanner.nextLine();
			}
			System.out.println(line);
			Scanner innerScanner = new Scanner(line);
			
			while(innerScanner.hasNext()){
				String word = innerScanner.next();				
				if(word.startsWith("<")) {
					retNode.setUserObject(word.substring(1));
					openTag = word.substring(1);
				} 
				else if (word.endsWith(">")){
					int startIndex = word.indexOf("\"");
					int endIndex = word.lastIndexOf("\"");
					retNode.setNodeLevel(word.substring(startIndex+1, endIndex));
				} 
				else {
					text += word.concat(" ");
				}
			}
			
			retNode.setText(text);
			String next = scanner.nextLine();
			if (next.startsWith("</".concat(openTag))) {
				return retNode;
			}
			else if (next.startsWith("<")) {
				retNode.add(readNode(next));
			}
		}

		return retNode;
	}

		

}
