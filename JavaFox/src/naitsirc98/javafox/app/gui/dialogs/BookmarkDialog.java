package naitsirc98.javafox.app.gui.dialogs;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import naitsirc98.javafox.app.user.bookmarks.Bookmarks;
import naitsirc98.javafox.app.util.Icon;

public final class BookmarkDialog extends Dialog<Pair<String, String>> {

	private static final String URL_VALID_PATTERN 
	= "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	private TextField name, url;
	private Label nameErrorLabel, urlErrorLabel;
	private Node okButton;

	public BookmarkDialog(String defaultName, String defaultURL) {

		setTitle("Add to bookmarks");
		setHeaderText("Add a page to your bookmarks");
		final Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(Icon.BOOKMARKS.image());

		ButtonType ok = new ButtonType("OK", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPrefWidth(300);

		name = new TextField();
		name.setPromptText("Page name");

		url = new TextField();
		url.setPromptText("Page URL");

		nameErrorLabel = new Label();
		nameErrorLabel.setTextFill(Color.RED);

		urlErrorLabel = new Label();
		urlErrorLabel.setTextFill(Color.RED);

		grid.add(new Label("Name:"), 0, 0);
		grid.add(name, 1, 0);
		grid.add(nameErrorLabel, 1, 1, 2, 1);
		grid.add(new Label("URL:"), 0, 2);
		grid.add(url, 1, 2);
		grid.add(urlErrorLabel, 1, 3, 2, 1);

		okButton = getDialogPane().lookupButton(ok);

		name.textProperty().addListener((observable, oldText, newText) -> {

			if(Bookmarks.getBookmarks().containsKey(newText.trim())) {
				nameErrorLabel.setText("This name is already in use!");
			} else if(newText.trim().isEmpty()) {
				nameErrorLabel.setText("Name cannot be empty!");
			} else {
				nameErrorLabel.setText("");
			}

			checkOKButton();

		});

		url.textProperty().addListener((observable, oldText, newText) -> {

			if(newText.trim().isEmpty()) {
				urlErrorLabel.setText("URL cannot be empty!");

			} else if(!newText.trim().matches(URL_VALID_PATTERN)) {

				urlErrorLabel.setText("URL is invalid");

			} else {

				urlErrorLabel.setText("");

			}

			checkOKButton();

		});

		name.setText(defaultName);
		url.setText(defaultURL);

		getDialogPane().setContent(grid);

		setResultConverter(dialogButton -> {
			if (dialogButton == ok) {
				return new Pair<>(name.getText(), url.getText());
			}
			return null;
		});

		Platform.runLater(() -> name.requestFocus());

	}

	private void checkOKButton() {

		final boolean repeated = Bookmarks.getBookmarks().containsKey(name.getText().trim());
		final boolean nameEmpty = name.getText().trim().isEmpty();
		final boolean urlEmpty = url.getText().trim().isEmpty();
		final boolean urlInvalid = !url.getText().trim().matches(URL_VALID_PATTERN);

		okButton.setDisable(repeated || nameEmpty || urlEmpty || urlInvalid);

	}

}
