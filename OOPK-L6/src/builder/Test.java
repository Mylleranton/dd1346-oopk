package builder;
import java.util.ArrayList;
import java.util.LinkedList;

import builder.*;

public class Test {
	
	private static int N = 10000000;
	private static ArrayList arrList;
	private static LinkedList linList;
	private static BuilderList bList;
	
	public static void main(String[] args){
		
		long tStart = System.nanoTime();
		
		
		BuilderList.Builder b = new BuilderList.Builder();
		arrList = new ArrayList();
		linList = new LinkedList();
		
		for (int i = 0; i<N; i++){
			//b.add(a(i), i);
			arrList.add(a(i), i);
			//linList.add(a(i), i);
		}

		//bList = b.build();
		
		for (int i = 0; i<N; i++){
			//bList.get(b());
			arrList.get(b());
			//linList.get(b());
		}
		
		long tStop = System.nanoTime();
		System.out.println("Time elapsed: " + (tStop - tStart)/1000000 + " ms");
	}
	
	public static int a(int i) {
		return 0;
		//return (int) Math.floorDiv(i, 2);
		//return (int) Math.floor(i*Math.random());
	}

	public static int b() {
		return (int) Math.floor(N*Math.random());
	}
	
}
