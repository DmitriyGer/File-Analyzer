package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAuthorization;

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
    private ImageView viewPhoto;

    @FXML
    void btnAuthorization(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Authorization.fxml"));
            Parent root = fxmlLoader.load();
            AuthorizationController controller = fxmlLoader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);

            newStage.setResizable(false);

            newStage.setTitle("Feeler Manager. Авторизация");
            newStage.setScene(new Scene(root));

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initStyle(StageStyle.DECORATED);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        
    }

    @FXML
    void btnShowInstruction(ActionEvent event) {

    }

    @FXML
    void btnShowToDeveloper(ActionEvent event) {

    }

    // ToolTip
    private void addTooltip(Button button, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
    }

    // Загрузчик нужной страницы
    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().setAll(root);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. Вместо кнопки "Войти" после авторизации должна появляться label с Логином, а при нажатии на Label 
     *    должно вылазить под ним окно, где будет кнопка выйти
     */
    @FXML
    void initialize() {

        addTooltip(btnAuthorization, "Авторизация");
        addTooltip(btnConnectAndroid, "Подключение к Android");
        addTooltip(btnDiskAnalyzer, "Анализатор дискового пространства");
        addTooltip(btnFindDuplicates, "Поиск дубликатов файлов");
        addTooltip(btnFixMetadata, "Исправление метаданных");
        addTooltip(btnSortMediaFiles, "Сортировка медиафайлов");
        addTooltip(btnInstruction, "Инструкция");
        addTooltip(btnToDeveloper, "О разработчике");
        
   
    }

}
