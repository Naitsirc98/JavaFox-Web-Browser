package naitsirc98.javafox.app.services.downloads;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import naitsirc98.javafox.app.config.UserConfig;

public final class DownloadService extends Service<Void> {
	
	private final DownloadProgressService downloadProgress;
	
	private final String url;
	private final String filename;
	private final double size;
	
	public DownloadService(String url, String filename, double size) {
		this.url = url;
		this.filename = filename;
		this.size = size;
		downloadProgress = new DownloadProgressService();
	}
	
	public DownloadProgressService getProgressService() {
		return downloadProgress;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new DownloadTask();
	}
	
	private class DownloadTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			
			downloadProgress.messageProperty().addListener((observable, old, neww) -> {
				updateMessage(neww);
			});
			
			System.out.println("downloading = "+filename);
			
			final String path = UserConfig.getConfig().getString("downloads")+filename;
			
			try(final ReadableByteChannel reader 
					= Channels.newChannel(new URL(url).openStream());
					FileOutputStream file = new FileOutputStream(path)) {
				
				FileChannel fileChannel = file.getChannel();
				
				downloadProgress.start(path, size);
				
				fileChannel.transferFrom(reader, 0, Long.MAX_VALUE);
				
				fileChannel.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			updateMessage("Download complete");
			
			updateProgress(1, 1);
			
			System.out.println("file downloaded as location "+path);
			
			return null;
		}

	}

	
	



}
