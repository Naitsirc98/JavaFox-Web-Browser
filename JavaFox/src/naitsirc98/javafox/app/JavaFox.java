/*
 * A very simple Web Browser JavaFX Application
 * 
 * 
 * @author Cristian Daniel Herrera Herrera
 * 
 * */


package naitsirc98.javafox.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import naitsirc98.javafox.app.config.UserConfig;
import naitsirc98.javafox.app.tabs.WebTab;
import naitsirc98.javafox.app.util.Icon;

public class JavaFox extends Application {
	
	public static void execute(String[] args) {
		launch(args);
	}

	
	private static JavaFox javafox;
	
	public static JavaFox getJavaFox() {
		
		if(javafox == null) {
			throw new IllegalStateException("JavaFox has not been initialized!");
		}
		return javafox;
	}
	
	private Stage window;
	private TabPane tabs;

	public JavaFox() {
		
		if(javafox != null) {
			throw new IllegalStateException("JavaFox has been already initialized!");
		}

		javafox = this;
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		UserConfig.getConfig();
		
		window = stage;
		
		stage.setTitle("JavaFox");
		
		BorderPane root = new BorderPane();
		
		tabs = new TabPane();
		
		tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		tabs.getStylesheets().add(getClass().getResource("/styles.css").toString());
		
		root.setCenter(new StackPane(tabs));
		
		Scene scene = new Scene(root);
		
		scene.getAccelerators().put(new KeyCodeCombination(
				KeyCode.T, KeyCombination.CONTROL_DOWN), () -> addTab());
		
		scene.getAccelerators().put(new KeyCodeCombination(
				KeyCode.W, KeyCombination.CONTROL_DOWN), () -> closeSelectedTab());
		
		scene.getAccelerators().put(new KeyCodeCombination(
				KeyCode.PLUS, KeyCombination.CONTROL_DOWN), () -> zoomOnSelectedTab(true));
		
		scene.getAccelerators().put(new KeyCodeCombination(
				KeyCode.MINUS, KeyCombination.CONTROL_DOWN), () -> zoomOnSelectedTab(false));
		
		stage.setScene(scene);
		
		stage.setMaximized(true);
		
		stage.getIcons().add(Icon.JAVAFOX.image());
		
		stage.setMinWidth(720);
		stage.setMinHeight(600);
		
		stage.show();
		
		tabs.getTabs().add(new WebTab());
		
	}
	
	private void zoomOnSelectedTab(boolean zoomIn) {
		
		final WebTab tab = (WebTab) tabs.getSelectionModel().getSelectedItem();
		
		tab.zoom(zoomIn ? 0.1 : -0.1);

	}

	public void addTab() {
		final Tab tab = new WebTab(null);
		tabs.getTabs().add(tab);
		tabs.getSelectionModel().select(tab);
	}
	
	public void closeSelectedTab() {

		final WebTab tab = (WebTab) tabs.getSelectionModel().getSelectedItem();
		
		tabs.getTabs().remove(tab);
		
		tab.getOnClosed().handle(null);
	}
	
	public int countTabs() {
		return tabs.getTabs().size();
	}
	
	
	public void exit() {
		
		if(javafox == null) {
			throw new IllegalStateException("JavaFox has not been initialized!");
		}
		
		// ...
		
		System.exit(0);
		
	}

	public double getWidth() {
		return window.getWidth();
	}
	
	public double getHeight() {
		return window.getHeight();
	}

}
