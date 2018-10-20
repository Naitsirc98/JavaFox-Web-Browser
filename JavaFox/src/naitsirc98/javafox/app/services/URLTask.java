package naitsirc98.javafox.app.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.concurrent.Task;

public abstract class URLTask extends Task<String> {

	protected final String url;
	
	protected URLTask(final String url) {
		this.url = url;
	}
	
	protected final HttpURLConnection openConnection() {
		
		HttpURLConnection connection = null;
		
		try {
			
			final URL url = new URL(this.url);
			
			connection = (HttpURLConnection) url.openConnection();
			
			connection.addRequestProperty("User-Agent", 
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			
			return connection;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connection;
		
	}
	
	protected final void sendRequest(HttpURLConnection connection, String params) {
		
		connection.setDoOutput(true);
		
		connection.setRequestProperty("Content-Length", 
				String.valueOf(params.getBytes().length));
		
		try(final DataOutputStream writer 
				= new DataOutputStream(connection.getOutputStream())) {
			
			writer.writeBytes(params);
			
			writer.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	protected final String getResponse(HttpURLConnection connection) {
		
		final StringBuffer buffer = new StringBuffer();
		
		try(final BufferedReader reader 
				= new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			
			String line = null;
			
			while((line = reader.readLine()) != null) {
				buffer.append(line).append(System.lineSeparator());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return buffer.toString();
		
	}
	
	

}
