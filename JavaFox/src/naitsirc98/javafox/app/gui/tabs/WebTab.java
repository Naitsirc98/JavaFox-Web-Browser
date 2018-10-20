package naitsirc98.javafox.app.gui.tabs;

import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import naitsirc98.javafox.app.JavaFox;
import naitsirc98.javafox.app.config.UserConfig;
import naitsirc98.javafox.app.gui.Toolbar;
import naitsirc98.javafox.app.web.WebManager;

public class WebTab extends Tab {

	private BorderPane root;
	private Tooltip tooltip;
	
	private WebManager manager;

	
	public WebTab() {
		this(UserConfig.getConfig().getString("mainPage"));
	}

	public WebTab(String url) {
		super("New Tab");
		
		manager = new WebManager(this, url);
		
		setContextMenu(new TabContextMenu(this));
		
		tooltip = new Tooltip();
		
		setTooltip(tooltip);
		
		tooltip.textProperty().bind(textProperty());

		root = new BorderPane();
		
		root.setCenter(manager.getWebView());
		
		root.setTop(Toolbar.getToolbar());

		setContent(root);

		setOnClosed(e -> {
			
			manager.close();
			
			Toolbar.getToolbar().removeTab(this);

			final JavaFox app = JavaFox.getJavaFox();

			if(app.countTabs() == 0) {
				app.exit();
			}

		});
		
		setOnSelectionChanged(e -> {
			if(isSelected()) {
				Toolbar.getToolbar().setTab(this);
				root.setTop(Toolbar.getToolbar());
			}
		});

		
	}
	
	public WebManager getManager() {
		return manager;
	}

	public void zoom(final boolean zoomIn) {
		manager.zoom(zoomIn ? 0.1 : -0.1);
	}

	
}
