package naitsirc98.javafox.app.web.downloads;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import naitsirc98.javafox.app.config.UserConfig;
import naitsirc98.javafox.app.services.HTTPHeadRequestService;
import naitsirc98.javafox.app.services.downloads.DownloadService;

public final class Download {
	
	public static void newDownload(final String url) {
		
		final Download download = new Download();
		
		download.sendRequest(url);
		
		// Toolbar.getToolbar().addDownload(new DownloadView(download));
		
	}
	
	private final ReadOnlyStringWrapper filename = new ReadOnlyStringWrapper(this, "filename");
	private final ReadOnlyStringWrapper type = new ReadOnlyStringWrapper(this, "type");
	private final ReadOnlyDoubleWrapper size = new ReadOnlyDoubleWrapper(this, "size");
	
	private final ReadOnlyObjectWrapper<DownloadService> service = new ReadOnlyObjectWrapper<>(this, "service");

	private Download() {
		
	}
	
	public ReadOnlyObjectProperty<DownloadService> serviceProperty() {
		return service.getReadOnlyProperty();
	}
	
	public DownloadService getDownloadService() {
		return service.get();
	}
	
	public ReadOnlyStringProperty filenameProperty() {
		return filename.getReadOnlyProperty();
	}
	
	public ReadOnlyStringProperty typeProperty() {
		return type.getReadOnlyProperty();
	}
	
	public ReadOnlyDoubleProperty sizeProperty() {
		return size.getReadOnlyProperty();
	}
	
	public String getFilename() {
		return filename.get();
	}

	public String getType() {
		return type.get();
	}

	public double getSize() {
		return size.get();
	}


	private void sendRequest(String url) {
		
		final HTTPHeadRequestService request = new HTTPHeadRequestService();

		request.start(url, "Content-Disposition", "Content-Type", "Content-Length");

		request.setOnSucceeded(e -> {
			
			if(request.getValue().length < 3) {
				System.out.println("Download: couldn't get response");
				return;
			}
			
			System.out.println(Arrays.toString(request.getValue()));
			
			final String content = request.getValue()[0];
			type.set(request.getValue()[1]);
			size.set(Double.parseDouble(request.getValue()[2]));
			
			if(content != null) {
				filename.set(new String(
						content.substring(content.indexOf("\"")+1, content.lastIndexOf("\"")).getBytes(),
						StandardCharsets.ISO_8859_1));
			} else {
				filename.set("(Could not retrieve filename)");
			}
			
			final Alert dialog = new Alert(AlertType.CONFIRMATION);
			
			dialog.setTitle("Download file ".concat(filename.get()));
			
			dialog.setHeaderText("Do you want to download this file?");
			
			dialog.setContentText(String.format("Type: %s; Size: %.2f MB", type.get(), size.get()/1024/1024));
			
			dialog.showAndWait()
			.filter(response -> response == ButtonType.OK)
			.ifPresentOrElse(response -> start(url, filename.get(), size.get()),
					() -> System.out.println("Download cancelled"));

		});
		
	}

	private void start(String url, String filename, double size) {
		
		final boolean confirmation = checkIfAlreadyExists(filename);
		
		if(confirmation) {
			
			System.out.println("Download started");
			
			DownloadManager.getManager().add(this);
			
			service.set(new DownloadService(url, filename, size));
			
			service.get().setOnSucceeded(e -> DownloadManager.getManager().remove(this));

			service.get().start();
			
		} else {
			System.out.println("Download cancelled");
		}
		

	}
	
	private boolean checkIfAlreadyExists(String filename) {
		
		final Path path = Paths.get(UserConfig.getConfig().getString("downloads")+filename);
		
		if(!Files.exists(path)) {
			return true;
		}
		
		final Alert dialog = new Alert(AlertType.CONFIRMATION);
		
		dialog.getButtonTypes().clear();
		dialog.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		
		dialog.setTitle("File already exists at location "+path.getParent());
		
		dialog.setHeaderText("Do you want to overwrite this file?");
	
		final ButtonType response = dialog.showAndWait().get();
		
		return response == ButtonType.YES;
	}

}
