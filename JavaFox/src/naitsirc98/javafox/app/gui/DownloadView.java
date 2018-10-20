package naitsirc98.javafox.app.gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import naitsirc98.javafox.app.web.Download;

public class DownloadView extends MenuItem {

	private static final long serialVersionUID = 4078019138930227797L;

	private final Button info;
	private final Button options;
	
	private final Download download;
	
	public DownloadView(Download download) {
		
		this.download = download;
		
		info = new Button();
		options = new Button();
		
		customizeButtons();
		
	}

	private void customizeButtons() {
		
		HBox graphic = new HBox();
		
		GridPane infoGraphic = new GridPane();
		
		Label name = new Label();
		
		download.filenameProperty().addListener((observable, old, neww) -> name.setText(neww));
		
		Label progress = new Label("0 MB");
		
		// info.textProperty().bind(download.getDownloadService().messageProperty());
		
		graphic.getChildren().addAll(name, progress);
		
		setGraphic(graphic);
	}

}
