package naitsirc98.javafox.app.tabs.views;

import java.time.LocalDate;
import java.time.ZoneId;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;

public class HistoryView extends Pane {

	private ListView<Hyperlink> list;
	
	public HistoryView(WebEngine engine) {
		
		list = new ListView<>();
	
		engine.getHistory().getEntries().forEach(entry -> {
			
			LocalDate date = entry.getLastVisitedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			Hyperlink link = new Hyperlink(date + ": " + entry.getTitle());
			
			link.setOnAction(e -> {
				engine.load(entry.getUrl());
			});
			
			list.getItems().add(link);
			
		});
		
		getChildren().add(list);
		
		
	}

}
