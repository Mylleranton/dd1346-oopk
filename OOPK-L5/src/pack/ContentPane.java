package pack;

import java.awt.BorderLayout;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;

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
	public JFXPanel jfxHistoryPanel;
	private ListView<WebHistory.Entry> historyList;

	
	public ContentPane() {
		super();
		setupGraphics();
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
	
	private void setupGraphics(){
		Dimension scSize = new Dimension(MainFrame.MAIN_WIDTH, MainFrame.HEIGHT-MenuBar.HEIGHT-100);
		this.setPreferredSize(scSize);
		this.setLayout(new GridBagLayout());
		
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

		jfxHistoryPanel = new JFXPanel();
		jfxHistoryPanel.setPreferredSize(new Dimension(230,MainFrame.HEIGHT-MenuBar.HEIGHT-100));
		
		Platform.runLater(() -> {
			WebHistory his = webView.getEngine().getHistory();
			historyList = new ListView<WebHistory.Entry>();
			historyList.setItems(his.getEntries());
			historyList.setCellFactory(param -> new ListCell<WebHistory.Entry>() {
				@Override
				protected void updateItem(WebHistory.Entry item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || item.getUrl() == null || empty) {
						setText(null);
					}
					else {
						this.setText(item.getUrl());
						
						this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								if (event.getClickCount() > 1) {
									ListCell<Entry> a = (ListCell<Entry>) event.getSource();
									System.out.println("Dubbelklick i historik, byter sida");
									webView.getEngine().load(a.getText());
								}
							}
						});
						
					}
				}
			});
			
			jfxHistoryPanel.setScene(new Scene(historyList));
		});
		addGraphics();
		jfxHistoryPanel.setVisible(false);
		
	}
	
	private void addGraphics(){
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(0,0,0,0);
		this.add(jfxPanelWeb,c);
		
		c.gridx = 1; c.gridy = 1; 
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0,0,0,0);
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.weightx = 0; c.weighty = 0;
		this.add(jfxPanelProgress, c);
		
		c.gridx = 0; c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 0.5;
		c.anchor = GridBagConstraints.NORTHWEST;
		this.add(jfxHistoryPanel, c);

		this.setVisible(true);

	}

}
