package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.DiskAnalyzer.AnalyzerChar;
import java.awt.Desktop;

public class DiskAnalyzerCharController {

    private Stage stage;
    private Map<String, Long> sizes;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private Stack<String> previousPaths = new Stack<>();
    private File selectedDirectory;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnChoosingDirectory;

    @FXML
    private Button btnGoBack;

    @FXML
    private Button btnNewWindow;

    @FXML
    private Button btnStartAnalys;
    
    @FXML
    private Button btnClearWayAndPieChart;

    @FXML
    private CheckBox checkPieChart;

    @FXML
    private CheckBox checkTreeView;

    @FXML
    private AnchorPane paneViewAnalyzer;

    @FXML
    private PieChart pieChart;

    @FXML
    private TextField textWay;

    @FXML
    private Label textNewWindow;

    public void setStage(Stage stage) {
        this.stage = stage;
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
     * Кнопка возвращения на предыдущую директорию
     * @param event
     */
    @FXML
    void btnGoBack(ActionEvent event) {
        if (!previousPaths.isEmpty()) {
            String path = previousPaths.pop();
            refillChart(path);
        }
    }

    /**
     * Кнопка открытия анализатора в новом окне
     * @param event
     */
    @FXML
    void btnNewWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DiskAnalyzerChar.fxml"));
            Parent root = fxmlLoader.load();
            DiskAnalyzerCharController controller = fxmlLoader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);
            controller.hideNewWindowButton();

            // Передача данных в новый контроллер
            controller.setData(selectedDirectory, sizes, previousPaths);

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
            refillChart(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Кнопка запуска анализатора
     * @param event
     */
    @FXML
    void btnStartAnalys(ActionEvent event) {
        if (selectedDirectory != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
                Parent root = loader.load();

                ProgressBarController progressController = loader.getController();
                Stage progressStage = new Stage();
                progressController.setStage(progressStage);
                progressStage.setScene(new Scene(root));
                progressStage.initModality(Modality.APPLICATION_MODAL);
                progressStage.initStyle(StageStyle.UTILITY);
                progressStage.setTitle("Загрузка...");

                progressController.getBtnCancel().setOnAction(e -> {
                    progressController.cancelAnalysis();
                    progressStage.close();
                });

                progressStage.show();

                // Запуск анализа в отдельном потоке
                new Thread(() -> {
                    AnalyzerChar analyzer = new AnalyzerChar();
                    analyzer.setProgressCallback((progress, total) -> {
                        if (progressController.isCancelled()) {
                            // Остановка анализа (например, выброс исключения или досрочный возврат)
                            return;
                        }
                        
                        double percentage = (double) progress / total;
                        Platform.runLater(() -> {
                            progressController.getProgressBar().setProgress(percentage);
                            progressController.getLabelProgress().setText(String.format("%.2f%%", percentage * 100));
                        });
                    });
                    sizes = analyzer.calculateDirectorySize(Path.of(selectedDirectory.getAbsolutePath()));

                    Platform.runLater(() -> {
                        if (!progressController.isCancelled()) {
                            refillChart(selectedDirectory.getAbsolutePath());
                        }
                        progressStage.close();
                    });
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Показать предупреждающее сообщение
            showAlertERROR("Feeler Manager. Ошибка", "Выберите диск или папку");
        }
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
     * Обрабатывающая функция
     */
    private void refillChart(String path) {
        pieChartData.clear();
        pieChartData.addAll(
            sizes.entrySet()
                .parallelStream()
                .filter(entry -> {
                    Path parent = Path.of(entry.getKey()).getParent();
                    return parent != null && parent.toString().equals(path);
                })
                .map(entry -> {
                    String displayName = entry.getKey() + " (" + formatSize(entry.getValue()) + ")";
                    return new PieChart.Data(displayName, entry.getValue());
                })
                .collect(Collectors.toList())
        );

        pieChart.setData(pieChartData);

        pieChart.getData().forEach(data -> {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getClickCount() == 2) { // Проверяем двойной клик
                    String dir = data.getName().split(" \\(")[0]; // Удаляем информацию о размере
                    File file = new File(dir);
                    try {
                        openFileInExplorer(file);
                    } catch (Exception e) {
                        showAlertERROR("Feeler Manager. Ошибка", "Не удалось открыть файл: " + file.getPath(), e.getMessage());
                    }
                } else {
                    String dir = data.getName().split(" \\(")[0]; // Удаляем информацию о размере
                    File file = new File(dir);
                    if (file.isDirectory()) {
                        previousPaths.push(path);
                        refillChart(dir);
                    }
                }
            });
        });
    }

    @SuppressWarnings("deprecation")
    private void openFileInExplorer(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    if (file.isDirectory()) {
                        desktop.open(file);
                    } else {
                        String command = String.format("explorer /select,\"%s\"", file.getAbsolutePath());
                        Runtime.getRuntime().exec(command);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlertERROR("Feeler Manager. Ошибка открытия файла", "Не удалось открыть файл в проводнике.");
        }
    }

    /**
     * Функция вывода информационного сообщения
     * @param title
     * @param header
     * @param content
     */
    private void showAlertERROR(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
     * Функция подсчета занимаемого директорией места
     * @param size
     * @return
     */
    private String formatSize(long size) {
        DecimalFormat df = new DecimalFormat("#.##");
        if (size >= 1024 * 1024 * 1024) {
            return df.format(size / (1024.0 * 1024 * 1024)) + " GB";
        } else if (size >= 1024 * 1024) {
            return df.format(size / (1024.0 * 1024)) + " MB";
        } else if (size >= 1024) {
            return df.format(size / 1024.0) + " KB";
        } else {
            return size + " B";
        }
    }  

    @FXML
    void initialize() {
        // pieChart.setPrefSize(400, 400);
        // pieChart.setMinSize(300, 300);
    }

}
