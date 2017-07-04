package pack;

public class Human implements Comparable<Human> {
	// Privata fält
	private int age;
	private String name;
	private static final String[] nameList = {"Anton","Hasse","Putte","Nils","Leffe","Olof","Margareta","Zlatan","Elina"};
	
	// Konstruktörer
	public Human(int ageIn, String nameIn){
		this.age = ageIn;
		this.name = nameIn;
	}
	// Slumpar namn och åler
	public Human(){
		this((int) (Math.random()*100), nameList[(int) (nameList.length*Math.random())]);
	}
	
	// Getters och setters för fält
	public int getAge(){
		return age;
	}
	public String getName(){
		return name;
	}
	public void setAge(int age){
		this.age = age;
	}
	public void setName(String name){
		this.name = name;
	}
	
	// Snygg utdata
	public String toString(){
		return "Namn: " + name + ", ålder: " + age;
	}
	
	// Jämför Humans baserat på ålder
	@Override
	public int compareTo(Human o) {
		int res = this.age - o.age;
		return res;
	}
	
}
