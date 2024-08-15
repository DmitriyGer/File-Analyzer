package main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.DiskAnalyzer.AnalyzerConductor;
import main.DiskAnalyzer.DiskAnalysisTask;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Контроллер для анализатора дискового пространства - исследование в проводнике
 */
public class DiskAnalyzerConductorController {

    @FXML
    private TreeView<StackPane> directoryTreeView;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnChoosingDirectory;

    @FXML
    private Button btnNewWindow;

    @FXML
    private Button btnStartAnalys;
    
    @FXML
    private Button btnClearWayAndTreeView;

    @FXML
    private CheckBox checkTreeView;

    @FXML
    private AnchorPane paneViewAnalyzer;

    @FXML
    private TextField textWay;

    @FXML
    private Label textNewWindow;

    private long rootTotalSpace;
    private File selectedDirectory;
    private Stage primaryStage;

    private Map<String, Long> sizes = new HashMap<>();
    private Stack<String> previousPaths = new Stack<>();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setSelectedDirectory(File selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    /**
     * Кнопка выбора директории
     * @param event
     */
    @FXML
    void btnChoosingDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) btnChoosingDirectory.getScene().getWindow();
        selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            textWay.setText(selectedDirectory.getAbsolutePath());
            textWay.end();
        }
    }

    /**
     * Кнопка открытия анализатора в новом окне
     * @param event
     */
    @FXML
    void btnNewWindow(ActionEvent event) {
        try {
            // Загрузка FXML файла для нового окна
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DiskAnalyzerConductor.fxml"));
            Parent root = fxmlLoader.load();

            // Получение контроллера для нового окна
            DiskAnalyzerConductorController controller = fxmlLoader.getController();
            Stage newStage = new Stage();
            controller.setPrimaryStage(newStage);
            controller.hideNewWindowButton();

            // Передача данных, если уже выполнен анализ
            if (selectedDirectory != null) {
                controller.setData(selectedDirectory, sizes, previousPaths);
            }

            newStage.setTitle("Feeler Manager. Анализатор дискового пространства");
            newStage.setScene(new Scene(root));

            // Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // newStage.setWidth(screenBounds.getWidth() * 1);
            // newStage.setHeight(screenBounds.getHeight() * 1);

            newStage.setMaximized(true); // Открытие окна на весь экран

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initStyle(StageStyle.DECORATED);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для скрытия кнопки
     */
    private void hideNewWindowButton() {
        if (btnNewWindow != null) {
            btnNewWindow.setVisible(false);
        }
        if (textNewWindow != null) {
            textNewWindow.setVisible(false);
        }
    }

    public void setData(File selectedDirectory, Map<String, Long> sizes, Stack<String> previousPaths) {
        this.selectedDirectory = selectedDirectory;
        this.sizes = sizes;
        this.previousPaths = previousPaths;

        if (selectedDirectory != null) {
            textWay.setText(selectedDirectory.getAbsolutePath());
            textWay.end();
            // Обновление UI или выполнение операций с полученными данными
            btnStartAnalys(new ActionEvent());
        }
    }

    /**
     * Кнопка запуска анализатора
     * @param event
     */
    public void btnStartAnalys(ActionEvent event) {
        if (selectedDirectory != null) {
            try {
                rootTotalSpace = AnalyzerConductor.getDirectorySize(selectedDirectory);
    
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressDialogAnalyzer.fxml"));
                Parent root = loader.load();
                ProgressBarController progressBarController = loader.getController();
                Stage progressStage = new Stage();
                progressStage.initModality(Modality.APPLICATION_MODAL);
                progressStage.setScene(new Scene(root));
                progressBarController.setStage(progressStage);
                progressStage.setTitle("Feeler Manager. Загрузка...");
                progressStage.setResizable(false);
                progressStage.show();
    
                // Запуск задачи анализа
                DiskAnalysisTask task = new DiskAnalysisTask(selectedDirectory, rootTotalSpace, directoryTreeView);
                progressBarController.getProgressBar().progressProperty().bind(task.progressProperty());
                progressBarController.getLabelProgress().textProperty().bind(task.messageProperty());
    
                task.setOnSucceeded(e -> progressStage.close());
                task.setOnCancelled(e -> progressStage.close());
    
                // Установка действия для кнопки "Отмена"
                progressBarController.getBtnCancel().setOnAction(e -> {
                    task.cancel();
                    progressBarController.cancelAnalysis();
                    Platform.runLater(progressStage::close); // Закрытие окна прогресса в JavaFX thread
                });
    
                // Запуск задачи в отдельном потоке
                new Thread(task).start();
    
            } catch (Exception e) {
                e.printStackTrace();
                showAlertERROR("Feeler Manager. Ошибка", "Произошла ошибка при обработке файлов: " + e.getMessage());
            }
        } else {
            showAlertERROR("Feeler Manager. Ошибка", "Выберите диск или папку");
        }
    }
   
    /**
     * Кнопка обнуления пути и чистка pieChart
     * @param event
     */
    @FXML
    void btnClearWayAndTreeView(ActionEvent event) {
        textWay.clear();
        directoryTreeView.setRoot(null);
    }

    /**
     * Функция вывода об ошибке
     * @param title
     * @param message
     */
    private void showAlertERROR(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 1. Доработать функцию остановки работы алгоритма в прогресс баре
     * 
     * Частично 2. Оптимизировать. Ест много ОЗУ и работает медленее, чем диаграма, а также вывод объемных
     *    директорий повторятеся. 
     *    2.1. Реализовать код, который будет подгружать сначала родительские директории, а затем дочерние, 
     *         при открытии родительских
     * 
     * 3. Написать код для очисти КЭШа при последующем запросе/нажатии кнопки "Отмена"/переходе на другую страницу.
     * 
     * ЕСТЬ 4. Написать исключения для системных директорий, чтобы алгоритм пропускал их.
     * 
     * ЕСТЬ 5. Работа с функцией, селать так, чтобы она запускалась фоном и не тормозила программу.
     *    Или написать об этом в инструкции.
     * 
     * 6. При сканировании диска C возможны ошибки, программа не выводит данные, а прогресс бар зависает.
     */
    @FXML
    public void initialize() {
        
    }
}