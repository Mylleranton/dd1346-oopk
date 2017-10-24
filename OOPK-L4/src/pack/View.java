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

/**
 * 
 * @author anton
 * View class 
 * Handles all view calls and repaining
 */
public class View extends JPanel {
	// Brownian motion window size
	public static final int SIZE = 600;
	
	private Model model;
	
	public View(Model model_in){
		super();
		// Super because of JPanel
		
		// JPanel preferences
		this.setPreferredSize(new Dimension(SIZE,SIZE));
        this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		
		this.model = model_in;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// Particles and StuckParticles
		ArrayList<Particle> parts = model.getParticles();
		ArrayList<Particle> stuckParts = model.getStuckParticles();
		
		// Loop through all particles and paint them in the correct color
		for(int i = 0; i<parts.size(); i++) {
			Particle p = parts.get(i);

			Ellipse2D.Double ell = new Ellipse2D.Double(p.getPosition().getX(), p.getPosition().getY(),
					2, 2);
			g2.setColor(Color.RED);	
			g2.fill(ell);
		}
		for(int i = 0; i < stuckParts.size(); i++) {
			Particle p = stuckParts.get(i);

			Ellipse2D.Double ell = new Ellipse2D.Double(p.getPosition().getX(), p.getPosition().getY(),
					2, 2);
			g2.setColor(Color.YELLOW);
			g2.fill(ell);
		}
	}

}
