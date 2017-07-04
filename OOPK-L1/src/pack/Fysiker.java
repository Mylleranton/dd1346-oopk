package pack;

public class Fysiker extends Human {
	
	private int year;
	
	// Konstruktör
	public Fysiker(int inAge, String inName, int inYear) throws Exception {
		super(inAge,inName);
		// Se till så kraven på fysiker uppfylls, annars exception
		if (inYear < 1932 || inYear > 2015 || inAge < 15){
			throw new IllegalArgumentException("Fysiker måste vara äldre än 15 år och kan inte börjat innan 1932 eller efter 2015.");
		}
		
		this.year = inYear;
	}
	// Slumpmässig konstruktör
	public Fysiker() throws Exception {
		super();
		this.year = 1932 + (int) ((2015-1932)*Math.random());
		// Se till så en fysiker alltid är över 15.
		if (this.getAge() < 15) {
			this.setAge((int) (100*Math.random()));
		}
		
	}
	public int getYear(){
		return this.year;
	}
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

	

	
}
