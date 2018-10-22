package naitsirc98.javafox.app.util;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class IconUtils {

	private IconUtils() {

	}

	public static void setGraphic(Button button, Icon icon) {

		final ImageView view = new ImageView(icon.image());

		view.setFitWidth(20);
		view.setFitHeight(20);

		button.setGraphic(view);

	}
	
	public static Icon getIconFor(final String fileType) {
		
		// TODO
		
		return Icon.AUDIO;
	}

}
