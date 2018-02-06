package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.junit.Test;

public class SendMessageToProgram {

	@Test
	public void test() {

		Socket connection = null;
		String ip = "130.229.150.15";
		String port = "6666";
		
		try {
			connection = new Socket(InetAddress.getByName(ip), Integer.parseUnsignedInt(port));
		} catch (UnknownHostException e) {
			System.err.println("The provided IP did not lead anywhere...: " + e.toString());

		} catch (IOException e) {
			System.err.println("I/O error on attemted outgoing connection: " + e.toString());

		} catch (NumberFormatException e) {
			System.err.println("Port has invalid format: " + port + ": " + e.toString());
		}
		
		assert(connection != null):"Connection failed";
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(bw != null && br != null):"Streams failed";
		
		while(true) {
			System.out.println("Waiting for message...");
			Scanner scanner = new Scanner(System.in);
			String msg = scanner.nextLine().trim();
			System.out.println("SENDING: " + msg);
			try {
				bw.write(msg);
				bw.flush();
				bw.flush();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
