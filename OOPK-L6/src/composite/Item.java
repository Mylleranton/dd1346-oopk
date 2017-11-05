package composite;

import java.util.ArrayList;
import java.util.Iterator;

public class Item extends Component {

	public Item(String inName, double inWeight) {
		super(inName, inWeight);
	}

	@Override
	public void addChild(Component c) {
		System.out.println("Cannot add child to an item");
	}

	@Override
	public void removeChild(Component c) {
		System.out.println("Cannot remove child from an item");		
	}

	@Override
	public ArrayList<Component> getChildren() {
		System.out.println("Cannot get child from an item");
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Iterator<Component> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
