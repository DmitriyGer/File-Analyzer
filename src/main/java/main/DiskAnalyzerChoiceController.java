package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class DiskAnalyzerChoiceController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnChoice;

    @FXML
    private CheckBox checkPieChart;

    @FXML
    private CheckBox checkTreeView;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private BorderPane paneBorderContent;

    @FXML
    void btnChoice(ActionEvent event) {

        boolean checkPie = checkPieChart.isSelected();
        boolean checkTree = checkTreeView.isSelected();

        System.out.println(checkPie);
        System.out.println(checkTree);

        // Проверка выбора параметров анализатора
        if (!checkPie && !checkTree) {
            showAlert("Ошибка", "Выберите параметры поиска");
            return;
        }

        if (checkPie) {
            loadPage("DiskAnalyzerChar.fxml");
        } else if (checkTree) {
            loadPage("DiskAnalyzerConductor.fxml");
        }

    }

    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            paneBorderContent.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Вывод сообщений об ошибке
     * 
     * @param title
     * @param content
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void initialize() {

        // Добавляем слушатели изменений состояния CheckBox
        checkPieChart.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkTreeView.setSelected(false);
            }
        });

        checkTreeView.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkPieChart.setSelected(false);
            }
        });

    }
}
