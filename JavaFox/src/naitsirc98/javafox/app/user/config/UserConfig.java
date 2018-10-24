package naitsirc98.javafox.app.user.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import naitsirc98.javafox.app.user.history.HistoryFile;

public final class UserConfig {

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

	private Map<String, String> defaultAttribValues;
	
	{
		
		defaultAttribValues = new HashMap<>();

		defaultAttribValues.put("mainPage", "https://www.google.com");
		defaultAttribValues.put("historyBufferSize", "512");
		defaultAttribValues.put("historyFileSize", "4096");
		defaultAttribValues.put("downloads", System.getProperty("user.home")+"/Downloads/");
		
	}

	private URI uri;
	private JsonObject json;

	private UserConfig() throws IOException, URISyntaxException {

		uri = getClass().getResource(USER_CONFIG_PATH).toURI();

		json = load();
		
		new HistoryFile();

	}

	private JsonObject load() throws IOException {

		final String json = new String(getClass().getResourceAsStream(USER_CONFIG_PATH)
				.readAllBytes(), StandardCharsets.UTF_8);

		return new Gson().fromJson(json, JsonObject.class);

	}
	
	public void setProperty(String property, Number value) {
		json.addProperty(property, value);
	}
	
	public void setProperty(String property, Boolean value) {
		json.addProperty(property, value);
	}
	
	public void setProperty(String property, String value) {
		json.addProperty(property, value);
	}
	
	
	public boolean getBoolean(String property) {
		return json.get(property).getAsBoolean();
	}
	
	public String getString(String property) {
		return get(property).getAsString();
	}
	
	public int getInt(String property) {
		return get(property).getAsInt();
	}

	private JsonElement get(String attribute) {

		JsonElement element = json.get(attribute);

		if(element == null) {
			element = createDefault(attribute);
		}

		return element;
	}

	private JsonElement createDefault(String attribute) {
		
		json.addProperty(attribute, defaultAttribValues.get(attribute));
		
		return json.get(attribute);
	}

	public void save() {
		
		System.out.println("saving user config...");
		
		try(final BufferedWriter writer = new BufferedWriter(
				new FileWriter(uri.getPath()))) {

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
