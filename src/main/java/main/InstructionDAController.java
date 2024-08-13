package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class InstructionDAController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView imageBack;

    @FXML
    private TextFlow txtInstruction;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Функция для создания форматированного текстового объекта
     * @param content
     * @return
     */
    private static Text createFormattedText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font("Bookman Old Style", 14));
        return text;
    }

    /**
     * Функция для добавления текстов в TextFlow
     * @param textFlow
     */
    private static void initializeTextFlow(TextFlow textFlow) {
        String[] texts = {
                "Порядок выполнения:",
                "\n1. Выбираете удобный для вас вывод данных:",
                "\n\tа. Круговая диаграмма",
                "\n\tб. Иерархический",
                "\n\n2. Выберите необходиму папку для анализа дискового пространства. Вы можете выбрать как отдельную\n    папку, так и диск целиком.",
                "\n\n3. Работа с кнопками:",
                "\n\tа. Выбрать. Предоставляется выбор папки или диска для анализа.",
                "\n\tб. Очистить. Очищает выбор директории, а так же удаляет данные анализа.",
                "\n\tв. Старт. Запускает анализ дискового пространства.",
                "\n\tг. Открыть в новом окне. Для удобства работы выводом данных было создано окно, позволяющее\n \t    открывать программу на весь экран, что позволяет эффективнее рабоать с программой.",
                "\n\tд. Назад. Доступно при использовании круговой диаграмы. При нажатии на кнопку происходит переход\n \t    в предыдущую папку.",
                "\n\n4.1 Работа с круговой диаграмой:",
                "\n\tа. После завершения сканирования, путем двойного нажатия на необходимый фрагмент диаграммы\n \t    можно перейти в папку и посмотреть ее занятость. Если двойным кликом нажать на файл, то он\n \t    откроется в проводнике",
                "\n\tб. При нажатии на кнопку 'Назад' происходит переход в предыдущую папку",
                "\n\n4.2 Работа с иерархическим выводом данных:",
                "\n\t— После завершения сканирования для каждой папки и файла появляется зелёная шкала. Она\n \t    графически показывает, сколько места занимает элемент в папке или на диске.",
                "\n\t— В правой части таблицы вы найдёте информацию о дате создания файла или папки, их размере и\n \t    процентном соотношении занимаемого ими места на диске."
        };

        for (String str : texts) {
            textFlow.getChildren().add(createFormattedText(str));
        }
    }

    private void loadInstruction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Instruction.fxml"));
            Parent instructionPage = loader.load();

            // Получаем контроллер и устанавливаем mainController
            InstructionController instructionController = loader.getController();
            instructionController.setMainController(mainController);
            
            mainController.setContent(instructionPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {

        initializeTextFlow(txtInstruction);

        imageBack.setOnMouseClicked(event -> loadInstruction());

    }

}
