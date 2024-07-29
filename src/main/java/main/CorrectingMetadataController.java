package main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class CorrectingMetadataController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnChoosingDirectory;

    @FXML
    private Button btnStartFix;

    @FXML
    private Button btnStartScan;

    @FXML
    private CheckBox checkPatternAuto;

    @FXML
    private CheckBox checkPattern;

    @FXML
    private CheckBox checkManually;

    @FXML
    private ChoiceBox<String> choiceOriginalPattern;

    @FXML
    private Label labelInstruction;

    @FXML
    private Label labelMatchFound;

    @FXML
    private Label labelMatchNotFound;

    @FXML
    private TableColumn<?, ?> tableCurrentDate;

    @FXML
    private TableColumn<?, ?> tableImage;

    @FXML
    private TableColumn<?, ?> tableName;

    @FXML
    private TableColumn<?, ?> tableNewDate;

    @FXML
    private TableColumn<?, ?> tableNumber;

    @FXML
    private TableColumn<?, ?> tableSelect;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> tableWay;

    @FXML
    private TextField textUserPattern;

    @FXML
    private TextField textWay;

    /**
     * Кнопка выбора директории
     * @param event
     */
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
                textWay.end();
            }
        }
    }

    /**
     * Кнопка запуска предварительного сканирования
     * @param event
     */
    @FXML
    void btnStartSearch(ActionEvent event) {

    }

    /**
     * Кнопка для запуска исправлений метаданных
     * @param event
     */
    @FXML
    void btnStartCorrect(ActionEvent event) {

    }

    /**
     * Проверка на системные директории
     * @param directory
     * @return
     */
    private boolean isSystemDirectory(File directory) {

        String userName = System.getProperty("user.name");
        String appDataPath = "C:\\Users\\" + userName + "\\AppData";

        String[] systemPaths = {
            System.getenv("SystemRoot"), System.getenv("PerfLogs"), System.getenv("Recovery"), 
            System.getenv("ProgramFiles"), System.getenv("ProgramFiles(x86)"), 
            System.getenv("Windows"), System.getenv("ProgramData"), appDataPath
        };
        for (String systemPath : systemPaths) {
            if (systemPath != null && directory.getAbsolutePath().startsWith(systemPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Вывод сообщений об ошибке
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

    /**
     * 1. Добавить возможность исправления метаданных в таблице после предварительного сканирования
     */
    @FXML
    void initialize() {

        // Заполняем ChoiceBoxЮ, задаем значение по умолчанию, делаем неактивным и добавляем слушателя
        choiceOriginalPattern.getItems().addAll("yyyyMMdd_HHmmss - Камера", "yyyyMMdd-HHmmss - Скриншоты", "'yyyyMMdd'-WA - WhatsApp", "yyyyMMdd_HHmmss - Telegram", "yyyyMMddHHmmss - Общий");
        choiceOriginalPattern.setValue("yyyyMMdd_HHmmss - Камера"); 
        choiceOriginalPattern.setDisable(true);
        checkPattern.selectedProperty().addListener((observable, oldValue, newValue) -> {
            choiceOriginalPattern.setDisable(!newValue);
        });


        // Задаем textUserPattern значение по умолчанию, делаем неактивным и добавляем слушателя
        textUserPattern.setText("yyyyMMdd_HHmmss");
        textUserPattern.setDisable(true);
        checkManually.selectedProperty().addListener((observable, oldValue, newValue) -> {
            textUserPattern.setDisable(!newValue);
        });


        // Добавляем слушатели изменений состояния CheckBox
        checkPatternAuto.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkPattern.setSelected(false);
                checkManually.setSelected(false);
            }
        });

        checkPattern.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkPatternAuto.setSelected(false);
                checkManually.setSelected(false);
            }
        });

        checkManually.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkPatternAuto.setSelected(false);
                checkPattern.setSelected(false);
            }
        });


        // Подсказка для пользователя к Label labelInstruction 
        Tooltip tooltipInstruction = new Tooltip("Вводите только цифры в той последовательности, в которой они указаны в навании файла \n"
                                                    + "yyyy - соответствует году \n"
                                                    + "MM - соответствует месяцу \n"
                                                    + "dd - соответствует дню \n"
                                                    + "HH - соответствует часам \n"
                                                    + "mm - соответствует минутам \n"
                                                    + "ss - соответствует секундам \n"
                                                    + "Пример: IMG_20240729_220130.jpg \n"
                                                    + "                        yyyyMMdd_HHmmss \n"
                                                    );
        labelInstruction.setTooltip(tooltipInstruction);

    }

}
