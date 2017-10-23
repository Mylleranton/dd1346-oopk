package pack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/**
 * 
 * DirTreeE2 implements the XML scanner but does not print out a tree
 * For that functionality, see DirTreeMainC
 * @author anton
 *
 */
public class DirTreeE2 {

	private static final String fileName = "/src/pack/Liv.xml";
	private static Scanner scanner;
	
	public static void main(String[] args) {
		try {
			scanner = new Scanner(new File(System.getProperty("user.dir") + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		new DirTreeE2();
	}
	
	public DirTreeE2(){
		readNode();
	}
	
	private MyNode readNode(String...s) {
		String line; 
		String openTag = "";
		MyNode returnNode = new MyNode();
		
		// Scanna varje linje i XML-dokumentet
		while (scanner.hasNextLine()) {
			
			// Beskrivande text för noden
			String text = "";
			// Ingen mer nästad nod -> Nästa linje
			if (s.length == 0) {
				line = scanner.nextLine();
			} else {
				line = s[0];
			}
			// Skippa första linjen
			if(line.startsWith("<?xml")) {
				line = scanner.nextLine();
			}
			// DEBUG System.out.println(line);
			//Scanna varje ord för sig
			Scanner innerScanner = new Scanner(line);
			
			while(innerScanner.hasNext()){
				String word = innerScanner.next();	
				// Öppnande tag
				if(word.startsWith("<")) {
					returnNode.setUserObject(word.substring(1));
					openTag = word.substring(1);
				} 
				// Avslutande tag
				else if (word.endsWith(">")){
					int startIndex = word.indexOf("\"");
					int endIndex = word.lastIndexOf("\"");
					returnNode.setNodeLevel(word.substring(startIndex+1, endIndex));
				} 
				// Annars är det en beskrivande text
				else {
					text += word.concat(" ");
				}
			}
			
			returnNode.setText(text);
			
			String next = scanner.nextLine();
			// Om vi parsat hela denna sub-noden, returnera
			if (next.startsWith("</".concat(openTag))) {
				return returnNode;
			}
			// Om vi parsar vidare och hittar fler sub-noder, kör på
			else if (next.startsWith("<")) {
				returnNode.add(readNode(next));
			}
		}

		return returnNode;
	}

		

}
