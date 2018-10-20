package naitsirc98.javafox.app.services;

import java.net.HttpURLConnection;
import java.net.URL;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class HTTPHeadRequestService extends Service<String[]> {
	
	private String url;
	private String[] fields;
	
	public HTTPHeadRequestService() {
		
	}
	
	public HTTPHeadRequestService(String url, String... fields) {
		this.url = url;
		this.fields = fields;
	}

	public void start(String url, String... fields) {
		this.url = url;
		this.fields = fields;
		start();
	}
	
	@Override
	public void start() {
		
		if(url == null || fields == null) {
			throw new IllegalStateException();
		}
		
		super.start();
		
	}
	
	@Override
	protected Task<String[]> createTask() {
		return new HTTPHeadRequestTask();
	}
	
	private class HTTPHeadRequestTask extends Task<String[]> {

		@Override
		protected String[] call() throws Exception {
			
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			
			connection.addRequestProperty("User-Agent", 
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			
			connection.setRequestMethod("HEAD");
			
			String[] result = null;
			
			if(fields[0].equals("all")) {
				
				result = (String[]) connection.getHeaderFields().values().toArray();
				
			} else {
				
				result = new String[fields.length];
				
				for(int i = 0;i < result.length;i++) {
					
					result[i] = connection.getHeaderField(fields[i]);
					
				}
				
			}
			
			updateProgress(0, 0);
			updateValue(result);
			
			connection.disconnect();
			
			return result;
		}
		
		
	}

	

}
