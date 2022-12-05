package gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    FileChooser fileChooser = new FileChooser();
    String userName = System.getProperty("user.name");
    File file = null;
    Integer bpm = 100;
    String[] genres = {"Blues", "Pop"};
    String genre = "";

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
    void loadClicked() {
        file = fileChooser.showOpenDialog(new Stage());
        if ( file != null )
            load_pane_path.setText(file.getPath());
    }

    void toggleVisibility() {
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

    @FXML
    void toggleClicked() {
        if ( file != null )
            toggleVisibility();
    }

    boolean isValidFile(String fileName) {
        String fileType = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0)
            fileType = fileName.substring(i+1);
        return fileType.equals("txt") || fileType.equals("pdf");
    }

    @FXML
    void dragFile(DragEvent event) {
        Dragboard dB = event.getDragboard();
        if (isValidFile(dB.getUrl()))
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
     }

    @FXML
    void dropFile(DragEvent event) {
        Dragboard dB = event.getDragboard();
        file = dB.getFiles().get(0);
        load_pane_path.setText(file.getPath());
    }

    public void setGenre() {
        String newGenre = setting_pane_cb.getValue();
        setting_pane_genre.setText("Genre:" + newGenre);
        genre = newGenre;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // hide setting elements on launch
        setting_pane_bpm.setVisible(false);
        setting_pane_slider.setVisible(false);
        setting_pane_back.setVisible(false);
        setting_pane_genre.setVisible(false);
        setting_pane_cb.setVisible(false);

        // allow only pdf/txt-filter
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".txt or .pdf", "*.txt", "PDF", "*.pdf")
        );

        fileChooser.setTitle("Select .pdf or .txt file");

        // start searching in "desktop"-folder
        fileChooser.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));

        setting_pane_slider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal)->{
            setting_pane_bpm.setText("bpm:" + newVal.intValue());
            bpm = newVal.intValue();
        });

        // set choice options (genres) for choiceBox
        setting_pane_cb.getItems().addAll(genres);
        setting_pane_cb.setOnAction(actionEvent -> setGenre());
    }
}
