package main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FindDuplicatesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnChoosingDirectory;

    @FXML
    private Button btnStartSearch;

    @FXML
    private CheckBox checkContent;

    @FXML
    private CheckBox checkName;

    @FXML
    private CheckBox checkSize;

    @FXML
    private ChoiceBox<String> choiceDataType;

    @FXML
    private TextField textWay;

    private boolean nameSelected = false;
    private boolean sizeSelected = false;
    private boolean contentSelected = false;
    private String dataTypeSelected = null;

    private boolean isSystemDirectory(File directory) {

        String userName = System.getProperty("user.name");
        String appDataPath = "C:\\Users\\" + userName + "\\AppData";

        String[] systemPaths = {
            System.getenv("SystemRoot"), System.getenv("PerfLogs"), System.getenv("Recovery"), 
            System.getenv("ProgramFiles"), System.getenv("ProgramFiles(x86)"), System.getenv("Windows"), 
            System.getenv("ProgramData"), appDataPath
        };
        for (String systemPath : systemPaths) {
            if (systemPath != null && directory.getAbsolutePath().startsWith(systemPath)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void btnChoosingDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) btnChoosingDirectory.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            if (isSystemDirectory(selectedDirectory)) {
                showAlert("Ошибка", "Выбранная папка является системной. Пожалуйста, выберите другую папку.");
            } else {
                textWay.setText(selectedDirectory.getAbsolutePath());
            }
        }
    }

    @FXML
    void btnStartSearch(ActionEvent event) {

        // Проверка выбора параметров поиска
        nameSelected = checkName.isSelected();
        sizeSelected = checkSize.isSelected();
        contentSelected = checkContent.isSelected();

        if (!nameSelected && !sizeSelected && !contentSelected) {
            showAlert("Ошибка", "Выберите параметры поиска");
            return;
        }

        // Проверка выбора типа данных
        dataTypeSelected = choiceDataType.getValue();
        if (dataTypeSelected == null) {
            showAlert("Ошибка", "Выберите тип данных");
            return;
        }

        // Проверка выбранной директории
        String directoryPath = textWay.getText();
        if (directoryPath == null || directoryPath.isEmpty()) {
            showAlert("Ошибка", "Выберите папку для поиска дубликатов файла");
            return;
        }

        

        // // Дальнейшая логика поиска дубликатов
        // System.out.println("Начинаем поиск дубликатов...");
        // System.out.println("Параметры поиска: по названию=" + nameSelected + ", по размеру=" + sizeSelected + ", по содержимому=" + contentSelected);
        // System.out.println("Тип данных: " + dataTypeSelected);
        // System.out.println("Директория: " + directoryPath);
        
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        choiceDataType.getItems().add("Медиа файлы");
        choiceDataType.getItems().add("Документы");
        choiceDataType.getItems().add("Все поддерживаемые файлы");
    }
}