package pack;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Controller extends JPanel implements ChangeListener, ActionListener {

	private Model model;
	private View view;
	
	private static final int L_MAX = 10;
	private static final int L_MIN = 0;
	private static final int DELTA_MAX = 99;
	private static final int DELTA_MIN = 0;
	
	private static double DELTA = DELTA_MAX;
	
	private JSlider LSlider;
	private JSlider DeltaSlider;
	
	private Timer timer;
	private double timeElapsed = 0;
	
	
	private FileWriter fileWriter;
	private final String logFile;
	private final DecimalFormat df = new DecimalFormat(".#####");
	
	public Controller(Model m, View v) {
		this.model = m;
		this.view = v;
		initGraphics();
		
		timer = new Timer(1000-(int) (10*Math.floor(DELTA)), this);
		timer.start();
		
		logFile = System.getProperty("user.dir") + "/src/pack/log.txt";
		try {
			fileWriter = new FileWriter(logFile, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initGraphics() {
		LSlider = new JSlider(JSlider.HORIZONTAL,
				L_MIN, L_MAX,L_MIN);
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
		
		this.setLayout(new GridLayout(2,2));
		this.add(DeltaSlider);
		this.add(LSlider);
		this.add(new JLabel("Hastighet"));
		this.add(new JLabel("Förflyttningssteg"));
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
			fileWriter.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
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
		timeElapsed += (1000-10*DELTA)/1000;
		if(timeElapsed <= 10) {
			logPositions(model);
		}
		model.updateParticles();
		view.repaint();
	}
	
	
}
