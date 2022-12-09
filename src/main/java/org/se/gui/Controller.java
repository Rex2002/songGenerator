package org.se.gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    // userName gibt den Namen des aktuellen Benutzers -> Pfad zum Laden / Speichern
    String userName = System.getProperty("user.name");

    // file enthält die zu untersuchende txt-/pdf-Datei
    File file = null;

    // bpm gibt die im Song zu verwendenden Beats per Minute an
    int bpm = 100;

    // genres beinhaltet die Auswahl an verfügbaren Musik-Genres
    String[] genres = {"Blues", "Pop"};

    // genre gibt das aktuell ausgewählte Genre an
    String genre = "";

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
    private Label setting_pane_generate;

    @FXML
    private ProgressBar setting_pane_progress;

    @FXML
    private Label setting_pane_progressLbl;

    @FXML
    private Button setting_pane_save;

    @FXML
    private Button setting_pane_generateBtn;

    /**
     * Öffnet den Datei-Explorer und lädt die ausgewählte Datei.
     */
    @FXML
    void loadClicked() {
        file = fileChooser.showOpenDialog(new Stage());
        if ( file != null )
            load_pane_path.setText(file.getPath());
    }

    /**
     * Erhöht den Fortschrittsbalken und zeigt die fertigen Funktionen an.
     *
     * @param msg die abgeschloßene Funktion
     * @param val Wert, um den der Fortschritt erhöht wird
     */
    void increaseProgress(String msg, double val) {
        setting_pane_progressLbl.setText("Progress: " + msg + " done...");
        progress += val;
        setting_pane_progress.setProgress(progress);
    }

    /**
     * Setzt alle Load-Elemente auf "Unsichtbar" und alle Setting-Elemente auf "Sichtbar" und umgekehrt.
     */
    @FXML
    void toggleClicked() {
        if ( file != null ) {
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
            setting_pane_generate.setVisible(showLoad);
            setting_pane_progress.setVisible(showLoad);
            setting_pane_progressLbl.setVisible(showLoad);
            setting_pane_generateBtn.setVisible(showLoad);
            setting_pane_save.setVisible(showLoad);
        }
    }

    /**
     * Gibt an, ob ein File eine .pdf- oder .txt-Datei ist.
     *
     * @param fileName die zu überprüfende Datei
     * @return ist .pdf- oder .txt-Datei
     */
    boolean isValidFile(String fileName) {
        String fileType = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0)
            fileType = fileName.substring(i+1);
        return fileType.equals("txt") || fileType.equals("pdf");
    }

    /**
     * Reagiert onDrag.
     * Lässt valide Dateien in den Drag&Drop-Bereich ziehen.
     */
    @FXML
    void dragFile(DragEvent event) {
        Dragboard dB = event.getDragboard();
        if (isValidFile(dB.getUrl()))
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
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
    }

    /**
     * Setzt das Genre aus der ChoiceBox (DropDownMenu).
     */
    public void setGenre() {
        String newGenre = setting_pane_cb.getValue();
        setting_pane_genre.setText("Genre: " + newGenre);
        genre = newGenre;
    }

    /**
     * Startet die Songgenerierung.
     */
    @FXML
    void generateSong() {
        progress = 0;
        setting_pane_progress.setProgress(0);

        if (!genre.equals("")) {
            // dummy

        }
    }

    /**
     * Öffnet den Datei-Explorer und speichert den Song in dem ausgewählten Ordner.
     */
    @FXML
    void saveFile() {
        File saveSong = fileChooser.showSaveDialog(new Stage());
    }

    /**
     * Öffnet das Benutzerhandbuch.
     */
    @FXML
    void showGuide() {
        try {
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
        setting_pane_generate.setVisible(false);
        setting_pane_progress.setVisible(false);
        setting_pane_progressLbl.setVisible(false);
        setting_pane_generateBtn.setVisible(false);
        setting_pane_save.setVisible(false);

        // allow only pdf/txt-filter
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".txt or .pdf", "*.txt", "PDF", "*.pdf")
        );

        fileChooser.setTitle("Select .pdf or .txt file");

        // start searching in "desktop"-folder
        //fileChooser.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));

        // handle slider changes
        setting_pane_slider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal)->{
            setting_pane_bpm.setText("bpm: " + newVal.intValue());
            bpm = newVal.intValue();
        });

        // set choice options (genres) for choiceBox
        setting_pane_cb.getItems().addAll(genres);
        setting_pane_cb.setOnAction(actionEvent -> setGenre());
    }
}
