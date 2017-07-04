package pack;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {

	// Main test funktion. Innehåller kod för Command Line.
	// För övriga test, anropa varje funktion för sig.
	public static void main(String[] args) throws Exception{
		/*
		 *  HÄR BÖRJAR DEL C3:
		 */
		int i = 0;
		ArrayList<Human> mFHarray = new ArrayList<Human>(); 
		// Så länge det finns argument
		while(i < args.length){
			String mName = "";
			int mYear = 0;
			int mAge = 0;
			// Pröva om vi fått kommandona -F eller -H 
			// Pröva att parsa heltalen, se upp för NumberFormatExceptions och NullPointers (i > arr.len)
			if(args[i].equals("-F")){
				try{
					mName = args[i+1];
					mAge = Integer.parseInt(args[i+2]);
					mYear = Integer.parseInt(args[i+3]);
				} catch(NumberFormatException e) {
					System.out.println("NumberFormatException: Ålder och fysikklass måste vara heltal: " + e.getMessage());
					System.exit(0);
					
				} catch(Exception e){
					System.out.println(e.getMessage());
					System.exit(0);
				}
				// Vi har plockat ut datan från fyra fält, hoppa fram 4.
				i = i+4;
				// Se till så årsformateringen är korrekt
				mYear = mYear <= 32 ? mYear+2000 : mYear+1900;
				// Lägg till i vår arraylist
				mFHarray.add(new Fysiker(mAge,mName,mYear));
				
			} else if(args[i].equals("-H")) {
				try{
					mName = args[i+1];
					mAge = Integer.parseInt(args[i+2]);
				} catch(NumberFormatException e) {
					System.out.println("NumberFormatException: Ålder måste vara heltal: " + e.getMessage());
					System.exit(0);
				} catch(Exception e){
					System.out.println(e.getMessage());
					System.exit(0);
				}
				i = i+3;
				mFHarray.add(new Human(mAge,mName));
			} else {
				// Om vi får kommandon som inte är -H/F hamnar vi här.
				throw new IllegalArgumentException("Felaktigt format");
			}
		}
		// Skriv ut ArrayList:en
		for(Human element: mFHarray){
			System.out.println(element);
		}
		
		/*
		 * HÄR ANROPAS ÖVRIGA TESTFUNKTIONER
		 */
		
	}
	// TEST E2
	private static void printHTest(){
		Human a = new Human(23, "Hasse");
		System.out.println(a);
		System.out.println("Namn: " + a.getName() + ", ålder: " +  a.getAge());
		Human b = new Human();
		System.out.println(b);
	}
	// TEST E3
	private static void createHArray() {
		Human[] arr = new Human[15];
		for(int i = 0; i < 15; i++){
			arr[i] = new Human();
		}
		
		for(int j = 0; j<15; j++){
			System.out.println(arr[j]);
		}
	}
	//TEST E4 del 1 (E4.4)
	private static void createFArray() throws Exception{
		Fysiker[] arr = new Fysiker[15];
		for(int i = 0; i<15;i++){
			arr[i] = new Fysiker();
		}
		for(int i = 0; i<15;i++){
			System.out.println(arr[i]);
		}
	}
	// Test E4 del 2 (E4.5)
	private static void createFHArray() throws Exception{
		Fysiker[] arrF = new Fysiker[5];
		Human[] arrH = new Human[5];
		for(int i = 0; i<5;i++){
			arrF[i] = new Fysiker();
			arrH[i] = new Human();
		}
		
		for(int i = 0; i<5;i++){
			System.out.println(arrF[i]);
			System.out.println(arrH[i]);
		}
	}
	// TEST E5.2
	private static void compH() {
		Human a = new Human();
		Human b = new Human();
		
		if (a.compareTo(b) > 0) {
			System.out.println(a.getName() + " som är " + a.getAge() + " är äldre än " + b.getName() + " som är " + b.getAge());
		}
		else if (a.compareTo(b) < 0 ){
			System.out.println(a.getName() + " som är " + a.getAge() + " är yngre än " + b.getName() + " som är " + b.getAge());
		}
		else {
			System.out.println(a.getName() + " som är " + a.getAge() + " är lika gammal som " + b.getName() + " som är " + b.getAge());
		}
	}
	// TEST E5.4 del 1
	private static void compFH() throws Exception{
		Fysiker a = new Fysiker();
		Human b = new Human();
		if (a.compareTo(b) > 0) {
			System.out.println(a.getName() + " som är " + a.getAge() + " är äldre än " + b.getName() + " som är " + b.getAge());
		}
		else if (a.compareTo(b) < 0 ){
			System.out.println(a.getName() + " som är " + a.getAge() + " är yngre än " + b.getName() + " som är " + b.getAge());
		}
		else {
			System.out.println(a.getName() + " som är " + a.getAge() + " är lika gammal som " + b.getName() + " som är " + b.getAge());
		}
	}
	// TEST E5.4 del 2
	private static void createFHArrayAndSort() throws Exception{
		Human[] arr = new Human[10];
		for(int i = 0; i<10;i = i+2){
			arr[i] = new Human();
			arr[i+1] = new Fysiker();
		}
		Arrays.sort(arr);
		for(int i = 0; i<10;i++){
			System.out.println(arr[i]);
		}
	}
	
}
