/*
 * A very simple Web Browser JavaFX Application
 * 
 * 
 * @author Cristian Daniel Herrera Herrera
 * 
 * */


package naitsirc98.javafox.app;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import naitsirc98.javafox.app.config.UserConfig;
import naitsirc98.javafox.app.gui.Toolbar;
import naitsirc98.javafox.app.gui.tabs.WebTab;
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
	private Pane root;
	private TabPane tabs;

	public JavaFox() {
		
		if(javafox != null) {
			throw new IllegalStateException("JavaFox has been already initialized!");
		}

		javafox = this;
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		window = stage;
		
		stage.setTitle("JavaFox");
		
		root = new StackPane();
		
		tabs = new TabPane();
		
		tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		tabs.getStylesheets().add(getClass().getResource("/styles.css").toString());
		
		root.getChildren().add(new StackPane(tabs));
		
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
		
		stage.setOnCloseRequest(e -> exit());
		
		stage.show();
		
		addTab();
		
	}
	
	private void zoomOnSelectedTab(boolean zoomIn) {
		
		final WebTab tab = (WebTab) tabs.getSelectionModel().getSelectedItem();
		
		tab.zoom(zoomIn);

	}
	
	public void select(Tab tab) {
		tabs.getSelectionModel().select(tab);
		Toolbar.getToolbar().setTab((WebTab) tab);
	}

	public void addTab() {
		final WebTab tab = new WebTab();
		tabs.getTabs().add(tab);
		select(tab);
	}
	
	public void closeTab(Tab tab) {
		
		tabs.getTabs().remove(tab);
		
		tab.getOnClosed().handle(null);
		
	}
	
	public void closeSelectedTab() {
		closeTab(tabs.getSelectionModel().getSelectedItem());
	}
	
	public void closeAllExcept(Tab tab) {
		
		tabs.getTabs().retainAll(tab);
		
		tabs.getSelectionModel().select(tab);
		
	}
	
	public void closeOnRight(Tab tab) {
		
		final int index = tabs.getTabs().indexOf(tab);
		
		final List<Tab> right = tabs.getTabs().subList(index+1, tabs.getTabs().size());
		
		right.forEach(e -> e.getOnClosed().handle(null));
		
		tabs.getTabs().removeAll(right);
		
	}
	
	public void closeOnLeft(Tab tab) {
		
		final int index = tabs.getTabs().indexOf(tab);
		
		final List<Tab> left = tabs.getTabs().subList(0, index);
		
		left.forEach(e -> e.getOnClosed().handle(null));
		
		tabs.getTabs().removeAll(left);
		
	}
	
	public int countTabs() {
		return tabs.getTabs().size();
	}
	

	public void exit() {
		
		if(javafox == null) {
			throw new IllegalStateException("JavaFox has not been initialized!");
		}
		
		UserConfig.getConfig().save();
		
		System.exit(0);
		
	}

	public double getWidth() {
		return window.getWidth();
	}
	
	public double getHeight() {
		return window.getHeight();
	}
	
	public double widthOf(double percentage) {
		return JavaFox.getJavaFox().getWidth() * percentage;
	}

	public double heightOf(double percentage) {
		return JavaFox.getJavaFox().getHeight() * percentage;
	}

}
