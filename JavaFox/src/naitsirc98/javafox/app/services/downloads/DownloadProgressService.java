package naitsirc98.javafox.app.services.downloads;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DownloadProgressService extends Service<Void> {
	
	private String path;
	private double size;

	DownloadProgressService() {}
	
	public void start(String path, double size) {
		this.path = path;
		this.size = size;
		super.start();
	}
	
	@Override
	protected Task<Void> createTask() {
		return new DownloadProgressTask();
	}
	
	private class DownloadProgressTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			
			final File file = new File(path);
			
			double old = 0;
			double length;
			
			updateProgress(0.0,size);
			
			while((length = file.length()) < size) {
				
				final double speed = (length - old) / 1024;
				
				final String message = String.format("Downloaded %.2f of %.2f MB (%.1f KB/s)", 
						length/1024/1024, size/1024/1024, speed);
				
				updateProgress(length, size);
				updateMessage(message);
				
				System.out.println("message = "+message);
				
				old = length;
				
				Thread.sleep(1000);
				
			}
			
			return null;
		}
		
		
	}
	
	
}