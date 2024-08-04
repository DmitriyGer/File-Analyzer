package main;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import main.DiskAnalyzer.Analyzer;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class DiskAnalyzerController {
    private Stage stage;
    private Map<String, Long> sizes;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private Stack<String> previousPaths = new Stack<>();

    @FXML
    private Button btnChooseDir;

    @FXML
    private Button btnGoBack;

    @FXML
    private Button btnNewWindow;

    @FXML
    private PieChart pieChart;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Кнопка выбора директории
     * @param event
     */
    @FXML
    private void btnChooseDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            sizes = new Analyzer().calculateDirectorySize(Path.of(path));
            previousPaths.clear(); // Очистить при выборе новой директории
            refillChart(path);
        }
    }

    /**
     * Кнопка возвращения на предыдущую директорию
     * @param event
     */
    @FXML
    private void btnGoBack(ActionEvent event) {
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
    private void btnNewWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DiskAnalyzer.fxml"));
            Parent root = fxmlLoader.load();
            DiskAnalyzerController controller = fxmlLoader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);

            newStage.setTitle("Disk Analyzer");
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initStyle(StageStyle.DECORATED);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                String dir = data.getName().split(" \\(")[0]; // Удаляем информацию о размере
                File file = new File(dir);
                if (file.isDirectory()) {
                    previousPaths.push(path);
                    refillChart(dir);
                } else {
                    showAlert("Информация", "Внимание!", "Выбранный элемент является файлом.");
                }
            });
        });
    }
    
    /**
     * Функция вывода и обработки ошибок
     * @param title
     * @param header
     * @param content
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
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

    /** 
     * 1. При выборе конечного файла, например видео, при нажатии открывать его (сейчас выводит ошибку)
     *    Сейчас выводится сообщение о том, что это файл и дальше перейти нельзя
     */
    @FXML
    public void initialize() {
        // Initialization, if needed
    }

}