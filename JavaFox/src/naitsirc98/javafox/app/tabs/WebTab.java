package naitsirc98.javafox.app.tabs;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import naitsirc98.javafox.app.JavaFox;
import naitsirc98.javafox.app.config.UserConfig;
import naitsirc98.javafox.app.util.Icon;

public class WebTab extends Tab {

	public static final String GOOGLE_URL = "https://www.google.com";
	private static final String PDF_VIEWER = WebTab.class
			.getResource("/pdfjs-1.9.426-dist/web/viewer.html").toExternalForm();
	private static final String PDF_VIEW_STYLE = WebTab.class
			.getResource("/pdfjs-1.9.426-dist/web/viewer.css").toExternalForm();


	private BorderPane root;
	private VBox header;
	private Button back, forward, add, refresh, home;
	private TextField currentURL, search;
	private Label zoomLabel;

	private WebView view;
	private WebEngine engine;
	private WebHistory history;
	private Worker<Void> loadWorker;
	private ProgressBar progress;
	private int relativePosition = 0;

	private String pdfLocation;

	public WebTab() {
		this(UserConfig.getConfig().getMainPage());
	}

	public WebTab(String url) {
		super("New Tab");

		createWebView(url);

		createWidgets();

		root = new BorderPane();

		root.setTop(header);

		root.setCenter(view);

		setContent(root);

		setOnClosed(e -> {

			final JavaFox app = JavaFox.getJavaFox();

			if(app.countTabs() == 0) {
				app.exit();
			}

		});

		engine.load(url);

	}


	private void createWidgets() {

		header = new VBox();
		header.setAlignment(Pos.TOP_CENTER);

		HBox widgets = new HBox();
		widgets.setSpacing(2);
		widgets.setStyle("-fx-background-color: darkgrey;");
		widgets.setAlignment(Pos.CENTER_LEFT);

		back = createButton(Icon.BACK, 20,20, e -> {
			relativePosition--;
			history.go(-1);
		});
		back.setDisable(true);


		forward = createButton(Icon.FORWARD, 20, 20, e -> {
			relativePosition++;
			history.go(1);
		});
		forward.setDisable(true);

		refresh = createButton(Icon.REFRESH, 20, 20, e-> engine.reload());

		currentURL = new TextField();
		currentURL.setPrefWidth(widthOf(0.45));
		currentURL.setMinWidth(widthOf(0.1));
		currentURL.setFont(Font.font("Sans Seriff", 14));

		currentURL.setOnAction(e -> {

			String url = currentURL.getText().trim();

			try {

				new URI(url).toURL();

			} catch (Exception ex) {

				url = resolveWithGoogle(url);

			} finally {
				engine.load(url);
			}

		});
		currentURL.setPromptText("Search with Google or enter address");

		HBox.setMargin(currentURL, new Insets(0,widthOf(0.05),0,10));

		search = new TextField();
		search.setPrefWidth(widthOf(0.2));
		search.setMinWidth(widthOf(0.042));
		search.setFont(Font.font("Sans Seriff", 14));
		search.setOnAction(e -> {

			engine.load(resolveWithGoogle(search.getText()));

		});
		search.setPromptText("Search in Google");

		HBox.setMargin(search, new Insets(0,widthOf(0.05),0,0));

		add = createButton(Icon.ADD, 20, 20, e -> JavaFox.getJavaFox().addTab());
		add.setTooltip(new Tooltip("Create new tab"));

		home = createButton(Icon.HOME, 20, 20, e -> engine.load(GOOGLE_URL));
		home.setTooltip(new Tooltip("Go to the main page"));

		
		zoomLabel = new Label("Zoom: "+(int)(view.getZoom()*100)+"%");
		zoomLabel.setStyle("-fx-background-color: lightgray;");
		
		widgets.getChildren().addAll(back, forward, refresh, add, home, currentURL, search, zoomLabel);


		progress = new ProgressBar();

		progress.setStyle("-fx-accent: #487799;");

		progress.setPrefWidth(JavaFox.getJavaFox().getWidth());

		progress.progressProperty().bind(loadWorker.progressProperty());

		header.getChildren().addAll(widgets, progress);

	}

	private void createWebView(String url) {

		view = new WebView();
		
		view.zoomProperty().addListener(e -> {
			zoomLabel.setText("Zoom: "+(int)(view.getZoom()*100)+"%");
		});

		engine = view.getEngine();

		engine.setJavaScriptEnabled(true);

		engine.setUserStyleSheetLocation(PDF_VIEW_STYLE);

		loadWorker = engine.getLoadWorker();

		loadWorker.stateProperty().addListener(e -> {

			System.out.println(loadWorker.getState());

			switch(loadWorker.getState()) {
			case CANCELLED:
				if(engine.getLocation().endsWith(".pdf")) {
					pdfLocation = engine.getLocation();
					engine.load(PDF_VIEWER);
				}
				break;
			case FAILED:
				engine.load(new File("no_connection.html").toURI().toString());
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

		loadWorker.progressProperty().addListener(e -> {

			if(!progress.isVisible()) {
				progress.setVisible(true);
			}

			if(loadWorker.getProgress() == 1.0) {
				progress.setVisible(false);
			}

		});

		engine.documentProperty().addListener(e -> {

			if(engine.getDocument() == null) {
				return;
			}

			final Document doc = engine.getDocument();

			setText(getTitle());
			currentURL.setText(doc.getBaseURI());


		});

		history = engine.getHistory();

		history.currentIndexProperty().addListener(e -> {

			back.setDisable(history.getCurrentIndex() <= 0);
			forward.setDisable(relativePosition >= 0);

		});

	}

	private Button createButton(Icon icon, double w, double h, EventHandler<ActionEvent> onAction) {

		Button button = new Button();
		ImageView view = new ImageView(icon.image());
		view.setFitHeight(w);
		view.setFitWidth(h);

		button.setStyle("-fx-background-color: lightgray;");
		button.setGraphic(view);
		button.setOnAction(onAction);

		return button;
	}

	private void checkPDF() {
		
		if(pdfLocation == null) {
			return;
		}

		System.out.println("Loading pdf...");

		try(final InputStream stream = new URL(pdfLocation).openStream()) {
			
			pdfLocation = null;
	
			final byte[] data = stream.readAllBytes();

			String base64 = Base64.getEncoder().encodeToString(data);

			engine.executeScript("openFileFromBase64('"+base64+"')");

		} catch (Exception ex1) {
			ex1.printStackTrace();
		}




	}

	private String getTitle() {

		Document doc = engine.getDocument();
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

	private String resolveWithGoogle(String text) {

		return GOOGLE_URL+"/search?q=" + text.trim();

	}

	public void zoom(double factor) {
		
		if(factor < 0 && view.getZoom() <= 0.1 || factor > 0 && view.getZoom() >= 3) {
			return;
		}
		
		view.setZoom(view.getZoom()+factor);
		
	}
	
	private double widthOf(double percentage) {
		return JavaFox.getJavaFox().getWidth() * percentage;
	}
	
	private double heightOf(double percentage) {
		return JavaFox.getJavaFox().getHeight() * percentage;
	}

}
