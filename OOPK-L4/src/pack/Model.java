package pack;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Model {
	// L parameter, number of particles and holding arrays for stuck/unstuck particles
	private static double L = 1;
	private int particles;
	private ArrayList<Particle> partArray;
	private ArrayList<Particle> stuckPartArray;
	private ArrayList<Particle> allParticles;
	
	public Model(int particles_in){
		this.particles = particles_in;
		
		// Init arrays
		partArray = new ArrayList<Particle>();
		stuckPartArray = new ArrayList<Particle>();
		allParticles = new ArrayList<Particle>();
		
		// Generate new particles according to input
		for(int i = 0; i < particles; i++) {
			partArray.add(new Particle());
		}
		allParticles.addAll(partArray);
	}
	
	// Update the positions of all non-stuck particles
	public void updateParticles(){
		for(Particle p : partArray){
			p.updatePosition((2*Math.PI)*Math.random());
		}
	}
	
	// Returns the positions of the particles
	public ArrayList<Point2D> getParticlePositions(){
		ArrayList<Point2D> returnList = new ArrayList<Point2D>();
		
		for(Particle p : partArray) {
			returnList.add(p.getPosition());
		}
		for(Particle p : stuckPartArray) {
			returnList.add(p.getPosition());
		}
		return returnList;
	}
	
	// Getters for the arrays
	public ArrayList<Particle> getParticles(){
		return this.partArray;
	}
	public ArrayList<Particle> getStuckParticles(){
		return this.stuckPartArray;
	}
	// Getter/setter for L
	public static double getL() {
		return L;
	}
	public static void setL(double L_in) {
		L = L_in;
	}

	/**
	 * 
	 * @author anton
	 * Particle model class
	 */
	class Particle {
		// Holds position
		private Point2D pos;
		
		// Constructor, initializes the position
		public Particle(double x_in,double y_in) {
			
			pos = new Point2D.Double();
			this.pos.setLocation(x_in, y_in);
		}
		// Construct random particle
		public Particle(){
			this(600*Math.random(), 600*Math.random());
		}
		
		// Updates the position with a random angle
		public void updatePosition(double theta){
			this.pos.setLocation(this.pos.getX() + L*Math.cos(theta), 
					this.pos.getY() + L*Math.sin(theta));
		}
		// Getter for position
		public Point2D getPosition(){
			return pos;
		}
	}
}
