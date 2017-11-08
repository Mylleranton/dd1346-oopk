package factory;

public class Test {

	public static void main(String[] args) {
		Human f = Human.create("Anton", "F15", 20);
		System.out.println(f);

		Human d = Human.create("Pelle", "D99", 23);
		System.out.println(d);
		
		try {
			//Fysiker f2 = new Fysiker("Test", 2000,20);
			//Datalog d2 = new Datalog("Test", 2000,20);
		} catch (Exception e){
			e.printStackTrace();
		}
		//System.out.println(new TestHuman(20,"A"));
		
	}


}
