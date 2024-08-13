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

public class InstructionFDController {

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
                "\n\n1. Выберите необходиму папку для поиска дубликатов. Вы можете выбрать как отдельную папку, так и\n    диск целиком.",
                "\n\n2. Выбор параметров поиска. Приложение умеет выполнять поиск одинаковых файлов по трем параметрам:",
                "\n\ta. По названию. Программа ищет файлы с одинаковым названием и выводит пользователю\n\t    информацию о них в таблицу.",
                "\n\tб. По размеру. Программа анализирует выбранную пользователем папку, собирает размеры всех\n\t    файлов и возвращает группы файлов с одинаковым размером.",
                "\n\tв. По содержимому. Программа анализирует выбранную пользователем папку и выявляет одинаковые\n\t    по содержанию файлы. Данная сортировка наиболее эффективная для выяления дубликатов.",
                "\n\n3. Выбор типа данных. Программа имеет три параметра на выбор пользователя:",
                "\n\tа. Медиа файлы. К медиафайлам относятся такие форматы, как: .png, .jpeg, .jpg, .gif, .mp4, .mp3, .waw,\n \t    .avi",
                "\n\tб. Документы. К документам относятся такие форматы, как: .doc, .docx, .xls, .xlsx, .pdf, .ppt, .pptx, .txt",
                "\n\tв. Все поддерживаемые форматы. Поддерживаются все популярные форматы медиафайлов и\n\t    документов, которые описаны выше.",
                "\n\n4. Работа с кнопками:\n",
                "\ta. Начать поиск. При нажатии на кнопку, запускается процесс поиска дубликатов. Результат поиска\n \t    выводится в таблицу.",
                "\n\tб. Открыть в новом окне. Для удобства работы с таблицей было создано окно, позволяющее открывать \n \t    программу на весь экран, что позволяет эффективнее рабоать с программой.",
                "\n\n5. Работа с таблицей:",
                "\n\tа. В случае нахождения дубликатов файлов, в таблице будет отображаться основная информация о них.",
                "\n\tб. Для открытия файла в проводнике, достаточно нажать на него два раза."
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
