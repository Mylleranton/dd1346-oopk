package pack;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pack.Model.Particle;

/**
 * @author anton
 * Controller class for MVC implementation which implements all control of MV and
 * also adds Slider and Button Swing components
 */
public class Controller extends JPanel implements ChangeListener, ActionListener {

	// MV object
	private Model model;
	private View view;

	// Slider min/max values
	private static final int L_MAX = 20;
	private static final int L_MIN = 0;
	private static final int DELTA_MAX = 100;
	private static final int DELTA_MIN = 0;
	private static double DELTA = DELTA_MAX;

	// Slider and log button objects
	private JSlider LSlider;
	private JSlider DeltaSlider;
	private JButton logButton;

	// Timer and timestamps
	private Timer timer;
	private double timeElapsed = 0;

	// Filewriter and format for CSV
	private BufferedWriter buffWriter;
	private final String logFile;
	private final DecimalFormat df = new DecimalFormat(".#####");

	/**
	 * Initializes a controller for m and v
	 * @param m Model that is controlled
	 * @param v View that is controlled
	 */
	public Controller(Model m, View v) {
		model = m;
		view = v;

		// Initialize the graphics
		initGraphics();

		// Start the timer
		timer = new Timer((int) Math.floor(1000 - (9.5 * DELTA)), this);
		timer.start();

		// Logfile and init the writer
		logFile = System.getProperty("user.dir") + "/src/pack/log.csv";
		try {
			buffWriter = new BufferedWriter(new FileWriter(logFile, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialized the graphics (L and Delta Sliders and log-button)
	 * Called on construction
	 */
	private void initGraphics() {
		// L and Delta-sliders
		LSlider = new JSlider(SwingConstants.HORIZONTAL, L_MIN, L_MAX, (L_MIN + L_MAX) / 2);
		DeltaSlider = new JSlider(SwingConstants.HORIZONTAL, DELTA_MIN, DELTA_MAX, (DELTA_MAX + DELTA_MIN) / 2);

		// ChangeListeners
		LSlider.addChangeListener(this);
		DeltaSlider.addChangeListener(this);

		// Slider graphics preferences
		LSlider.setMajorTickSpacing(2);
		LSlider.setPaintTicks(true);
		LSlider.setPaintLabels(true);
		DeltaSlider.setMajorTickSpacing(10);
		DeltaSlider.setPaintTicks(true);
		DeltaSlider.setPaintLabels(true);

		// Log-Button and button preferences
		logButton = new JButton("Logga positioner");
		logButton.setEnabled(true);
		logButton.addActionListener(this);

		// Use simple GridLayout, add sliders, labels and button
		setLayout(new GridLayout(3, 2));
		this.add(DeltaSlider);
		this.add(LSlider);
		this.add(new JLabel("Hastighet"));
		this.add(new JLabel("Förflyttningssteg"));
		this.add(logButton);
	}

	/**
	 * Logs the positions of all particles if called and write data to logFile
	 */
	private void logPositions() {
		// Stringbuilder for efficiency
		StringBuilder sb = new StringBuilder();
		ArrayList<Point2D> positions = model.getParticlePositions();

		// Append timestamp
		sb.append(df.format(timeElapsed));
		sb.append(",");

		// Append all positions and linebreak
		for (Point2D pos : positions) {
			sb.append(df.format(pos.getX()));
			sb.append(",");
			sb.append(df.format(pos.getY()));
			sb.append(",");
		}
		sb.append("\n");

		// Try writing to file
		try {
			buffWriter.write(sb.toString());
			buffWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if new particles are stuck and in that case,
	 * removes them from the free particle list and adds them to the 
	 * stuck particle list
	 */
	private void checkIfStuck() {
		ArrayList<Particle> parts = model.getParticles();
		ArrayList<Particle> stuckParts = model.getStuckParticles();

		// Memory optimisation
		ArrayList<Point2D> stuckPos = new ArrayList<Point2D>();
		for (Particle part : stuckParts) {
			stuckPos.add(part.getPosition());
		}

		for (int i = 0; i < parts.size(); i++) {
			Particle p = parts.get(i);

			// Stuck along edges of simulation
			if ((p.getPosition().getX() >= View.SIZE) || (p.getPosition().getY() >= View.SIZE)
					|| (p.getPosition().getX() <= 0) || (p.getPosition().getY() <= 0)) {

				parts.remove(p);
				stuckParts.add(p);
				continue;
			}
			// Stuck along other object
			else if (Math.abs((Math.pow(p.getPosition().getX() - 300, 2) + Math.pow(p.getPosition().getY() - 300, 2))
					- (150 * 150)) < 5) {

				parts.remove(p);
				stuckParts.add(p);
				continue;
			}

			// Stuck along other stuck particles
			for (Particle stuck_ps : stuckParts) {
				if (stuck_ps.getPosition().distanceSq(p.getPosition()) <= 9) {
					parts.remove(p);
					stuckParts.add(p);
					stuckPos.add(p.getPosition());
					break;
				}
			}
		}
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 * State changed for either slider, handles parameter changing and timer configurations
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == LSlider) {
			JSlider source = (JSlider) e.getSource();
			Model.setL(source.getValue());
		} else if (e.getSource() == DeltaSlider) {
			JSlider source = (JSlider) e.getSource();
			DELTA = source.getValue();
			timer.setDelay((int) Math.floor(1000 - (9.5 * DELTA)));
			System.out.println("Timer interval:" + timer.getDelay());
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * Triggered on button pressed and changes logging, and handles timer triggering
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == logButton) {
			MyFrame.LOG = MyFrame.LOG ? false : true;
			System.out.println("Logg: " + MyFrame.LOG);
			logButton.setText(MyFrame.LOG ? "Loggar data" : "Loggar inte data");
			return;
		}

		// Log positions if enabled
		timeElapsed += (1000 - (9.5 * DELTA)) / 1000;
		if ((timeElapsed <= 15) && MyFrame.LOG) {
			logPositions();
		}

		// Check particles for stuck, update postions and repaint canvas
		checkIfStuck();
		model.updateParticles();
		view.repaint();
	}

}
