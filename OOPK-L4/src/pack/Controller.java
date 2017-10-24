package pack;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pack.Model.Particle;

public class Controller extends JPanel implements ChangeListener, ActionListener {

	private Model model;
	private View view;
	
	private static final int L_MAX = 20;
	private static final int L_MIN = 0;
	private static final int DELTA_MAX = 99;
	private static final int DELTA_MIN = 0;
	
	private static double DELTA = DELTA_MAX;
	
	private JSlider LSlider;
	private JSlider DeltaSlider;
	private JButton logButton;
	
	private Timer timer;
	private double timeElapsed = 0;
	
	
	private BufferedWriter buffWriter;
	private final String logFile;
	private final DecimalFormat df = new DecimalFormat(".#####");
	
	public Controller(Model m, View v) {
		this.model = m;
		this.view = v;
		initGraphics();
		
		timer = new Timer(1000-(int) (10*Math.floor(DELTA)), this);
		timer.start();
		
		logFile = System.getProperty("user.dir") + "/src/pack/log.csv";
		try {
			buffWriter = new BufferedWriter(new FileWriter(logFile, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private void initGraphics() {
		LSlider = new JSlider(JSlider.HORIZONTAL,
				L_MIN, L_MAX,(L_MIN+L_MAX)/2);
		DeltaSlider = new JSlider(JSlider.HORIZONTAL,
				DELTA_MIN, DELTA_MAX,DELTA_MAX);
		
		LSlider.addChangeListener(this);
		DeltaSlider.addChangeListener(this);
		LSlider.setMajorTickSpacing(2);
		LSlider.setPaintTicks(true);
		LSlider.setPaintLabels(true);
		DeltaSlider.setMajorTickSpacing(10);
		DeltaSlider.setPaintTicks(true);
		DeltaSlider.setPaintLabels(true);
		
		logButton = new JButton("Logga positioner");
		logButton.setEnabled(true);
		logButton.addActionListener(this);
		
		this.setLayout(new GridLayout(3,2));
		this.add(DeltaSlider);
		this.add(LSlider);
		this.add(new JLabel("Hastighet"));
		this.add(new JLabel("Förflyttningssteg"));
		this.add(logButton);
	}
	
	private void logPositions(Model model){
		StringBuilder sb = new StringBuilder();
		Point2D[] positions = model.getParticlePositions();
		
		sb.append(df.format(timeElapsed));
		sb.append(",");
		for(int i = 0; i<positions.length; i++) {
			sb.append(df.format(positions[i].getX()));
			sb.append(",");
			sb.append(df.format(positions[i].getY()));
			sb.append(",");
		}
		sb.append("\n");
		try {
			buffWriter.write(sb.toString());
			buffWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void checkIfStuck(){
		ArrayList<Particle> parts = model.getParticles();
		ArrayList<Particle> stuckParts = model.getStuckParticles();

		// Memory optimisation
		ArrayList<Point2D> stuckPos = new ArrayList<Point2D>();
		for(Particle part : stuckParts){
			stuckPos.add(part.getPosition());
		}
		
		
		for (int i = 0; i < parts.size(); i++){
			Particle p = parts.get(i);
			
			// Stuck along edges of simulation
			if(p.getPosition().getX() >= View.SIZE ||
					p.getPosition().getY() >= View.SIZE ||
					p.getPosition().getX() <= 0 ||
					p.getPosition().getY() <= 0) {
				parts.remove(i);
				stuckParts.add(p);
				continue;
			}
			// Stuck along other object
			else if (Math.abs(Math.pow(p.getPosition().getX()-300,2) + Math.pow(p.getPosition().getY()-300,2) - 150*150) < 5) {
				//System.out.println("Obj is on edge");
				parts.remove(i);
				stuckParts.add(p);
				continue;
			}
			
			// Stuck along other stuck particles
			for(int j = 0; j<stuckPos.size(); j++){
				Point2D stuck_ps = stuckPos.get(j);
				if(stuck_ps.distance(p.getPosition()) <= 3){
					parts.remove(i);
					stuckParts.add(p);
					stuckPos.add(p.getPosition());
					break;
				}
			}	
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		//System.out.println("Slider changed");
		if(e.getSource() == LSlider) {
			JSlider source = (JSlider) e.getSource();
			Model.setL((double) source.getValue());
		}
		else if(e.getSource() == DeltaSlider) {
			JSlider source = (JSlider) e.getSource();
			DELTA = (double) source.getValue();
			timer.setDelay(1000-(int) (10*Math.floor(DELTA)));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == logButton){
			MyFrame.LOG = MyFrame.LOG ? false : true; 
			System.out.println("Logg: " + MyFrame.LOG);
			logButton.setText(MyFrame.LOG ? "Loggar data" : "Loggar inte data");
			return;
		}
		timeElapsed += (1000-10*DELTA)/1000;
		//System.out.println(timeElapsed);
		if(timeElapsed <= 0.5 && MyFrame.LOG) {
			logPositions(model);
		}
		checkIfStuck();
		model.updateParticles();
		view.repaint();
	}
	
	
}
