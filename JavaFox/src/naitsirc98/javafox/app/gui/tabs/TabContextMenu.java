package naitsirc98.javafox.app.gui.tabs;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import naitsirc98.javafox.app.JavaFox;

public class TabContextMenu extends ContextMenu {
	
	public TabContextMenu(Tab owner) {
		
		MenuItem select = new MenuItem("Select");
		select.setOnAction(e -> JavaFox.getJavaFox().select(owner));
		
		MenuItem close = new MenuItem("Close this tab");
		close.setOnAction(e -> JavaFox.getJavaFox().closeTab(owner));
		
		MenuItem closeOthers = new MenuItem("Close other tabs");
		closeOthers.setOnAction(e -> JavaFox.getJavaFox().closeAllExcept(owner));
		
		MenuItem closeOnRight = new MenuItem("Close tabs on the right");
		closeOnRight.setOnAction(e -> JavaFox.getJavaFox().closeOnRight(owner));
		
		MenuItem closeOnLeft = new MenuItem("Close tabs on the left");
		closeOnLeft.setOnAction(e -> JavaFox.getJavaFox().closeOnLeft(owner));
		
		getItems().addAll(select, close, closeOthers, closeOnRight, closeOnLeft);
		
		
	}

}
