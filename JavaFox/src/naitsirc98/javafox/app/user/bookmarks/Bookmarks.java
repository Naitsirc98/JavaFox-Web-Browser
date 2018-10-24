package naitsirc98.javafox.app.user.bookmarks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public final class Bookmarks extends HashMap<String,String> {
	
	private static final long serialVersionUID = 1L;

	private static final String BOOKMARKS_FILE = Bookmarks.class
			.getResource("/config/bookmarks.dat").toExternalForm()
			.replace("file:/", "");
	
	private static Bookmarks bookmarks;
	
	public static Bookmarks getBookmarks() {
		
		if(bookmarks == null) {
			
			synchronized (Bookmarks.class) {
				try {
					bookmarks = getFromFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return bookmarks;
	}
	
	private static Bookmarks getFromFile() throws IOException {
		
		Bookmarks bookmarks = null;
		
		System.out.println(BOOKMARKS_FILE);
		
		final File file = new File(BOOKMARKS_FILE);
		
		if(!file.exists()) {
			file.createNewFile();
			bookmarks = new Bookmarks();
			bookmarks.save();
			
		} else if(file.length() == 0) {
			
			bookmarks = new Bookmarks();
			bookmarks.save();
			
		}else {
			
			try(final ObjectInputStream reader = new ObjectInputStream(new FileInputStream(file))) {
				
				bookmarks = (Bookmarks) reader.readObject();
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		return bookmarks;
	}

	private Bookmarks() {
		
	}
	
	public void save() {
		
		File file = new File(BOOKMARKS_FILE);
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try(final ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(BOOKMARKS_FILE))) {
			
			writer.writeObject(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
