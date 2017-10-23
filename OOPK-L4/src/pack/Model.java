package pack;
import java.awt.geom.Point2D;

public class Model {
	private static double L = 1;
	private int particles;
	private Particle[] partArray;
	
	public Model(int particles_in){
		this.particles = particles_in;
		
		partArray = new Particle[particles];
		for(int i = 0; i<particles; i++) {
			partArray[i] = new Particle();
		}
	}
	
	public void updateParticles(){
		for(int i = 0; i<particles;i++){
			partArray[i].updatePosition((2*Math.PI)*Math.random());
		}
	}
	
	public Point2D[] getParticlePositions(){
		Point2D[] posArray = new Point2D[particles];
		for(int i = 0; i<particles; i++) {
			posArray[i] = partArray[i].getPosition();
		}
		return posArray;
	}
	
	
	public static double getL() {
		return L;
	}
	public static void setL(double L_in) {
		L = L_in;
	}







	
	
	
	


	class Particle {
		private Point2D pos;
		
		public Particle(double x_in,double y_in){
			pos = new Point2D.Double();
			this.pos.setLocation(x_in, y_in);
		}
		
		public Particle(){
			this(600*Math.random(), 600*Math.random());
		}
		
		public void updatePosition(double theta){
			this.pos.setLocation(this.pos.getX() + L*Math.cos(theta), 
					this.pos.getY() + L*Math.sin(theta));

		}
		public Point2D getPosition(){
			return pos;
		}
	}
}
