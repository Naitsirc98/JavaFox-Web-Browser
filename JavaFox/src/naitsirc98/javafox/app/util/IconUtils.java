package naitsirc98.javafox.app.util;

import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public class IconUtils {

	private IconUtils() {

	}

	public static void setGraphic(Labeled node, Icon icon) {
		
		node.setStyle("");

		final ImageView view = new ImageView(icon.image());

		view.setFitWidth(20);
		view.setFitHeight(20);

		node.setGraphic(view);

	}
	
	public static void setGraphic(MenuItem node, Icon icon) {
		
		node.setStyle("");

		final ImageView view = new ImageView(icon.image());

		view.setFitWidth(20);
		view.setFitHeight(20);

		node.setGraphic(view);

	}
	
	public static Icon getIconFor(final String fileType) {
		
		// TODO
		
		return Icon.AUDIO;
	}

}
