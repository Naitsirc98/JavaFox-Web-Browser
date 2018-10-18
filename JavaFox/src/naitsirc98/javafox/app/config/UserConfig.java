package naitsirc98.javafox.app.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class UserConfig {
	
	private static final String USER_CONFIG_PATH = "/config/userconfig.json";
	
	private static UserConfig config;
	
	public static UserConfig getConfig() {
		
		if(config == null) {
			try {
				config = new UserConfig();
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		return config;
		
	}
	
	private URI uri;
	private JsonObject json;
	private String mainPage;

	private UserConfig() throws IOException, URISyntaxException {
		
		uri = getClass().getResource(USER_CONFIG_PATH).toURI();
		
		json = load();
		
		mainPage = json.get("mainPage").getAsString();
		
	}
	
	private JsonObject load() throws IOException {
		
		final String json = new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8);
		
		return new Gson().fromJson(json, JsonObject.class);
		
	}
	
	public String getMainPage() {
		return mainPage;
	}
	
	public void setMainPage(String page) {
		this.mainPage = page;
		setValue("mainPage", page);
	}
	
	private void setValue(String key, String value) {
		json.addProperty(key, value);
		save();
	}
	
	private void save() {
		
		try(final BufferedWriter writer = new BufferedWriter(
				new FileWriter(Paths.get(uri).toFile()))) {
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			writer.write(gson.toJson(json));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/*private JsonObject create() throws IOException {
		
		File file = new File("/config/userconfig.conf");
		
		file.getParentFile().mkdirs();
		file.createNewFile();
		
		StringBuilder builder = new StringBuilder("{");
		builder.append(System.lineSeparator()).append("    ");
		builder.append("\"mainPage\"").append(": ").append("\"https://www.google.com\"");
		builder.append(System.lineSeparator()).append("}");
		
		try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

			writer.write(builder.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(builder);
		
		return gson.fromJson(builder.toString(), JsonObject.class);
		
	}*/

}
