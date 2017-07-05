package pack;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyNode extends DefaultMutableTreeNode {
	private String level;
	private String text;
	
	public MyNode(String name, String mLevel, String mText) {
		super(name);
		setNodeLevel(mLevel);
		setText(mText);
	}
	public MyNode(){
		super();
	}

	public String getNodeLevel() {
		return level;
	}

	public void setNodeLevel(String level) {
		this.level = level;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
