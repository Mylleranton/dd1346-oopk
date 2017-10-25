package pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebHistory;

public class EventController implements ActionListener, ChangeListener<State> {
	private MenuBar mbar;
	private ContentPane cpane;
	private MainFrame frame;

	private WebHistory webHistory;
	private ObservableList<WebHistory.Entry> historyEntries;
	
	private ExceptionChangeListener exChangeListener;
	
	public EventController(MenuBar mbar, ContentPane cpane, MainFrame frame) {
		this.mbar = mbar;
		this.cpane = cpane;
		this.frame = frame;
		exChangeListener = new ExceptionChangeListener();
		
		mbar.setActionListener(this);		
		
		Platform.runLater(() -> {
			webHistory = cpane.webView.getEngine().getHistory();
			historyEntries = webHistory.getEntries();
			cpane.webView.getEngine().getLoadWorker().stateProperty().addListener(this);
			cpane.webView.getEngine().getLoadWorker().exceptionProperty().addListener(exChangeListener);
		});
	}	
	private void loadWebpage(String newPage){

		cpane.setWebpage(newPage);
		mbar.addressField.setText(newPage);
	}
	
	private void loadPreviousWebpage() {
		int currentIndex = webHistory.getCurrentIndex();
		Platform.runLater(() -> {
			webHistory.go(historyEntries.size() > 1 && currentIndex > 0 ? -1 : 0);
		});
		mbar.addressField.setText(historyEntries.get(currentIndex > 0 ? currentIndex-1 : currentIndex).getUrl());
	}
	
	private void loadForwardWebpage(){
		int currentIndex = webHistory.getCurrentIndex();
		Platform.runLater(() -> {
			webHistory.go(historyEntries.size() > 1 && currentIndex < historyEntries.size()-1 ? 1 : 0);
		});
		mbar.addressField.setText(historyEntries.get(currentIndex < historyEntries.size() -1 ? currentIndex+1 : currentIndex).getUrl());
	}
	
	private void updateFBButtons(){
		int hisSize = historyEntries.size();
		int curIndex = webHistory.getCurrentIndex();
		boolean fwdButton = curIndex < hisSize - 1;
		boolean backButton = curIndex > 0;
		
		System.out.println(fwdButton + " " + backButton);
		
		mbar.fwdButton.setEnabled(fwdButton);
		mbar.fwdButton.setBorderPainted(fwdButton);
		mbar.backButton.setEnabled(backButton);
		mbar.backButton.setBorderPainted(backButton);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == mbar.addressField) {
			System.out.println("Addressfältet uppdaterades");
			loadWebpage(mbar.addressField.getText());
		}
		else if (e.getSource() == mbar.fwdButton) {
			System.out.println("FWD button klickades");
			loadForwardWebpage();
		} 
		else if (e.getSource() == mbar.backButton) {
			System.out.println("BACK button klickades");
			loadPreviousWebpage();
		}
	}

	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
		if (newValue == Worker.State.SCHEDULED) {
			cpane.jfxPanelProgress.setVisible(true);

			updateFBButtons();
		} else if (newValue == Worker.State.SUCCEEDED) {
			cpane.jfxPanelProgress.setVisible(false);
		} 
	}
	
	private class ExceptionChangeListener implements ChangeListener<Throwable> {

		@Override
		public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable newValue) {
			if(newValue != null) {
				System.out.println("HEJ");
				SwingUtilities.invokeLater(() -> {
					frame.errorPane(newValue.getMessage());
				});
			}
		}
	}

}
