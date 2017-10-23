package pack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model;
	public View(Model model_in){
		this.setPreferredSize(new Dimension(600,600));
		this.model = model_in;
		this.setBackground(Color.BLACK);
		this.setForeground(Color.BLACK);
	}
	
	@Override
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.clearRect(0, 0, 600, 600);
		Point2D[] pointArr = model.getParticlePositions();
		for(int i = 0; i<pointArr.length; i++){
			Ellipse2D.Double ell = new Ellipse2D.Double(pointArr[i].getX(), pointArr[i].getY(),
					2, 2);
			g2.setColor(Color.RED);
			g2.fill(ell);
		}
	}

}
