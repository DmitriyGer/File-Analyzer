package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.SortMediaFiles.IsMediaFile;
import main.SortMediaFiles.SotrFile;

public class SortMediaFilesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnChoosingDirectory;

    @FXML
    private Button btnStartSort;

    @FXML
    private CheckBox checkByDay;

    @FXML
    private CheckBox checkByMonth;

    @FXML
    private CheckBox checkByYear;

    @FXML
    private ChoiceBox<String> choiceOutputData;

    @FXML
    private TextField textWay;

    @FXML
    private TextField textWayNewDirectory;

    private Stage primaryStage;
    private File selectedNewDirectory;

    protected void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void btnChoosingDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) btnChoosingDirectory.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            if (isSystemDirectory(selectedDirectory)) {
                showErrorAlert("Feeler Manager. Ошибка", "Выбранная папка является системной. Пожалуйста, выберите другую папку.");
            } else {
                textWay.setText(selectedDirectory.getAbsolutePath());
                textWay.end();
            }
        }
    }

    @FXML
    void btnStartSort(ActionEvent event) throws IOException {
        boolean yearSelected = checkByYear.isSelected();
        boolean monthSelected = checkByMonth.isSelected();
        boolean daySelected = checkByDay.isSelected();

        if (!yearSelected && !monthSelected && !daySelected) {
            showErrorAlert("Feeler Manager. Ошибка", "Выберите параметры сортировки");
            return;
        }

        String directoryPath = textWay.getText();
        if (directoryPath == null || directoryPath.isEmpty()) {
            showErrorAlert("Feeler Manager. Ошибка", "Выберите исходную папку для сортировки файлов");
            return;
        }

        File sourceDirectory = new File(directoryPath);
        List<File> mediaFiles = IsMediaFile.getMediaFiles(sourceDirectory);
        String choice = choiceOutputData.getValue();

        switch (choice) {
            case "Отсортировать в текущей папке":
                selectedNewDirectory = sourceDirectory;
                break;
            case "Переместить отсортированные файлы в":
                if (selectedNewDirectory == null) {
                    showErrorAlert("Feeler Manager. Ошибка", "Пожалуйста, выберите папку для перемещения файлов");
                    return;
                }
                break;
            case "Копировать отсортированные файлы в":
                if (selectedNewDirectory == null) {
                    showErrorAlert("Feeler Manager. Ошибка", "Пожалуйста, выберите папку для копирования файлов");
                    return;
                }
                break;
        }

        SotrFile.sortFiles(mediaFiles, yearSelected, monthSelected, daySelected, selectedNewDirectory, choice);
        showSuccessAlert("Файлы успешно отсортированы.");
        
    }

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
     * Функция для показа сообщения об ошибке
     * @param title
     * @param content
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Функция для показа сообщения о успешном выполнении алгоритма
     * @param title
     * @param content
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Feeler Manager. Операция завершена");
        alert.setHeaderText(null); // Убираем заголовок
        alert.setContentText(message);

        // Загрузка изображения зеленой галочки
        Image image = new Image(getClass().getResourceAsStream("SortMediaFiles/Image/iconSuccessfully.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);

        // Установка изображения в качестве иконки
        alert.setGraphic(imageView);

        alert.showAndWait();
    }

    
    /** 
     * Доработки на будущее:
     *      1. Добавить функцию сортировки по месторасположению, если таковой папметр имеется
     *      2. Добавить фильтрайию по людям
     */
    @FXML
    void initialize() {
        choiceOutputData.getItems().addAll("Отсортировать в текущей папке", "Переместить отсортированные файлы в", "Копировать отсортированные файлы в");
        choiceOutputData.setValue("Отсортировать в текущей папке");
        textWayNewDirectory.setVisible(false);
        choiceOutputData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Переместить отсортированные файлы в") || newValue.equals("Копировать отсортированные файлы в")) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Feeler Manager. Выберите директорию");
                selectedNewDirectory = directoryChooser.showDialog(primaryStage);
                if (selectedNewDirectory != null) {
                    if (isSystemDirectory(selectedNewDirectory)) {
                        showErrorAlert("Feeler Manager. Ошибка", "Выбранная папка является системной. Пожалуйста, выберите другую папку.");
                    } else {
                        textWayNewDirectory.setText(selectedNewDirectory.getAbsolutePath());
                        textWayNewDirectory.setVisible(true);
                        textWayNewDirectory.end();
                    }
                }
            } else {
                textWayNewDirectory.setVisible(false);
                selectedNewDirectory = null;
            }
        });
    }
}