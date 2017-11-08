package factory;

public class Fysiker extends Human {
	
	// Har utöver Humans parametrar ett startår 
	private int year;
	
	// Konstruktör
	private Fysiker(int inAge, String inName, int inYear) throws IllegalArgumentException {
		super(inAge,inName);
		// Se till så kraven på fysiker uppfylls, annars exception
		if (inYear < 1932 || inYear > 2015 || inAge < 15){
			throw new IllegalArgumentException("Fysiker måste vara äldre än 15 år och kan inte börjat innan 1932 eller efter 2015.");
		}
		this.year = inYear;
	}

	public int getYear(){
		return this.year;
	}
	
	@Override
	public String toString(){
		return super.toString() + String.format(" och tillhör F-%02d", this.year % 100);
	}
	// Jämför fysiker som Humans, fast om två fysiker är lika avgör årskurs.
	@Override
	public int compareTo(Human o){
		int res = this.getAge() - o.getAge();
		if(res == 0 && o instanceof Fysiker){
			return this.year - ((Fysiker) o).getYear();
		}
		return super.compareTo(o);
	}
	
	public static Fysiker getInstance(String name, int year, int age) {
		Fysiker f = new Fysiker(age, name, year);
		return f;
	}

	

	
}
