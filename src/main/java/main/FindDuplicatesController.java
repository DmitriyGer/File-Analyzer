package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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
    private Button btnNewWindow;

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
    private TableColumn<FileInfo, Integer> tableNumber;

    @FXML
    private TableColumn<FileInfo, String> tableName;

    @FXML
    private TableColumn<FileInfo, ImageView> tableImage;

    @FXML
    private TableColumn<FileInfo, String> tableWay;

    @FXML
    private TableColumn<FileInfo, String> tableSize;

    protected ObservableList<FileInfo> fileDataList = FXCollections.observableArrayList();

    @FXML
    private TextField textWay;

    @FXML
    private Label textNewWindow;

    /**
     * Проверка на системные директории
     * @param directory
     * @return
     */
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
     * Кнопка выбора директории
     * @param event
     */
    @FXML
    void btnChoosingDirectory(ActionEvent event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) btnChoosingDirectory.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            if (isSystemDirectory(selectedDirectory)) {
                showAlertERROR("Feeler Manager. Ошибка", "Выбранная папка является системной. Пожалуйста, выберите другую папку.");
            } else {
                textWay.setText(selectedDirectory.getAbsolutePath());
                textWay.end();
            }
        }
    }

    /**
     * Кнопка запуска поиска дубликатов файлов
     * @param event
     */
    @FXML
    void btnStartSearch(ActionEvent event) {
        
        fileDataList.clear();
        tableView.setItems(FXCollections.observableArrayList());

        boolean nameSelected = checkName.isSelected();
        boolean sizeSelected = checkSize.isSelected();
        boolean contentSelected = checkContent.isSelected();

        String directoryPath = textWay.getText();
        if (directoryPath == null || directoryPath.isEmpty()) {
            showAlertERROR("Feeler Manager. Ошибка", "Выберите папку для поиска дубликатов файла");
            return;
        }

        if (!nameSelected && !sizeSelected && !contentSelected) {
            showAlertERROR("Feeler Manager. Ошибка", "Выберите параметры поиска");
            return;
        }

        String dataType = choiceDataType.getValue();
        if (dataType == null) {
            showAlertERROR("Feeler Manager. Ошибка", "Выберите тип данных");
            return;
        }

        FileType fileType = getFileType(dataType);
        if (fileType == null) {
            showAlertERROR("Feeler Manager. Ошибка", "Неправильный тип данных");
            return;
        }

        try {
            // Загрузка окна прогресс-бара
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
            VBox progressBarLayout = loader.load();
            ProgressBarController progressBarController = loader.getController();

            DuplicateFilesProcessor processor = new DuplicateFilesProcessor(fileType, nameSelected, sizeSelected, contentSelected);
            processor.setFileDataList(fileDataList);

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    processor.setOnProgressUpdate((progress, message) -> {
                        updateProgress(progress, 1.0);
                        updateMessage(message);
                    });

                    processor.processFiles(directoryPath);
                    return null;
                }
            };

            // Связывание прогресс-бара и лейбла с задачей
            progressBarController.getProgressBar().progressProperty().bind(task.progressProperty());
            progressBarController.getLabelProgress().textProperty().bind(task.messageProperty());

            // Обработчик отмены
            progressBarController.getBtnCancel().setOnAction(event1 -> {
                task.cancel();
                processor.cancel();
            });

            // Показываем окно прогресс-бара
            Stage progressStage = new Stage();
            progressStage.setScene(new Scene(progressBarLayout));
            progressBarController.setStage(progressStage);
            progressStage.setTitle("Загрузка...");
            progressStage.show();

            task.setOnSucceeded(workerStateEvent -> {
                progressStage.close();
                tableView.setItems(fileDataList);
                if (fileDataList.isEmpty()) {
                    showAlertINFO("Feeler Manager. Результат", "Дубликаты не найдены");
                }
            });

            task.setOnCancelled(workerStateEvent -> {
                progressStage.close();
                showAlertINFO("Feeler Manager. Отмена", "Процесс поиска дубликатов был отменён");
            });

            new Thread(task).start();
        } catch (IOException e) {
            e.printStackTrace();
            showAlertERROR("Feeler Manager. Ошибка", "Произошла ошибка при загрузке окна прогресс-бара.");
        }
    }

    /**
     * Функция для определения размера файла
     * @param sizeInBytes
     * @return
     */
    private String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 0) {
            return "Invalid size";
        }

        final long BYTE = 1;
        final long KILOBYTE = 1024 * BYTE;
        final long MEGABYTE = 1024 * KILOBYTE;
        final long GIGABYTE = 1024 * MEGABYTE;
        final long BIT = BYTE * 8;

        if (sizeInBytes >= GIGABYTE) {
            return String.format("%.2f Гб", (double) sizeInBytes / GIGABYTE);
        } else if (sizeInBytes >= MEGABYTE) {
            return String.format("%.2f Мб", (double) sizeInBytes / MEGABYTE);
        } else if (sizeInBytes >= KILOBYTE) {
            return String.format("%.2f Кб", (double) sizeInBytes / KILOBYTE);
        } else if (sizeInBytes >= BYTE) {
            return String.format("%d байт", sizeInBytes);
        } else {
            return String.format("%d бит", sizeInBytes * BIT);
        }
    }

    /**
     * Функция для запуска приложения в новом окне с сохраненными параметрами
     * @param event
     */
    @FXML
    void btnNewWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FindDuplicates.fxml"));
            Parent root = loader.load();

            FindDuplicatesController newWindowController = loader.getController();

            newWindowController.copyDataFrom(this);
            newWindowController.hideNewWindowButton();

            Stage newStage = new Stage();
            newStage.setTitle("Feeler Manager. Поиск дубликатов файлов");
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            
            newStage.show();

        } catch (IOException e) {
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

    /**
     * Сохранение информации для окрытия в новом окне
     * @param originalController
     */
    private void copyDataFrom(FindDuplicatesController originalController) {
        this.textWay.setText(originalController.textWay.getText());
        
        this.checkName.setSelected(originalController.checkName.isSelected());
        this.checkSize.setSelected(originalController.checkSize.isSelected());
        this.checkContent.setSelected(originalController.checkContent.isSelected());
        
        this.choiceDataType.setValue(originalController.choiceDataType.getValue());
        
        this.fileDataList.addAll(originalController.fileDataList);
        this.tableView.setItems(this.fileDataList);
    }

    /**
     * Выбранный пользователем тип данных
     * @param dataType
     * @return
     */
    private FileType getFileType(String dataType) {
        switch (dataType) {
            case "Медиа файлы":
                return new MediaFileType();
            case "Документы":
                return new DocumentFileType();
            case "Все поддерживаемые форматы":
                return new AnyFileType();
            default:
                return null;
        }

    }

    /**
     * Вывод сообщений об ошибке
     * @param title
     * @param content
     */
    private void showAlertERROR(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Вывод Информационного сообщения
     * @param title
     * @param content
     */
    private void showAlertINFO(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Открывает проводник с выбранной папкой и выделенным файлом
     * @param filePath путь к файлу
     */
    @SuppressWarnings("deprecation")
    private void openFolderWithFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                String command;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    // Открытие файла в проводнике Windows
                    command = "explorer /select," + file.getAbsolutePath();
                } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    // Открытие файла в Finder на macOS
                    command = "open -R " + file.getAbsolutePath();
                } else {
                    // Открытие файла в файловом менеджере Linux
                    command = "xdg-open " + file.getParent();
                }
                Runtime.getRuntime().exec(command);
            } else {
                showAlertERROR("Feeler Manager. Ошибка", "Файл не найден: " + filePath);
            }
        } catch (IOException e) {
            showAlertERROR("Feeler Manager. Ошибка", "Не удалось открыть проводник для файла: " + filePath);
            e.printStackTrace();
        }
    }

    

    /**
     * ДОРАБОТАТЬ МЕТОТ ИНИЦИАЛИЗАЦИИ
     * ЕСТЬ 1. Реализовать функцию для очистки таблицы при повторном запуске или запуске с другого метода
     * ЕСТЬ 2. Реализовать, чтобы данные обновлялись со всех трех файлов
     * ЕСТЬ !!! 3. Доработать функцию для работы с документами !!!
     * ЕСТЬ 4. Для реализации полноэкранного режима переделать в SceneBuilder главное окно в BorderPane 
     *    (Под вопросом, возможно и так все исправить). Иначе, добавть функцию "На весь экран"
     * 5. Добавить в таблицу кнопку выбора файлов (Как это сделано, в CorrectingMetadata.fxml) и кнопку удалить 
     * ЕСТЬ 6. Добавить функцию открытия файла в проводнике с указанием на элемент
     * ЕСТЬ 7. Добавить сообщение с выводом о том, что дубликаты не найдены
     * ЕСТЬ 8. Добавить прогресс бар. 
     *      8.1. Реализовать прогресс бар, показывающий проценты загрузки))
     * ЕСТЬ 9. Поработать над отображением размера файла
     * ЕСТЬ 10. В новом окне убрать кнопку "Открыть в новом окне" и убрать текст рядом с ней.
     *     В анализаторе сделать тоже самое
     */
    @FXML
    void initialize() {

        // Заполняем ChoiceBox и задаем значение по умолчанию
        choiceDataType.getItems().addAll("Медиа файлы", "Документы", "Все поддерживаемые форматы");
        choiceDataType.setValue("Медиа файлы");

        // Добавляем слушатели изменений состояния CheckBox
        checkContent.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkName.setSelected(false);
                checkSize.setSelected(false);
            }
        });

        checkName.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkContent.setSelected(false);
                checkSize.setSelected(false);
            }
        });

        checkSize.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                checkContent.setSelected(false);
                checkName.setSelected(false);
            }
        });

        // Работа с таблицей
        tableNumber.prefWidthProperty().bind(tableView.widthProperty().multiply(0.03));
        tableName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        tableImage.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        tableWay.prefWidthProperty().bind(tableView.widthProperty().multiply(0.47));
        tableSize.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        // Инициализация таблицы и привязка данных
        tableNumber.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
        tableName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableImage.setCellValueFactory(cellData -> cellData.getValue().imageViewProperty());
        tableWay.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
        tableSize.setCellValueFactory(cellData -> {
            Long size = cellData.getValue().getSize();
            String formattedSize = formatFileSize(size);
            return new SimpleStringProperty(formattedSize);
        });
        tableView.setItems(fileDataList);

        // Добавление Tooltip на указание пути к файлу
        tableWay.setCellFactory(column -> new TextFieldTableCell<FileInfo, String>() {
        private final Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });

        // Добавление Tooltip на указание имени
        tableName.setCellFactory(column -> new TextFieldTableCell<FileInfo, String>() {
            private final Tooltip tooltip = new Tooltip();
    
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item);
                        tooltip.setText(item);
                        setTooltip(tooltip);
                    }
                }
            });

        // Обработчик двойного клика
        tableView.setRowFactory(tv -> {
            TableRow<FileInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    FileInfo rowData = row.getItem();
                    openFolderWithFile(rowData.getPath());
                }
            });
            return row;
        });
    }
    
}
