package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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

    @FXML
    private Label path_label;

    @FXML
    void loadClicked() {
        file = fileChooser.showOpenDialog(new Stage());
        if ( file != null )
            path_label.setText(file.getPath());
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
        path_label.setText(file.getPath());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".txt or .pdf", "*.txt", "PDF", "*.pdf")
        );
        fileChooser.setTitle("Select .pdf or .txt file");
        fileChooser.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));
    }
}
