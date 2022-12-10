package org.se;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author Marek Fischer
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ui/gui.fxml")));
		primaryStage.setTitle("SongGenerator");
		primaryStage.getIcons().add(new Image("/ui/icon.png"));
		primaryStage.setScene(new Scene(root, 800, 450));
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
