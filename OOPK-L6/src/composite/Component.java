package composite;

import java.util.ArrayList;

public abstract class Component implements Iterable<Component>, Cloneable {

	protected String name;
	protected double weight;

	public Component(String inName, double inWeight) {
		name = inName;
		weight = inWeight;
	}

	public abstract void addChild(Component c);

	public abstract void removeChild(Component c);

	public abstract ArrayList<Component> getChildren();

	public abstract boolean hasChildren();

	public String toString() {
		return name;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public Component clone() throws CloneNotSupportedException {
		return (Component) super.clone();
	}

}
