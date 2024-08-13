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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
    private TextFlow textFlow;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private BorderPane paneBorderContent;

    @FXML
    void btnChoice(ActionEvent event) {

        boolean checkPie = checkPieChart.isSelected();
        boolean checkTree = checkTreeView.isSelected();

        // Проверка выбора параметров анализатора
        if (!checkPie && !checkTree) {
            showAlert("Feeler Manager. Ошибка", "Выберите параметры поиска");
            return;
        }

        if (checkPie) {
            loadPage("DiskAnalyzerChar.fxml");
        } else if (checkTree) {
            loadPage("DiskAnalyzerConductor.fxml");
        }

    }

    /**
     * Загрузка страницы
     * @param fxmlFile
     */
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

    /**
     * Функция для создания форматированного текстового объекта
     * @param content
     * @return
     */
    private static Text createFormattedText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font("Bookman Old Style", 16));
        return text;
    }

    /**
     * Функция для добавления текстов в TextFlow
     * @param textFlow
     */
    private static void initializeTextFlow(TextFlow textFlow) {
        String[] texts = {
                "Диаграмный. Это мощное и удобное приложение для анализа использования",
                "дискового пространства, которое предоставляет визуализацию данных в ",
                "понятном и наглядном формате с использованием круговой диаграммы. ",
                "Этот инструмент обеспечивает глубокий анализ и детализацию всех данных, ",
                "хранящихся на вашем диске, помогая вам эффективно управлять и ",
                "оптимизировать дисковое пространство.\n\n\n",
                "Иерархический. Функция анализатора дискового пространства, пользователям видеть иерархию ",
                "файлов и папок, облегчая навигацию и понимание организации данных на жестком диске, ",
                "Эта функция является мощным инструментом для управления дисковым пространством, ",
                "обеспечивая наглядное представление и облегчая оптимизацию использования ресурсов хранения."
        };

        for (String str : texts) {
            textFlow.getChildren().add(createFormattedText(str));
        }
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

        initializeTextFlow(textFlow);

    }
}
