package factory;



public abstract class Human implements Comparable<Human> {
	
	private int age;
	private String name;
	
	public Human(int ageIn, String nameIn){
		this.age = ageIn;
		this.name = nameIn;
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
	
	// Snygg utdata Namn: XXXX, ålder: XX
	public String toString(){
		return "Namn: " + name + ", ålder: " + age;
	}
	
	// Jämför Humans baserat på ålder
	@Override
	public int compareTo(Human o) {
		int res = this.age - o.age;
		return res;
	}
		
	public static Human create(String name, String year, int age) {
		if (age < 15 || !(year.startsWith("F") || year.startsWith("D"))  ){
			System.out.println("Error in creation");
			System.exit(0);
		}
		int mYear = Integer.parseInt(year.substring(1));
		mYear = (mYear <= 32) ? mYear+2000 : mYear+1900;
		if (year.startsWith("F")) {
			return Fysiker.getInstance(name, mYear, age);
			
		} else if (year.startsWith("D")) {
			return Datalog.getInstance(name, mYear, age);			
		}
		return null;
	}
}
