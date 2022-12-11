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
 * Controller-class for UI
 *
 * @author Marek Fischer
 */
public class Controller implements Initializable {

	final FileChooser fileChooser = new FileChooser();

	final FileChooser fileSaver = new FileChooser();
	SongGenerator songGenerator;

	File file = null;

	int bpm = 100;

	final String[] genres = Genre.names();

	Genre genre = Genre.POP;

	double progress = 0;
	@FXML
	private Button settingPaneBack;
	@FXML
	private CheckBox settingPaneMetrics;
	@FXML
	private Label settingPaneGenre;
	@FXML
	private Label loadPaneDrag;
	@FXML
	private Button loadPaneLoad;
	@FXML
	private Label loadPanePath;
	@FXML
	private Label loadPaneLabel;
	@FXML
	private Label settingPaneBpm;
	@FXML
	private Button loadPaneSettings;
	@FXML
	private Slider settingPaneSlider;
	@FXML
	private ChoiceBox<String> settingPaneCb;
	@FXML
	private ProgressBar generatePaneProgress;
	@FXML
	private Label generatePaneProgressLbl;
	@FXML
	private Button songGenerate;
	@FXML
	private Button songSave;

	/**
	 * opens native file explorer and stores selected file in file
	 */
	@FXML
	void loadClicked() {
		file = fileChooser.showOpenDialog(new Stage());
		if (file != null) loadPanePath.setText(file.getPath());

		// generating song now enabled
		songGenerate.setDisable(false);
	}

	/**
	 * toggles the visibility of all elements (load and generating have different initial values)
	 */
	@FXML
	void toggleClicked() {
		boolean showLoad = loadPanePath.isVisible();

		loadPanePath.setVisible(!showLoad);
		loadPaneLoad.setVisible(!showLoad);
		loadPaneLabel.setVisible(!showLoad);
		loadPaneSettings.setVisible(!showLoad);
		loadPaneDrag.setVisible(!showLoad);

		settingPaneBpm.setVisible(showLoad);
		settingPaneSlider.setVisible(showLoad);
		settingPaneMetrics.setVisible(showLoad);
		settingPaneBack.setVisible(showLoad);
		settingPaneGenre.setVisible(showLoad);
		settingPaneCb.setVisible(showLoad);
	}

	/**
	 * checks if the ending of the given filepath is valid (pdf or txt)
	 *
	 * @param fileName
	 *            - name of the file to be checked
	 * @return if file name is valid
	 */
	boolean isValidFile(String fileName) {
		String fileType = "";
		int i = fileName.lastIndexOf('.');
		if (i >= 0) fileType = fileName.substring(i + 1);
		return fileType.equals("txt") || fileType.equals("pdf");
	}

	@FXML
	void toggleUseMetrics() {
		if (settingPaneMetrics.isSelected()) {
			settingPaneSlider.setDisable(true);
			settingPaneBpm.setText("BPM");
		} else {
			settingPaneSlider.setDisable(false);
			settingPaneBpm.setText("BPM: " + bpm);
		}
	}

	/**
	 * allows valid files to be dragged and dropped
	 */
	@FXML
	void dragFile(DragEvent event) {
		Dragboard dB = event.getDragboard();
		if (isValidFile(dB.getUrl())) event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	}

	/**
	 * handles onDrop event
	 */
	@FXML
	void dropFile(DragEvent event) {
		Dragboard dB = event.getDragboard();
		file = dB.getFiles().get(0);
		loadPanePath.setText(file.getPath());

		// generating song now enabled
		songGenerate.setDisable(false);
	}

	/**
	 * event handler for genre drop-down selector
	 */
	public void setGenre() {
		genre = Genre.valueOf(settingPaneCb.getValue());
	}

	/**
	 * generates song
	 */
	@FXML
	void generateSong() {
		progress = 0;
		generatePaneProgress.setProgress(0);

		// show progressbar
		generatePaneProgress.setVisible(true);
		songSave.setVisible(true);
		generatePaneProgressLbl.setVisible(true);

		if (file != null) {

			Settings settings = new Settings(file.getAbsolutePath(), genre, settingPaneMetrics.isSelected() ? -1 : bpm);
			songGenerator = new SongGenerator(settings);
			generatePaneProgress.progressProperty().bind(songGenerator.progressProperty());
			songGenerator.messageProperty().addListener((observable, oldVal, newVal) -> generatePaneProgressLbl.setText(newVal));

			Thread th = new Thread(songGenerator);
			th.setDaemon(true);
			th.start();

			songSave.setDisable(false);
		}
	}

	/**
	 * opens native file explorer and stores song at selected location
	 */
	@FXML
	void saveFile() {
		if (songGenerator.getSeq() != null) {
			File saveSong = fileSaver.showSaveDialog(new Stage());
			if (saveSong != null) {
				try {
					songGenerator.getSeq().createFile(String.valueOf(saveSong.toPath()));
					((Stage) settingPaneCb.getScene().getWindow()).close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * opens the user guide
	 */
	@FXML
	void showGuide() {
		try {
			Desktop.getDesktop().open(new File("src/main/resources/ui/guide.pdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets initial settings
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// hide setting elements on launch
		settingPaneBpm.setVisible(false);
		settingPaneSlider.setVisible(false);
		settingPaneBack.setVisible(false);
		settingPaneGenre.setVisible(false);
		settingPaneCb.setVisible(false);
		settingPaneMetrics.setVisible(false);

		// can not generate or save song unless a file is loaded
		songGenerate.setDisable(true);
		songSave.setDisable(true);

		// progress not visible on launch
		generatePaneProgress.setVisible(false);
		songSave.setVisible(false);
		generatePaneProgressLbl.setVisible(false);

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
		settingPaneSlider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
			settingPaneBpm.setText("BPM: " + newVal.intValue());
			bpm = newVal.intValue();
		});

		// set choice options (genres) for choiceBox
		settingPaneCb.getItems().addAll(genres);
		settingPaneCb.setValue(genre.toString());

		settingPaneCb.setOnAction(actionEvent -> setGenre());
	}
}
