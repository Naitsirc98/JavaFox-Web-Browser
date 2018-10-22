package naitsirc98.javafox.app.web.downloads;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
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
	}
	
	public void remove(final Download download) {
		downloads.remove(download);
	}
	
	public int pendingDownloads() {
		return downloads.size();
	}
	
	public void display() {
		
		Stage window = new Stage();
		
		ListView<DownloadView> view = new ListView<>();
		
		downloads.forEach(d -> {
			
			DownloadView dv = new DownloadView(d);
			
			view.getItems().add(dv);
			
		});
		
		Scene scene = new Scene(view, 400, 300);
		
		window.setScene(scene);
		
		window.sizeToScene();
		
		window.show();
		
		window.focusedProperty().addListener((observable, old, neww) -> {
			
			if(!neww) {
				window.close();
			}
			
		});
		
		
	}

}
