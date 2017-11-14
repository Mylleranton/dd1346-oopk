package composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Composite implementation of component methods for containers and leafs
 * @author anton
 *
 */
public class Composite extends Component {

	protected ArrayList<Component> children = new ArrayList<Component>();

	public Composite(String inName, double inWeight) {
		super(inName, inWeight);
	}

	@Override
	public void addChild(Component c) {
		children.add(c);
	}

	@Override
	public void removeChild(Component c) {
		children.remove(c);
	}

	@Override
	public ArrayList<Component> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		String returnString = name;

		if (hasChildren()) {
			returnString += " som innehåller";
		}

		for (int i = 0; i < children.size(); i++) {
			if (i == (children.size() - 1)) {
				returnString += ", och en " + children.get(i).toString();
			} else if (i == 0) {
				returnString += " en " + children.get(i).toString();
			} else {
				returnString += ", en " + children.get(i).toString();
			}

		}
		return returnString;
	}

	@Override
	public double getWeight() {
		double returnWeight = weight;

		for (Component c : children) {
			returnWeight += c.getWeight();
		}
		return returnWeight;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public Iterator<Component> iterator() {
		return new IteratorDFS(this);
	}

	@Override
	public Component clone() {
		Component clone = null;
		try {
			clone = super.clone();

			if (clone instanceof Composite) {
				((Composite) clone).children = new ArrayList<Component>();
				for (Component c : children) {
					((Composite) clone).addChild(c.clone());
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return clone;

	}

	private class IteratorBFS implements Iterator<Component> {

		private Component root;
		private ArrayList<Component> componentsBF;

		public IteratorBFS(Component root) {
			this.root = root;
			componentsBF = new ArrayList<Component>();
			doBF();
		}

		private void doBF() {
			LinkedBlockingQueue<Component> queue = new LinkedBlockingQueue<Component>();
			queue.add(root);
			while (!queue.isEmpty()) {
				Component comp = queue.poll();
				componentsBF.add(comp);
				if (comp.hasChildren()) {
					queue.addAll(comp.getChildren());
				}

			}
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return !componentsBF.isEmpty();
		}

		@Override
		public Component next() {
			if (!hasNext()) {
				return null;
			}
			return componentsBF.remove(0);
		}

	}

	private class IteratorDFS implements Iterator<Component> {

		private Component root;
		private ArrayList<Component> componentsDF;

		public IteratorDFS(Component root) {
			this.root = root;
			componentsDF = new ArrayList<Component>();

			doDF(this.root);
		}

		private void doDF(Component comp) {
			componentsDF.add(comp);
			if (comp.hasChildren()) {
				for (Component c : comp.getChildren()) {
					doDF(c);
				}
			}

		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return !componentsDF.isEmpty();
		}

		@Override
		public Component next() {
			if (!hasNext()) {
				return null;
			}
			return componentsDF.remove(0);
		}

	}

}
