package pack;

import java.awt.BorderLayout;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
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
	
	//private JEditorPane edPane;
	//private JScrollPane scPane;
	public WebView wv;
	private JFXPanel jfxPanel;
	
	public ContentPane() {
		super();
		Dimension scSize = new Dimension(MainFrame.MAIN_WIDTH, MainFrame.HEIGHT-MenuBar.HEIGHT-100);
		this.setPreferredSize(scSize);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(0,0,10,0);
		
		//edPane = new JEditorPane();
		//scPane = new JScrollPane();

		//edPane.setEditable(false);
		//edPane.setBackground(Color.WHITE);

		//scPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//scPane.setEnabled(true);
		
		
		
		// TEST JAVA FX
		jfxPanel = new JFXPanel();
		//scPane.setViewportView(jfxPanel);
		Platform.runLater(() -> {
			wv = new WebView();
			jfxPanel.setScene(new Scene(wv));
			wv.getEngine().setUserAgent(wv.getEngine().getUserAgent().replaceAll("Macintosh; ", ""));
		});
		
		
		this.add(jfxPanel,c);
		this.setVisible(true);
	}
	
	public void setWebpage(String url) {
		//edPane.setPage(new URL(url));
		
		Platform.runLater(() -> {
			wv.getEngine().load(validateURL(url));
		});
		//scPane.validate();
	}

	private String validateURL(String url) {
		if (!url.startsWith("http")) {
			return "http://" + url;
		}
		return url;
	}

}
