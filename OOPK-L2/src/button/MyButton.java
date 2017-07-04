package button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

public class MyButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 3792105991631652494L;
	// Arrayer som håller de olika tillstånden
	private String[] mText = new String[2];
	private Color[] mColor = new Color[2];
	private int mState = 0;
	
	public MyButton(Color col1, Color col2, String text1, String text2) {
		super();
		//Mac-specifikt
		this.setOpaque(true);
		this.setBorderPainted(false);
		// Initiera arrayer och ge knappen sitt utseende
		mText[0] = text1;
		mText[1] = text2;
		mColor[0] = col1;
		mColor[1] = col2;
		setState(mState);
		this.addActionListener(this);
		
	}
	
	public MyButton(){
		this(Color.RED,Color.BLUE,"Jag är en knapp","Jag är inte en knapp");
	}
	// Ger knappen ett state (utseende)
	private void setState(int state){
		if(state < 2){
			this.setText(mText[state]);
			this.setBackground(mColor[state]);
			this.mState = state;
		}
	}
	public void toggleState(){
		if(mState == 0){
			setState(1);
		} else {
			setState(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		toggleState();
	}

}
