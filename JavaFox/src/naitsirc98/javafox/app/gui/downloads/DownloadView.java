package naitsirc98.javafox.app.gui.downloads;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import naitsirc98.javafox.app.util.Icon;
import naitsirc98.javafox.app.util.IconUtils;
import naitsirc98.javafox.app.web.downloads.Download;

public class DownloadView extends GridPane {

	private static final long serialVersionUID = 4078019138930227797L;

	private GridPane info;
	private final Button options;
	
	public DownloadView(Download download) {
		
		Label name = new Label("File: "+download.getFilename());
		
		if(name.getText().length() > 40) {
			
			name.setText(name.getText().substring(0, 40)+"...");
			
		}
		
		Label progress = new Label();
		
		progress.setTextFill(Color.BLUE);
		
		progress.textProperty().bind(download.getDownloadService().messageProperty());
		
		ImageView icon = new ImageView(IconUtils.getIconFor(download.getType()).image());
		
		icon.setFitWidth(32*0.77);
		
		icon.setFitHeight(32);
		
		options = new Button("Cancel");
		
		options.setOnAction(e -> download.cancel());
		
		add(name, 1, 0);
		
		add(icon, 0, 1);
		
		add(progress, 1, 2);
		
		add(options, 2, 1);
		
	}

}
