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

public class InstructionSMController {

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
                "\n\n1. Выберите необходиму папку для поиска и сортировки медиафайлов.",
                "\n\n2. Выберите необходимые параметры сортировки. Доступна сортировка по одному выбранному параметру\n    или по всем параметрам сразу. Типы сортировки:",
                "\n\tа. По годам. При выборе данного метода сортировки, программа будет искать файлы и сортировать\n \t    их по году создания медиафайла.",
                "\n\tб. По месяцам. При выборе данного метода сортировки, программа будет искать файлы и сортировать\n \t    их по месяцам создания медиафайла.",
                "\n\tв. По дням. При выборе данного метода сортировки, программа будет искать файлы и сортировать их\n \t    по дням создания медиафайла.",
                "\n\n3. Вывод отсортированных данных. Программа поддерживает три режима работы с данными пользователя:",
                "\n\tа. Отсортировать в текущей папке. В текущем режиме программа упорядочивает содержимое текущей\n \t    папки, создавая необходимые подпапки и перемещая в них отсортированные медиафайлы.",
                "\n\tб. Переместить отсортированные файлы в... Когда вы выбираете этот параметр, вам предоставляется\n \t    возможность выбрать папку для хранения и перемещения туда ваших медиафайлов в\n \t    отсортированном виде. Файлы удаляться из исходной папки и переместятся в новую в\n \t    отсортированном виде.",
                "\n\tв. Копировать отсортированные файлы в... Когда вы выбираете этот параметр, вам предоставляется\n \t    возможность выбрать папку для хранения и копирования туда ваших медиафайлов в\n \t    отсортированном виде. Файлы останутся в неотсортированном виде в исходной папке,\n \t    а в новой будут в отсортированном виде."
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
