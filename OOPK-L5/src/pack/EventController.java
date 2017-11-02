package pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebHistory;
/**
 * 
 * Controller for all actions that the browser can take. 
 * Handles e.g. going back/forward in history, entering of new webpage showing/hiding the history pane etc.
 * @author anton
 *
 */
public class EventController implements ActionListener, ChangeListener<State> {
	/**
	 * MenuBar instance that the EventController controls
	 */
	private MenuBar mbar;
	/**
	 * ContentPane instance that the EventController controls
	 */
	private ContentPane cpane;
	/**
	 * MainFrame instance that instansiated the EventController
	 */
	private MainFrame frame;
	/**
	 * Reference to the ContentPanes' WebViews' WebHistory
	 */
	private WebHistory webHistory;
	/**
	 * ObservableList that holds the WebHistory Entries 
	 */
	private ObservableList<WebHistory.Entry> historyEntries;
	
	/**
	 * Reference to the Exceptionlistener for the ContentPanes' WebView
	 */
	private ExceptionChangeListener exChangeListener;

	/**
	 * Initiates the controller with the objects that it controls and registers actionlisteners
	 * @param mbar MenuBar instance
	 * @param cpane ContentPane instance
	 * @param frame MainFrame instance
	 */
	public EventController(MenuBar mbar, ContentPane cpane, MainFrame frame) {
		this.mbar = mbar;
		this.cpane = cpane;
		this.frame = frame;
		
		// Register Action- and ExceptionChangeListeners
		exChangeListener = new ExceptionChangeListener();
		mbar.setActionListener(this);
		
		// Initiate the History and register listeners
		Platform.runLater(() -> {
			webHistory = cpane.webView.getEngine().getHistory();
			historyEntries = webHistory.getEntries();
			cpane.webView.getEngine().getLoadWorker().stateProperty().addListener(this);
			cpane.webView.getEngine().getLoadWorker().exceptionProperty().addListener(exChangeListener);
		});
	}

	/**
	 * Load a new webpage into the WebView
	 * @param newPage the page URL to be loaded
	 */
	private void loadWebpage(String newPage) {
		cpane.setWebpage(newPage);
	}
	
	/**
	 * Go back one page in the WebHistory.
	 * If no history is present, it does nothing.
	 */
	private void loadPreviousWebpage() {
		int currentIndex = webHistory.getCurrentIndex();
		Platform.runLater(() -> {
			webHistory.go((historyEntries.size() > 1) && (currentIndex > 0) ? -1 : 0);
		});
	}
	/**
	 * Go forward one page in the WebHistory.
	 * If there is no forward page, it does nothing.
	 */
	private void loadForwardWebpage() {
		int currentIndex = webHistory.getCurrentIndex();
		Platform.runLater(() -> {
			webHistory.go((historyEntries.size() > 1) && (currentIndex < (historyEntries.size() - 1)) ? 1 : 0);
		});
	}

	/**
	 * Update the Back and Forward buttons
	 * Buttons are only active if there exists a forward/backward page to go to, 
	 * otherwise, they are disabled.
	 */
	private void updateFBButtons() {
		int hisSize = historyEntries.size();
		int curIndex = webHistory.getCurrentIndex();
		boolean fwdButton = curIndex < (hisSize - 1);
		boolean backButton = curIndex > 0;
		
		// Set the Back and Forward buttons enabled/disabled
		mbar.fwdButton.setEnabled(fwdButton);
		mbar.fwdButton.setBorderPainted(fwdButton);
		mbar.backButton.setEnabled(backButton);
		mbar.backButton.setBorderPainted(backButton);
	}

	/**
	 * Toogles the visibility of the history pane
	 */
	private void toggleHistory() {
		cpane.jfxHistoryPanel.setVisible(!cpane.jfxHistoryPanel.isVisible());
	}

	/**
	 * ActionListener for the addressfield, back/forward buttons and the history button.
	 * Makes sure that their onClick actions are correctly handled.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Addressfield
		if (e.getSource() == mbar.addressField) {
			//System.out.println("Addressfältet uppdaterades");
			loadWebpage(mbar.addressField.getText());

		//	Forward Button	
		} else if (e.getSource() == mbar.fwdButton) {
			//System.out.println("FWD button klickades");
			loadForwardWebpage();
			
		// Back Button
		} else if (e.getSource() == mbar.backButton) {
			//System.out.println("BACK button klickades");
			loadPreviousWebpage();
			
		// History Button
		} else if (e.getSource() == mbar.historyButton) {
			//System.out.println("History button klickades");
			toggleHistory();
		}
	}
	/**
	 * WebView.LoadWorker StateChangeListener
	 * If Worker.STATE == SCHEDULED, then the progressbar is initiated.
	 * If Worker.STATE == SUCCEDED, then the progressbar is disabled, and the text of the address bar is set to the current page.
	 */
	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
		if (newValue == Worker.State.SCHEDULED) {
			cpane.jfxPanelProgress.setVisible(true);

			updateFBButtons();
		} else if (newValue == Worker.State.SUCCEEDED) {
			cpane.jfxPanelProgress.setVisible(false);
			mbar.setURLText(cpane.webView.getEngine().getLocation());
		}
	}
	/**
	 * ExceptionChangeListener for invalid URLs and Connectivity
	 * Displays error to user if exceptions register during page loading
	 * @author anton
	 *
	 */
	private class ExceptionChangeListener implements ChangeListener<Throwable> {
		/**
		 * On exception registered, display error to user
		 */
		@Override
		public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable newValue) {
			if (newValue != null) {
				//System.out.println("HEJ");
				SwingUtilities.invokeLater(() -> {
					frame.errorPane(newValue.getMessage());
				});
			}
		}
	}

}
