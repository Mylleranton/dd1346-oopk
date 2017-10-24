package pack;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author anton
 * Model implementation for MVC, initiates particle arrays
 */
public class Model {
	// L parameter, number of particles and holding arrays for stuck/unstuck
	// particles
	private static double L = 1;
	private int particles;
	private ArrayList<Particle> partArray;
	private ArrayList<Particle> stuckPartArray;
	private ArrayList<Particle> allParticles;

	/**
	 * Constructs a model with set number of particles
	 * @param particles_in Number of particles in model
	 */
	public Model(int particles_in) {
		particles = particles_in;

		// Init arrays
		partArray = new ArrayList<Particle>();
		stuckPartArray = new ArrayList<Particle>();
		allParticles = new ArrayList<Particle>();

		// Generate new particles according to input
		for (int i = 0; i < particles; i++) {
			partArray.add(new Particle());
		}
		allParticles.addAll(partArray);
	}

	// Update the positions of all non-stuck particles
	/**
	 * Update the positions of all particles 
	 */
	public void updateParticles() {
		for (Particle p : partArray) {
			p.updatePosition((2 * Math.PI) * Math.random());
		}
	}

	// Returns the positions of the particles
	/**
	 * @return ArrayList of all particle positions
	 */
	public ArrayList<Point2D> getParticlePositions() {
		ArrayList<Point2D> returnList = new ArrayList<Point2D>();

		for (Particle p : partArray) {
			returnList.add(p.getPosition());
		}
		for (Particle p : stuckPartArray) {
			returnList.add(p.getPosition());
		}
		return returnList;
	}

	/**
	 * @return ArrayList with all particles
	 */
	public ArrayList<Particle> getParticles() {
		return partArray;
	}

	/**
	 * @return ArrayList with all stuck particles
	 */
	public ArrayList<Particle> getStuckParticles() {
		return stuckPartArray;
	}

	/**
	 * @return Double L 
	 */
	public static double getL() {
		return L;
	}

	/**
	 * @param L_in new value of L
	 */
	public static void setL(double L_in) {
		L = L_in;
	}

	/**
	 * @author anton 
	 * Particle model class
	 */
	class Particle {
		// Holds position
		private Point2D pos;

		/**
		 * Initializes a Particle with position x,y
		 * @param x_in x position
		 * @param y_in y position
		 */
		public Particle(double x_in, double y_in) {
			pos = new Point2D.Double();
			pos.setLocation(x_in, y_in);
		}

		/**
		 * Initializes a particle with random position
		 */
		public Particle() {
			this(600 * Math.random(), 600 * Math.random());
		}

		/**
		 * Update the position of particle with random angle theta
		 * @param theta Angle of movement
		 */
		public void updatePosition(double theta) {
			pos.setLocation(pos.getX() + (L * Math.cos(theta)), pos.getY() + (L * Math.sin(theta)));
		}

		// Getter for position
		public Point2D getPosition() {
			return pos;
		}
	}
}
