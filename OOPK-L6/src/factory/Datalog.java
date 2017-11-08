package factory;

public class Datalog extends Human {
	
	// Har utöver Humans parametrar ett startår 
	private int year;
	
	// Konstruktör
	private Datalog(int inAge, String inName, int inYear) throws IllegalArgumentException {
		super(inAge,inName);
		// Se till så kraven på dataloger uppfylls, annars exception
		if (inYear < 1932 || inYear > 2015 || inAge < 15){
			throw new IllegalArgumentException("Dataloger måste vara äldre än 15 år och kan inte börjat innan 1932 eller efter 2015.");
		}
		this.year = inYear;
	}

	public int getYear(){
		return this.year;
	}
	
	@Override
	public String toString(){
		return super.toString() + String.format(" och tillhör D-%02d", this.year % 100);
	}
	// Jämför dataloger som Humans, fast om två dataloger är lika avgör årskurs.
	@Override
	public int compareTo(Human o){
		int res = this.getAge() - o.getAge();
		if(res == 0 && o instanceof Datalog){
			return this.year - ((Datalog) o).getYear();
		}
		return super.compareTo(o);
	}
	
	public static Datalog getInstance(String name, int year, int age) {
		Datalog d = new Datalog(age, name, year);
		return d;
	}

	

	
}
