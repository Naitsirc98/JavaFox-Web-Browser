package naitsirc98.javafox.app.util;

import java.io.FileNotFoundException;
import java.net.URL;

import javafx.scene.image.Image;

public enum Icon {
	
	JAVAFOX("javafox_logo.png"),
	BACK("back.png"),
	FORWARD("forward.png"),
	ADD("add.png"),
	REFRESH("refresh.png"),
	HOME("home.png"),
	DOWNLOADS("downloads.png"),
	AUDIO("audio_icon.png"),
	MENU("menu.png"),
	BOOKMARKS("star.png");


	private final Image image;
	private final String path;
	
	private Icon(String path) {
		this.path = getPath(path);
		image = new Image(this.path);
	}
	
	public Image image() {
		return image;
	}
	
	public String path() {
		return path;
	}
	
	private static String getPath(String relativePath) {
		
		final URL url = Icon.class.getResource("/icons/"+relativePath);
		
		if(url == null) {
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return url.toString();
		
	}
 
}
