package main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.DiskAnalyzer.AnalyzerChar;
import main.DiskAnalyzer.AnalyzerConductor;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
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
    private Button btnClearWayAndPieChart;

    @FXML
    private CheckBox checkTreeView;

    @FXML
    private AnchorPane paneViewAnalyzer;

    @FXML
    private PieChart pieChart;

    @FXML
    private TextField textWay;

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

            // Передача данных, если уже выполнен анализ
            if (selectedDirectory != null) {
                controller.setData(selectedDirectory, sizes, previousPaths);
            }

            newStage.setTitle("Disk Analyzer");
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
        try {
            if (selectedDirectory != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
                Parent root = loader.load();
                ProgressBarController progressController = loader.getController();

                Stage progressStage = new Stage();
                progressStage.setTitle("Прогресс анализа");
                progressStage.setScene(new Scene(root));
                progressStage.initModality(Modality.APPLICATION_MODAL);
                progressController.setStage(progressStage);

                progressController.getBtnCancel().setOnAction(e -> {
                    progressController.cancelAnalysis();
                    progressStage.close();
                });

                progressStage.show();

                new Thread(() -> {
                    try {
                        rootTotalSpace = AnalyzerConductor.getDirectorySize(selectedDirectory);
                        TreeItem<StackPane> rootItem = AnalyzerConductor.createTreeItem(selectedDirectory, rootTotalSpace, directoryTreeView);
                        
                        Platform.runLater(() -> {
                            rootItem.setExpanded(true);
                            directoryTreeView.setRoot(rootItem);
                        });

                        updateProgressBar(progressController, rootTotalSpace, selectedDirectory);

                        Platform.runLater(() -> {
                            if (!progressController.isCancelled()) {
                                directoryTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                                    if (newValue != null) {
                                        File selectedFile = new File(newValue.getValue().getId());
                                        long selectedDirectorySize = selectedFile.isDirectory() ? AnalyzerConductor.getDirectorySize(selectedFile) : rootTotalSpace;
                                        AnalyzerConductor.updateTreeItems(selectedFile, newValue, selectedDirectorySize, directoryTreeView);
                                    }
                                });
                            }
                            progressStage.close();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            progressStage.close();
                            System.out.println("Произошла ошибка при обработке файлов: " + e.getMessage());
                        });
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка при обработке файлов: " + e.getMessage());
        }
    }

    private void updateProgressBar(ProgressBarController progressController, long totalSpace, File directory) {
        AnalyzerChar analyzer = new AnalyzerChar();
        analyzer.setProgressCallback((progress, total) -> {
            if (progressController.isCancelled()) return;
            double percentage = (double) progress / totalSpace;
            Platform.runLater(() -> {
                progressController.getProgressBar().setProgress(percentage);
                progressController.getLabelProgress().setText(String.format("%.2f%%", percentage * 100));
            });
        });

        analyzer.calculateDirectorySize(Path.of(directory.getAbsolutePath()));
    }
   
    /**
     * Кнопка обнуления пути и чистка pieChart
     * @param event
     */
    @FXML
    void btnClearWayAndPieChart(ActionEvent event) {
        textWay.clear();
        pieChart.getData().clear();
    }

    /**
     * Добавить прогрессБар
     */
    @FXML
    public void initialize() {
        
    }

}

