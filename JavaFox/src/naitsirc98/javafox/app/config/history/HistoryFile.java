package naitsirc98.javafox.app.config.history;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HistoryFile {
	
	private Map<LocalDate, String> data;

	public HistoryFile() {
		data = new HashMap<>();
		load();
	}
	
	private void load() {
		
		String html = null;
		
		try(final InputStream stream = getClass().getResourceAsStream("/config/history.html")) {
			
			html = new String(stream.readAllBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parse(html);
		
	}
	
	private void parse(String html) {
		
		final StringBuilder builder = new StringBuilder(
				html.substring(html.indexOf("<body>")+7, html.lastIndexOf("</body>")).trim());
		
		// System.out.println(builder.toString());
		
		
	}

}
