package composite;

import java.util.ArrayList;

public abstract class Component implements Iterable<Component> {
	
	
	protected String name;
	protected double weight;
	
	public Component(String inName, double inWeight ) {
		this.name = inName;
		this.weight = inWeight;
	}
	
	public abstract void addChild(Component c);
	
	public abstract void removeChild(Component c);
	
	public abstract ArrayList<Component> getChildren();
	
	public abstract boolean hasChildren();
	
	public String toString() {
		return this.name;
	}
	public double getWeight(){
		return this.weight;
	}
	
}
