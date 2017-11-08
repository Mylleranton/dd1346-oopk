package composite;

import java.util.Collections;

public class TestClass {

	public static void main(String[] args) {

		Composite väska = new Composite("Väska", 2.0);
		Item tröja = new Item("Tröja", 0.30);
		Item byxa = new Item("Byxa", 0.30);
		Item tröja2 = new Item("Tröja 2", 0.40);
		Item byxa2 = new Item("Byxa 2", 0.50);

		Composite nessecär = new Composite("Nessecär", 0.50);
		Composite ask = new Composite("Ask", 0.10);
		Item hårspänne = new Item("Hårspänne", 0.30);
		Item gummisnodd = new Item("Gummisnodd", 0.30);
		Item tvål = new Item("Tvål", 0.40);
		Item schampoo = new Item("Schampoo", 0.50);

		Composite påse = new Composite("Påse", 0.10);
		Item strumpa = new Item("Strumpa", 0.20);
		Item strumpa2 = new Item("Strumpa 2", 0.25);
		Item strumpa3 = new Item("Strumpa 3", 0.30);

		påse.addChild(strumpa);
		påse.addChild(strumpa3);
		påse.addChild(strumpa2);

		ask.addChild(hårspänne);
		ask.addChild(gummisnodd);
		nessecär.addChild(ask);
		nessecär.addChild(tvål);
		nessecär.addChild(schampoo);

		väska.addChild(tröja);
		väska.addChild(byxa);
		väska.addChild(tröja2);
		väska.addChild(byxa2);
		väska.addChild(påse);
		väska.addChild(nessecär);

		// System.out.println(väska.toString());
		// System.out.println(väska.getWeight());

		//
		// System.out.println(väska.name + ", enskild vikt " + väska.weight);
		// printStructure(väska,0);

		// DFS or BFS
		// for(Component c : väska){
		// System.out.println(c.name);
		// }

		// Klonar väskan och printar klonens innehåll
		Component clone = väska.clone();
		Component clone2 = väska.clone();
		
		printStructure(clone, 0);
		System.out.println(clone == väska);
		System.out.println(clone == clone2);
		// Lägger till ett barn till väskan, och printar klonen som är
		// opåverkad.
		ask.addChild(new Item("TEST", 2));
		clone2.addChild(new Item("TEST 2", 1));
		printStructure(clone, 0);

	}

	// Ger djupet-först-genomgång
	public static void printStructure(Component root, int nivå) {
		for (Component c : root.getChildren()) {
			System.out.println(String.join("", Collections.nCopies(nivå, "-")) + c.name + ", enskild vikt " + c.weight);
			if (c.hasChildren()) {
				printStructure(c, nivå + 2);
			}
		}
	}

}
