package pack;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Model {
	private static double L = 1;
	private int particles;
	private ArrayList<Particle> partArray;
	private ArrayList<Particle> stuckPartArray;
	
	public Model(int particles_in){
		this.particles = particles_in;
		
		partArray = new ArrayList<Particle>();
		stuckPartArray = new ArrayList<Particle>();
		
		for(int i = 0; i<particles; i++) {
			partArray.add(new Particle());
		}
	}
	
	public void updateParticles(){
		for(int i = 0; i<partArray.size();i++){
			partArray.get(i).updatePosition((2*Math.PI)*Math.random());
		}
	}
	public Point2D[] getParticlePositions(){
		Point2D[] posArray = new Point2D[particles];
		int i = 0;
		while(i<partArray.size()){
			posArray[i] = partArray.get(i).getPosition();
			i += 1;
		}
		int j = 0;
		while(j<stuckPartArray.size()){
			posArray[i] = stuckPartArray.get(j).getPosition();
			j += 1;
			i += 1;
		}
		System.out.println("Ett anrop");
		return posArray;
	}

	public ArrayList<Particle> getParticles(){
		return this.partArray;
	}
	public ArrayList<Particle> getStuckParticles(){
		return this.stuckPartArray;
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
