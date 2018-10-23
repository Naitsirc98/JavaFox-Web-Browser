package naitsirc98.javafox.app.web;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import naitsirc98.javafox.app.gui.tabs.WebTab;
import naitsirc98.javafox.app.web.downloads.Download;

public class WebManager {
	
	private static final String PDF_VIEWER = WebManager.class
			.getResource("/pdfjs-1.9.426-dist/web/viewer.html").toExternalForm();
	private static final String PDF_VIEW_STYLE = WebManager.class
			.getResource("/pdfjs-1.9.426-dist/web/viewer.css").toExternalForm();
	private static final String NO_CONNECTION = WebManager.class
			.getResource("/pages/no_connection.html").toExternalForm();

	private final WebTab tab;
	
	private int relativeIndex = 0;
	
	private String lastSearchedURL;
	private String pdfLocation;
	
	private final WebView view;
	private final WebEngine engine;
	private final Worker<Void> loadWorker;
	
	public WebManager(WebTab tab, String url) {
		
		this.tab = tab;
		
		view = new WebView();

		engine = view.getEngine();

		engine.setJavaScriptEnabled(true);

		engine.setUserStyleSheetLocation(PDF_VIEW_STYLE);

		loadWorker = engine.getLoadWorker();

		loadWorker.stateProperty().addListener((observable, oldValue, newValue) -> {
			
			System.out.println(newValue);

			switch(newValue) {
			
			case CANCELLED:
				checkCancelReason();
				break;
			case FAILED:
				engine.load(NO_CONNECTION);
				break;
			case READY:
				break;
			case RUNNING:
				break;
			case SCHEDULED:
				break;
			case SUCCEEDED:
				checkPDF();
				break;
			default:
				break;

			}

		});

		engine.load(url);
		
	}
	
	public void close() {
		engine.load(null);
	}
	
	private void checkCancelReason() {
		
		if(engine.getLocation().endsWith(".pdf")) {
			pdfLocation = engine.getLocation();
			engine.load(PDF_VIEWER);
			
		} else {
			
			Download.newDownload(engine.getLocation());
			
		}
		
	}
	

	private void checkPDF() {
		
		if(pdfLocation == null) {
			tab.setText(getTitle());
			return;
		}

		System.out.println("Loading pdf...");

		try(final InputStream stream = new URL(pdfLocation).openStream()) {
	
			final byte[] data = stream.readAllBytes();

			String base64 = Base64.getEncoder().encodeToString(data);

			engine.executeScript("openFileFromBase64('"+base64+"')");
			
			tab.setText(pdfLocation.substring(pdfLocation.lastIndexOf('/')+1));
			
			pdfLocation = null;

		} catch (Exception ex1) {
			ex1.printStackTrace();
		}

		
	}
	
	public String getTitle() {

		Document doc = engine.getDocument();
		
		if(doc == null) {
			return engine.getLocation();
		}
		
		NodeList heads = doc.getElementsByTagName("head");

		if (heads.getLength() > 0) {
			Element head = (Element)heads.item(0);
			NodeList titles = head.getElementsByTagName("title");

			if (titles.getLength() > 0) {
				Node title = titles.item(0);
				return title.getTextContent();
			}
		}

		return engine.getLocation();
	}
	

	public void zoom(double factor) {
		
		if(factor < 0 && view.getZoom() <= 0.1 || factor > 0 && view.getZoom() >= 3) {
			return;
		}
		
		view.setZoom(view.getZoom()+factor);
		
	}

	public WebHistory getHistory() {
		return engine.getHistory();
	}
	
	public int getRelativeIndex() {
		return relativeIndex;
	}
	
	public void decRelativeIndex() {
		relativeIndex--;
	}
	
	public void incRelativeIndex() {
		relativeIndex++;
	}
	
	public WebView getWebView() {
		return view;
	}
	
	public WebEngine getEngine() {
		return engine;
	}
	
	public void setLastSearchedURL(String url) {
		lastSearchedURL = url;
	}
	
	public String getLastSearchedURL() {
		return lastSearchedURL;
	}
	
	public WebTab getTab() {
		return tab;
	}

}
