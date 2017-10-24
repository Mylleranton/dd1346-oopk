package pack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import pack.Model.Particle;



public class View extends JPanel {
	
	public static final int SIZE = 600; 
	private Model model;
	public View(Model model_in){
		super();
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		this.model = model_in;
        this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		ArrayList<Particle> parts = model.getParticles();
		ArrayList<Particle> stuckParts = model.getStuckParticles();
		
		
		for(int i = 0; i<parts.size(); i++){
			Particle p = parts.get(i);

			Ellipse2D.Double ell = new Ellipse2D.Double(p.getPosition().getX(), p.getPosition().getY(),
					2, 2);
			g2.setColor(Color.RED);	
			g2.fill(ell);
		}
		
		for(int i = 0; i<stuckParts.size(); i++){
			Particle p = stuckParts.get(i);

			Ellipse2D.Double ell = new Ellipse2D.Double(p.getPosition().getX(), p.getPosition().getY(),
					2, 2);
			g2.setColor(Color.YELLOW);
			g2.fill(ell);
		}
	}

}
