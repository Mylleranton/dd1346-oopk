package pack;

import java.awt.BorderLayout;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ContentPane extends JPanel {

	public WebView webView;
	private JFXPanel jfxPanelWeb;
	public JFXPanel jfxPanelProgress;
	private ProgressBar progressBar;
	
	public ContentPane() {
		super();
		
		Dimension scSize = new Dimension(MainFrame.MAIN_WIDTH, MainFrame.HEIGHT-MenuBar.HEIGHT-100);
		this.setPreferredSize(scSize);
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(0,0,0,0);

		
		// TEST JAVA FX
		jfxPanelWeb = new JFXPanel();
		progressBar = new ProgressBar();
		jfxPanelProgress = new JFXPanel();
		
		Platform.runLater(() -> {
			webView = new WebView();
			jfxPanelWeb.setScene(new Scene(webView));
			webView.getEngine().setUserAgent(webView.getEngine().getUserAgent().replaceAll("Macintosh; ", ""));

			jfxPanelProgress.setScene(new Scene(progressBar));
			progressBar.progressProperty().bind(webView.getEngine().getLoadWorker().progressProperty());
			
		});
		this.add(jfxPanelWeb,c);
		
		c.gridy = 1; 
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0,0,0,0);
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.weightx = 0; c.weighty = 0;
		this.add(jfxPanelProgress, c);
		this.setVisible(true);
	}
	
	public void setWebpage(String url) {
		Platform.runLater(() -> {
			webView.getEngine().load(validateURL(url));
		});
	}

	private String validateURL(String url) {
		if (!url.startsWith("http")) {
			return "http://" + url;
		}
		return url;
	}

}
