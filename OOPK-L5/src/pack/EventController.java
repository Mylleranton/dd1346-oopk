package pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;

public class EventController implements ActionListener, ChangeListener<State> {
	private MenuBar mbar;
	private ContentPane cpane;

	private Stack<String> backSites;
	private Stack<String> fwdSites;
	public String currentSite;
	private boolean engineLog = true;
	
	public EventController(MenuBar mbar, ContentPane cpane) {
		this.mbar = mbar;
		this.cpane = cpane;
		
		backSites = new Stack<String>();
		fwdSites = new Stack<String>();
		mbar.setActionListener(this);
		
		Platform.runLater(() -> {
			cpane.wv.getEngine().getLoadWorker().stateProperty().addListener(this);
		});
		updateFBButtons();

	}
	
	
	private void loadWebpage(String newPage){
		if (!currentSite.equals(newPage)) {
			//backSites.push(currentSite);
		}
		//currentSite = newPage;
		cpane.setWebpage(newPage);
		mbar.addressField.setText(newPage);
	}
	
	private void loadPreviousWebpage() {
		String prevPage;
		if (!backSites.isEmpty()) {
			prevPage = backSites.pop();
			fwdSites.push(currentSite);
			
			currentSite = prevPage;
			engineLog = false;
			cpane.setWebpage(prevPage);
			mbar.addressField.setText(prevPage);
		}
	}
	
	private void loadForwardWebpage(){
		String fwdPage;
		if(!fwdSites.isEmpty()){
			fwdPage = fwdSites.pop();
			backSites.push(currentSite);
			
			currentSite = fwdPage;
			engineLog = false;
			cpane.setWebpage(fwdPage);
			mbar.addressField.setText(fwdPage);
		}
	}
	
	private void updateFBButtons(){
		boolean fwdButton = !fwdSites.isEmpty();
		boolean backButton = !backSites.isEmpty();
		
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
			
			if ((currentSite != null && !currentSite.equals(cpane.wv.getEngine().getLocation()))
					&& engineLog) {
				backSites.push(currentSite);
			}
			currentSite = cpane.wv.getEngine().getLocation();
			mbar.addressField.setText(currentSite);
			updateFBButtons();
		}		
	}

}
