package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnConnectAndroid;

    @FXML
    private Button btnDiskAnalyzer;

    @FXML
    private Button btnFindDuplicates;

    @FXML
    private Button btnFixMetadata;

    @FXML
    private Button btnSortMediaFiles;

    @FXML
    private Button btnInstruction;

    @FXML
    private Button btnToDeveloper;

    @FXML
    private AnchorPane contentPane;

    @FXML
    void btnShowFindDuplicates(ActionEvent event) {
        loadPage("FindDuplicates.fxml");
    }

    @FXML
    void btnShowSortMediaFiles(ActionEvent event) {
        loadPage("SortMediaFiles.fxml");
        
    }

    @FXML
    void btnShowFixMetadata(ActionEvent event) {
        loadPage("CorrectingMetadata.fxml");

    }

    @FXML
    void btnShowDiskAnalyzer(ActionEvent event) {
        loadPage("DiskAnalyzerChoice.fxml");

    }

    @FXML
    void btnShowConnectAndroid(ActionEvent event) {
        loadPage("ConnectAndroid.fxml");
    }

    @FXML
    void btnShowInstruction(ActionEvent event) {

    }

    @FXML
    void btnShowToDeveloper(ActionEvent event) {

    }

    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().setAll(root);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
 
    }

}
