package naitsirc98.javafox.app.config.history;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import naitsirc98.javafox.app.config.UserConfig;

public class History {

	private static History history;

	public static History getHistory() {

		if(history == null) {
			history = new History();
		}

		return history;

	}

	private final Map<LocalDate, String> buffer;
	private final UserConfig config;
	private int index = 0;

	private History() {
		config = UserConfig.getConfig();
		buffer = new HashMap<>(config.getInt("historyBufferSize"));
	}

	public void log(String url) {
		buffer.put(LocalDate.now(), url);
		index++;
		checkIndex();
	}

	public String get(LocalDate date) {
		return buffer.get(date);
	}

	private void checkIndex() {

		if(index == config.getInt("historyBufferSize")) {

		}

	}
	
	private void dump() {
		
		/*<ul style="list-style-type:none">
  		<li>Coffee</li>
  		<li>Tea</li>
  		<li>Milk</li>
		</ul>*/
		
		
		
	}



}
