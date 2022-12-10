package org.se.gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.se.Settings;
import org.se.SongGenerator;
import org.se.music.model.Genre;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller-Klasse für die UI.
 *
 * @author Marek Fischer
 */
public class Controller implements Initializable {

	// fileChooser öffnet den Datei-Explorer
	FileChooser fileChooser = new FileChooser();

	// fileSaver öffnet den Datei-Explorer (andere Einstellungen als fileChooser)
	FileChooser fileSaver = new FileChooser();
	SongGenerator songGenerator;


	// userName gibt den Namen des aktuellen Benutzers -> Pfad zum Laden / Speichern (nur für Windows-User)
	// String userName = System.getProperty("user.name");

	// file enthält die zu untersuchende txt-/pdf-Datei
	File file = null;

	// song enthält den generierten Song (.midi-Datei)
	File song = null;

	// bpm gibt die im Song zu verwendenden Beats per Minute an
	int bpm = 100;

	// genres beinhaltet die Auswahl an verfügbaren Musik-Genres
	String[] genres = { "Blues", "Pop" };

	// genre gibt das aktuell ausgewählte Genre an
	String genre = "POP";

	// progress zeigt den Fortschritt der Songgenerierung an
	double progress = 0;

	@FXML
	private Button setting_pane_back;

	@FXML
	private Label setting_pane_genre;

	@FXML
	private Label load_pane_drag;

	@FXML
	private Button load_pane_load;

	@FXML
	private Label load_pane_path;

	@FXML
	private Label load_pane_label;

	@FXML
	private Label setting_pane_bpm;

	@FXML
	private Button load_pane_settings;

	@FXML
	private Slider setting_pane_slider;

	@FXML
	private ChoiceBox<String> setting_pane_cb;

	@FXML
	private ProgressBar generate_pane_progress;

	@FXML
	private Label generate_pane_progressLbl;

	@FXML
	private Button song_generate;

	@FXML
	private Button song_save;

	/**
	 * Öffnet den Datei-Explorer und lädt die ausgewählte Datei.
	 */
	@FXML
	void loadClicked() {
		file = fileChooser.showOpenDialog(new Stage());
		if (file != null) load_pane_path.setText(file.getPath());

		// generating song now enabled
		song_generate.setDisable(false);
	}

	// TODO: diese Funktion nutzen, um den Fortschrittsbalken zu verändern
	/**
	 * Erhöht den Fortschrittsbalken und zeigt die fertigen Funktionen an.
	 *
	 * @param msg
	 *            die abgeschloßene Funktion
	 * @param val
	 *            Wert, um den der Fortschritt erhöht wird
	 */
	void increaseProgress(String msg, double val) {
		generate_pane_progressLbl.setText("Progress: " + msg + " done...");
		progress += val;
		generate_pane_progress.setProgress(progress);
	}

	/**
	 * Setzt alle Load-Elemente auf "Unsichtbar" und alle Setting-Elemente auf "Sichtbar" und umgekehrt.
	 */
	@FXML
	void toggleClicked() {
		boolean showLoad = load_pane_path.isVisible();

		load_pane_path.setVisible(!showLoad);
		load_pane_load.setVisible(!showLoad);
		load_pane_label.setVisible(!showLoad);
		load_pane_settings.setVisible(!showLoad);
		load_pane_drag.setVisible(!showLoad);

		setting_pane_bpm.setVisible(showLoad);
		setting_pane_slider.setVisible(showLoad);
		setting_pane_back.setVisible(showLoad);
		setting_pane_genre.setVisible(showLoad);
		setting_pane_cb.setVisible(showLoad);
	}

	/**
	 * Gibt an, ob ein File eine .pdf- oder .txt-Datei ist.
	 *
	 * @param fileName
	 *            die zu überprüfende Datei
	 * @return ist .pdf- oder .txt-Datei
	 */
	boolean isValidFile(String fileName) {
		String fileType = "";
		int i = fileName.lastIndexOf('.');
		if (i >= 0) fileType = fileName.substring(i + 1);
		return fileType.equals("txt") || fileType.equals("pdf");
	}

	/**
	 * Reagiert onDrag.
	 * Lässt valide Dateien in den Drag&Drop-Bereich ziehen.
	 */
	@FXML
	void dragFile(DragEvent event) {
		Dragboard dB = event.getDragboard();
		if (isValidFile(dB.getUrl())) event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	}

	/**
	 * Reagiert onDrop.
	 * Schreibt die Datei in die dafür vorgesehene Variable und zeigt den Dateipfad an.
	 */
	@FXML
	void dropFile(DragEvent event) {
		Dragboard dB = event.getDragboard();
		file = dB.getFiles().get(0);
		load_pane_path.setText(file.getPath());

		// generating song now enabled
		song_generate.setDisable(false);
	}

	/**
	 * Setzt das Genre aus der ChoiceBox (DropDownMenu).
	 */
	public void setGenre() {
		String newGenre = setting_pane_cb.getValue();
		setting_pane_genre.setText("Genre: " + newGenre);
		genre = newGenre.toUpperCase();
	}

	/**
	 * Startet die Songgenerierung.
	 */
	@FXML
	void generateSong() {
		progress = 0;
		generate_pane_progress.setProgress(0);
		if (file != null) {
			Settings settings = new Settings(file.getAbsolutePath(), Genre.POP, bpm);
			songGenerator = new SongGenerator(settings);
			generate_pane_progress.progressProperty().bind(songGenerator.progressProperty());
			songGenerator.messageProperty().addListener((observable, oldVal, newVal) -> generate_pane_progressLbl.setText(newVal));

			Thread th = new Thread(songGenerator);
			th.setDaemon(true);
			th.start();

			song_save.setDisable(false);
		}
	}

	/**
	 * Öffnet den Datei-Explorer und speichert den Song in dem ausgewählten Ordner.
	 */
	@FXML
	void saveFile() {
		if (songGenerator.getSeq() != null) {
			File saveSong = fileSaver.showSaveDialog(new Stage());
			if (saveSong != null) {
				try {
					songGenerator.getSeq().createFile(String.valueOf(saveSong.toPath()));
					((Stage)setting_pane_cb.getScene().getWindow()).close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Öffnet das Benutzerhandbuch.
	 */
	@FXML
	void showGuide() {
		try {
			// TODO: Bugfix the file-path
			Desktop.getDesktop().open(new File("/guide.pdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setzt verschiedene Einstellungen beim starten der UI.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// hide setting elements on launch
		setting_pane_bpm.setVisible(false);
		setting_pane_slider.setVisible(false);
		setting_pane_back.setVisible(false);
		setting_pane_genre.setVisible(false);
		setting_pane_cb.setVisible(false);

		// can not generate or save song unless a file is loaded
		song_generate.setDisable(true);
		song_save.setDisable(true);

		// allow only pdf/txt-filter
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".txt or .pdf", "*.txt", "PDF", "*.pdf"));

		fileChooser.setTitle("Select .pdf or .txt file");

		// start searching in "desktop"-folder
		// fileChooser.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));

		// allow only mdi-filter
		fileSaver.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MIDI", "*.mid"));

		fileSaver.setTitle("Select directory and name file");

		// proposes a name for a file
		fileSaver.setInitialFileName("song");

		// start saving in "desktop"-folder
		// fileSaver.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));

		// handle slider changes
		setting_pane_slider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
			setting_pane_bpm.setText("bpm: " + newVal.intValue());
			bpm = newVal.intValue();
		});

		// set choice options (genres) for choiceBox
		setting_pane_cb.getItems().addAll(genres);
		setting_pane_cb.setOnAction(actionEvent -> setGenre());
	}
}
