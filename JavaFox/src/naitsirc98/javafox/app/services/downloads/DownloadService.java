package naitsirc98.javafox.app.services.downloads;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import naitsirc98.javafox.app.user.config.UserConfig;

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
		setOnCancelled(e -> System.out.println("Download cancelled"));
		setOnSucceeded(e -> System.out.println("Download complete"));
	}

	public DownloadProgressService getProgressService() {
		return downloadProgress;
	}

	@Override
	public boolean cancel() {
		if(downloadProgress != null) {
			downloadProgress.cancel();
		}
		return super.cancel();
	}

	@Override
	protected Task<Void> createTask() {
		return new DownloadTask();
	}

	private class DownloadTask extends Task<Void> {

		public DownloadTask() {
			setOnScheduled(e -> updateMessage("Preparing for download..."));
			setOnCancelled(e -> updateMessage("Download cancelled"));
			setOnFailed(e -> updateMessage("Download failed"));
			setOnSucceeded(e -> updateMessage("Download complete"));
		}


		@Override
		protected Void call() throws Exception {

			downloadProgress.messageProperty().addListener((observable, old, neww) -> {
				updateMessage(neww);
			});

			final String path = UserConfig.getConfig().getString("downloads").concat(filename);
			
			System.out.println("Downloading ".concat(filename));

			try(final ReadableByteChannel reader 
					= Channels.newChannel(new URL(url).openStream());
					final FileChannel fileChannel 
					= new FileOutputStream(path).getChannel();) {

				downloadProgress.start(path, size);

				fileChannel.transferFrom(reader, 0L, Long.MAX_VALUE);

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

	}






}
