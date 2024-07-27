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
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableColumn<FileInfo, Integer> columnIndex;
    
    @FXML
    private TableColumn<FileInfo, String> columnFileName;
    
    @FXML
    private TableColumn<FileInfo, ImageView> columnFileType;
    
    @FXML
    private TableColumn<FileInfo, String> columnFilePath;
    
    @FXML
    private TableColumn<FileInfo, Long> columnFileSize;

    private ObservableList<FileInfo> data = FXCollections.observableArrayList();

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
        boolean nameSelected = checkName.isSelected();
        boolean sizeSelected = checkSize.isSelected();
        boolean contentSelected = checkContent.isSelected();

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

    // Вывод сообщений об ошибке
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        choiceDataType.getItems().addAll("Медиа файлы", "Документы", "Все поддерживаемые файлы");

        columnIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
        columnFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnFileType.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnFilePath.setCellValueFactory(new PropertyValueFactory<>("path"));
        columnFileSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tableView.setItems(data);
    }
    
}
