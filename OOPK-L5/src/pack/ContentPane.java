package pack;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
/**
 * The ContentPane holds all components in the main display area. Holds a WebView, a ProgressBar and a History pane
 * @author anton
 *
 */
public class ContentPane extends JPanel {

	/**
	 *  Main Browser instance
	 */
	public WebView webView;
	/**
	 *  JFXPanel for the WebView
	 */
	private JFXPanel jfxPanelWeb;
	/**
	 *  JFXPanel for the ProgressBar
	 */
	public JFXPanel jfxPanelProgress;
	/**
	 *  JFXPanel for the WebHistory panel
	 */
	public JFXPanel jfxHistoryPanel;
	
	/**
	 * ProgressBar for loading webpages in the WebView
	 */
	private ProgressBar progressBar;
	
	/**
	 * ListView that holds the WebHistory items dynamically
	 */
	private ListView<WebHistory.Entry> historyList;

	/**
	 * Initiates the ContentPane and sets up the graphical elements
	 */
	public ContentPane() {
		// Super call for JPanel
		super();
		// Setup graphics
		setupGraphics();
	}

	/**
	 * Load new webpage in the WebView
	 * 
	 * @param url
	 *            The URL to be loaded
	 */
	public void setWebpage(String url) {
		Platform.runLater(() -> {
			webView.getEngine().load(validateURL(url));
		});
	}

	/**
	 * Vadidates the given url and returns a valid copy
	 * 
	 * @param url
	 *            The URL to be validated
	 * @return
	 */
	private String validateURL(String url) {
		if (!url.startsWith("http")) {
			return "http://" + url;
		}
		return url;
	}

	/**
	 * Initiates graphics and setup actionlisteners for all components.
	 * Also binds the progressbar to the WebView LoadWorker.progressProperty
	 */
	private void setupGraphics() {
		// Dimensions and layout
		Dimension scSize = new Dimension(MainFrame.MAIN_WIDTH, ImageObserver.HEIGHT - ImageObserver.HEIGHT - 100);
		setPreferredSize(scSize);
		setLayout(new GridBagLayout());

		// JavaFX components, browser and progressbar
		jfxPanelWeb = new JFXPanel();
		progressBar = new ProgressBar();
		jfxPanelProgress = new JFXPanel();

		// Initiate WebView (browser) and progressbar
		Platform.runLater(() -> {
			webView = new WebView();
			jfxPanelWeb.setScene(new Scene(webView));
			webView.getEngine().setUserAgent(webView.getEngine().getUserAgent().replaceAll("Macintosh; ", ""));

			jfxPanelProgress.setScene(new Scene(progressBar));
			progressBar.progressProperty().bind(webView.getEngine().getLoadWorker().progressProperty());

		});

		// History pane
		jfxHistoryPanel = new JFXPanel();
		jfxHistoryPanel.setPreferredSize(new Dimension(230, ImageObserver.HEIGHT - ImageObserver.HEIGHT - 100));

		// Init web history graphics
		Platform.runLater(() -> {
			WebHistory his = webView.getEngine().getHistory();
			historyList = new ListView<WebHistory.Entry>();
			historyList.setItems(his.getEntries());
			historyList.setCellFactory(param -> new ListCell<WebHistory.Entry>() {
				@Override
				protected void updateItem(WebHistory.Entry item, boolean empty) {
					super.updateItem(item, empty);

					// Display the URL of the page visited
					if ((item == null) || (item.getUrl() == null) || empty) {
						setText(null);
					} else {
						setText(item.getUrl());

						// On click -> goto that webpage
						this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								if (event.getClickCount() > 1) {
									ListCell<Entry> a = (ListCell<Entry>) event.getSource();
									// System.out.println("Dubbelklick i
									// historik, byter sida");
									webView.getEngine().load(a.getText());
								}
							}
						});

					}
				}
			});

			jfxHistoryPanel.setScene(new Scene(historyList));
		});
		// Add all graphics to the contentpane, and set history pane to
		// invisible
		addGraphics();
		jfxHistoryPanel.setVisible(false);

	}

	/**
	 * Adds all individual components to the ContentPane with the correct layout.
	 * Uses the GridBagLayout.
	 */
	private void addGraphics() {
		GridBagConstraints c = new GridBagConstraints();

		// WebView (main browser)
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(jfxPanelWeb, c);

		// Progress Bar
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.weightx = 0;
		c.weighty = 0;
		this.add(jfxPanelProgress, c);

		// History pane
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.NORTHWEST;
		this.add(jfxHistoryPanel, c);

		setVisible(true);

	}

}
