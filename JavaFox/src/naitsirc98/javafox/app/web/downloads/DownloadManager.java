package naitsirc98.javafox.app.web.downloads;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import naitsirc98.javafox.app.gui.WebToolBar;
import naitsirc98.javafox.app.gui.downloads.DownloadView;

public final class DownloadManager {
	
	private static DownloadManager manager;
	
	public static DownloadManager getManager() {
		
		if(manager == null) {
			
			synchronized(DownloadManager.class) {
				manager = new DownloadManager();
			}
		}
		
		return manager;

	}
	
	private final Deque<Download> downloads;

	private DownloadManager() {
		downloads = new ArrayDeque<>();
	}
	
	public void add(final Download download) {
		downloads.push(download);
		WebToolBar.getToolBar().enableDownloadsButton();
	}
	
	public void remove(final Download download) {
		downloads.remove(download);
	}
	
	public long pendingDownloads() {
		return downloads.stream().filter(d -> !d.isComplete()).count();
	}
	
	public void display(double x, double y) {
		
		Stage window = new Stage();
		
		window.setTitle("Downloads");
		
		ListView<DownloadView> view = new ListView<>();
		
		downloads.forEach(d -> {
			
			DownloadView dv = new DownloadView(d);
			
			view.getItems().add(dv);
			
		});
		
		Scene scene = new Scene(view, 400, 300);
		
		window.setScene(scene);
		
		window.sizeToScene();
		
		window.setX(x-300);
		window.setY(y+85);
		
		window.show();
		
		window.focusedProperty().addListener((observable, old, neww) -> {
			
			if(!neww) {
				window.close();
			}
			
		});
		
		
	}

}
