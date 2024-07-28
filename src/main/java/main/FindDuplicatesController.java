package main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.SearchDuplicateFiles.*;

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
    private TableView<FileInfo> tableView;

    @FXML
    private TableColumn<FileInfo, Integer> tableNumber;

    @FXML
    private TableColumn<FileInfo, String> tableName;

    @FXML
    private TableColumn<FileInfo, ImageView> tableImage;

    @FXML
    private TableColumn<FileInfo, String> tableWay;

    @FXML
    private TableColumn<FileInfo, Long> tableSize;

    protected ObservableList<FileInfo> fileDataList = FXCollections.observableArrayList();

    @FXML
    private TextField textWay;

    // Системные директории
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
            }
        }

    }

    /**
     * Кнопка запуска поиска дубликатов файлов
     * @param event
     */
    @FXML
    void btnStartSearch(ActionEvent event) {

        boolean nameSelected = checkName.isSelected();
        boolean sizeSelected = checkSize.isSelected();
        boolean contentSelected = checkContent.isSelected();

        // Проверка выбора параметров поиска
        if (!nameSelected && !sizeSelected && !contentSelected) {
            showAlert("Ошибка", "Выберите параметры поиска");
            return;
        }

        // Проверка выбранной дирекцией
        String directoryPath = textWay.getText();
        if (directoryPath == null || directoryPath.isEmpty()) {
            showAlert("Ошибка", "Выберите папку для поиска дубликатов файла");
            return;
        }

        // Проверка выбора типа данных
        String dataType = choiceDataType.getValue();
        if (dataType == null) {
            showAlert("Ошибка", "Выберите тип данных");
            return;
        }

        FileType fileType = getFileType(dataType);
        if (fileType == null) {
            showAlert("Ошибка", "Неправильный тип данных");
            return;
        }

        // Создание и запуск процессора поиска дубликатов
        DuplicateFilesProcessor processor = new DuplicateFilesProcessor(fileType, nameSelected, sizeSelected, contentSelected);
        processor.processFiles(directoryPath);

    }

    /**
     * Выбранный пользователем тип данных
     * @param dataType
     * @return
     */
    private FileType getFileType(String dataType) {
        switch (dataType) {
            case "Медиа файлы":
                return new MediaFileType();
            case "Документы":
                return new DocumentFileType();
            case "Все поддерживаемые файлы":
                return new AnyFileType();
            default:
                return null;
        }

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
     * ДОРАБОТАТЬ МЕТОТ ИНИЦИАЛИЗАЦИИ
     * 1. Реализовать функцию для очистки таблицы
     * 2. Реализовать, чтобы данные обновлялись со всех трех файлов
     * !!! 3. Доработать функцию для работы с документами !!!
     * 4. Для реализации полноэкранногог режима переделать в SceneBuilder главное окно в BorderPane
     */
    @FXML
    void initialize() {
        choiceDataType.getItems().addAll("Медиа файлы", "Документы", "Все поддерживаемые файлы");

        tableNumber.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
        tableName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableImage.setCellValueFactory(cellData -> cellData.getValue().imageViewProperty());
        tableWay.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
        tableSize.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        tableView.setItems(DuplicateFilesName.fileDataList);
        tableView.setItems(DuplicateFilesSize.fileDataList);
        tableView.setItems(DuplicateFilesContent.fileDataList);
    }
    
}
