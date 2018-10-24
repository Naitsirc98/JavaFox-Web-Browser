package naitsirc98.javafox.app.gui;

import java.net.URI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import naitsirc98.javafox.app.JavaFox;
import naitsirc98.javafox.app.gui.dialogs.BookmarkDialog;
import naitsirc98.javafox.app.gui.tabs.WebTab;
import naitsirc98.javafox.app.user.bookmarks.Bookmarks;
import naitsirc98.javafox.app.user.config.UserConfig;
import naitsirc98.javafox.app.util.Icon;
import naitsirc98.javafox.app.util.IconUtils;
import naitsirc98.javafox.app.web.WebManager;
import naitsirc98.javafox.app.web.downloads.DownloadManager;

public class WebToolBar extends VBox {

	private static WebToolBar toolbar;

	public static WebToolBar getToolBar() {

		if(toolbar == null) {
			toolbar = new WebToolBar();
		}

		return toolbar;
	}

	private Button back, forward, add, refresh, home;
	private TextField currentURL, search;
	private Label zoomLabel;
	private Button downloads;
	private Menu menu;
	private Button addBookmark;
	private ProgressBar progress;

	private WebTab selectedTab;

	private WebToolBar() {
		setCache(true);
		setAlignment(Pos.TOP_CENTER);
		createWidgets();
	}

	public void setTab(WebTab tab) {

		if(selectedTab == tab) {
			return;
		}

		checkHistoryButtons(tab.getManager());

		search.setText(tab.getManager().getLastSearchedURL());

		currentURL.setText(tab.getManager().getEngine().getLocation());
		
		zoomLabel.setText("Zoom: "+((int)(tab.getManager().getWebView().getZoom()*100))+"%");
		
		manageProperties(tab.getManager());

		selectedTab = tab;

	}

	public void setZoomValue(double zoom) {
		zoomLabel.setText("Zoom: "+((int)(zoom*100))+"%");
	}
	
	public void enableDownloadsButton() {
		downloads.setDisable(false);
	}


	private void init() {

		back.setOnAction(e -> {
			selectedTab.getManager().decRelativeIndex();
			selectedTab.getManager().getHistory().go(-1);
		});

		forward.setOnAction(e -> {
			selectedTab.getManager().incRelativeIndex();
			selectedTab.getManager().getHistory().go(1);
		});

		refresh.setOnAction(e-> selectedTab.getManager().getEngine().reload());

		currentURL.setOnAction(e -> {

			String url = currentURL.getText().trim();

			try {
				new URI(url).toURL();
			} catch (Exception ex) {
				url = tryResolve(url);
			} finally {
				selectedTab.getManager().getEngine().load(url);
			}

		});

		search.setOnAction(e -> {

			selectedTab.getManager().setLastSearchedURL(search.getText());
			selectedTab.getManager().getEngine().load(tryResolve(search.getText()));

		});

		add.setOnAction(e -> JavaFox.getJavaFox().addTab());

		home.setOnAction(e -> selectedTab.getManager().getEngine().load(UserConfig
				.getConfig().getString("mainPage")));

	}

	private void manageProperties(WebManager manager) {

		progress.progressProperty().unbind();

		progress.setProgress(manager.getEngine().getLoadWorker().getProgress());

		progress.progressProperty().bind(manager.getEngine().getLoadWorker().progressProperty());

		manager.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {


			if(newValue.startsWith("file:")) {
				return;
			}

			manager.getTab().setText(manager.getTitle());
			currentURL.setText(newValue);

		});

		manager.getHistory().currentIndexProperty().addListener((observable, oldValue, newValue) -> {

			checkHistoryButtons(manager);

		});

		manager.getWebView().zoomProperty().addListener((observable, oldValue, newValue) -> {
			zoomLabel.setText("Zoom: "+(int)(newValue.doubleValue()*100)+"%");
		});

	}

	private void checkHistoryButtons(WebManager manager) {
		back.setDisable(manager.getHistory().getCurrentIndex() <= 0);
		forward.setDisable(manager.getRelativeIndex() >= 0);
	}

	private void createWidgets() {

		JavaFox javafox = JavaFox.getJavaFox();

		HBox widgets = new HBox();
		widgets.setSpacing(2);
		widgets.setStyle("-fx-background-color: darkgrey;");
		widgets.setAlignment(Pos.CENTER_LEFT);

		back = createButton(Icon.BACK, 20,20);
		back.setDisable(true);

		forward = createButton(Icon.FORWARD, 20, 20);
		forward.setDisable(true);

		refresh = createButton(Icon.REFRESH, 20, 20);

		currentURL = new TextField();
		currentURL.setPrefWidth(javafox.widthOf(0.45));
		currentURL.setMinWidth(javafox.widthOf(0.1));
		currentURL.setFont(Font.font("Sans Seriff", 14));

		currentURL.setPromptText("Search with Google or enter address");
		
		addBookmark = createButton(Icon.BOOKMARKS, 20, 20);
		
		addBookmark.setOnAction(e -> showAddBookmarkDialog());

		HBox.setMargin(addBookmark, new Insets(0,javafox.widthOf(0.03),0,10));

		search = new TextField();
		search.setPrefWidth(javafox.widthOf(0.2));
		search.setMinWidth(javafox.widthOf(0.042));
		search.setFont(Font.font("Sans Seriff", 14));

		search.setPromptText("Search in Google");

		HBox.setMargin(search, new Insets(0,javafox.widthOf(0.02),0,0));

		add = createButton(Icon.ADD, 20, 20);
		add.setTooltip(new Tooltip("Create new tab"));

		home = createButton(Icon.HOME, 20, 20);
		home.setTooltip(new Tooltip("Go to the main page"));

		zoomLabel = new Label("Zoom: 100%");
		zoomLabel.setStyle("-fx-background-color: lightgray;");
		
		HBox.setMargin(zoomLabel, new Insets(0,javafox.widthOf(0.02),0,0));

		progress = new ProgressBar();

		progress.setVisible(false);

		progress.setStyle("-fx-accent: #487799;");

		progress.setPrefWidth(JavaFox.getJavaFox().getWidth());

		progress.setPrefHeight(10);

		progress.progressProperty().addListener(e -> {

			progress.setVisible(progress.getProgress() < 1.0);

		});

		downloads = createButton(Icon.DOWNLOADS, 20, 20);
		
		downloads.setOnAction(e -> {
			
			DownloadManager.getManager().display(
					downloads.getLayoutX(),downloads.getLayoutY());
			
		});
		
		downloads.setDisable(true);
		
		menu = new Menu();
		
		IconUtils.setGraphic(menu, Icon.MENU);
		
		Menu bookmarks = new Menu("Bookmarks");
		
		updateBookmarksMenu(bookmarks);
		
		bookmarks.setOnShowing(e -> updateBookmarksMenu(bookmarks));
		
		menu.getItems().add(bookmarks);
		
		MenuBar bar = new MenuBar(menu);
		
		bar.setStyle("-fx-background-color: transparent;");
		
		widgets.getChildren().addAll(back, forward, refresh, add, home, currentURL, addBookmark,
				search, zoomLabel, downloads, bar);

		getChildren().addAll(widgets, progress);
		
		init();

	}
	
	private void showAddBookmarkDialog() {
		
		final BookmarkDialog dialog = new BookmarkDialog(selectedTab.getText(), currentURL.getText());

		dialog.showAndWait().ifPresent(args -> Bookmarks.getBookmarks().put(args.getKey(), args.getValue()));

	}

	private void updateBookmarksMenu(Menu bookmarks) {
		
		Bookmarks bm = Bookmarks.getBookmarks();
		
		bookmarks.getItems().clear();
		
		bm.forEach((name, url) -> {
			
			MenuItem item = new MenuItem(name+": "+url);
			
			item.setOnAction(event -> selectedTab.getManager().getEngine().load(url));
			
			bookmarks.getItems().add(item);
			
		});
		
	}

	private Button createButton(Icon icon, double w, double h) {

		Button button = new Button();
		ImageView view = new ImageView(icon.image());
		view.setFitHeight(w);
		view.setFitWidth(h);

		button.setStyle("-fx-background-color: lightgray;");
		button.setGraphic(view);

		return button;
	}

	private String tryResolve(String text) {
		return UserConfig.getConfig().getString("mainPage")+"/search?q=" + text.trim();
	}


}
