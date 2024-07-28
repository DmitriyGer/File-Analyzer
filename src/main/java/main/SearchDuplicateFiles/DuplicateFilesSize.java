package main.SearchDuplicateFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.FindDuplicatesController;

/**
 * Исполняющая функция поиска дубликатов файлов по размеру
 */
public class DuplicateFilesSize extends FindDuplicatesController {

    public static ObservableList<FileInfo> fileDataList = FXCollections.observableArrayList();

    private final FileType fileType;
    private final List<String> systemDirectories;

    public DuplicateFilesSize(FileType fileType) {
        this.fileType = fileType;
        this.systemDirectories = getSystemDirectories();
    }

    /**
     * Проверки и исключения
     */
    public void processFiles(String directoryPath) {

        // fileDataList.clear();

        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("Указанный путь не является директорией.");
            return;
        }

        if (isSystemDirectory(directory)) {
            System.out.println("Сканирование системных директорий запрещено.");
            return;
        }

        try {

            Map<Long, List<File>> fileMap = new HashMap<>();
            findDuplicatesSize(directory, fileMap);

            int index = 1;
            for (List<File> files : fileMap.values()) {
                if (files.size() > 1) {
                    for (File file : files) {
                        ImageView imageView = createImageView(file);
                        imageView.setOnMouseClicked(event -> openFile(file));
                        fileDataList.add(new FileInfo(index++, file.getName(), imageView, file.getAbsolutePath(), file.length()));
                        System.out.println("Вывод через поиск по размеру файла -- есть");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Произошла ошибка при обработке файлов: " + e.getMessage());
        }

    }

    /**
     * Функция поиска дубликатов файлов по размеру
     * @param directory
     * @param fileSizeMap
     * @throws IOException
     */
    private void findDuplicatesSize(File directory, Map<Long, List<File>> fileSizeMap) throws IOException {
        File[] files = directory.listFiles();

        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                if (isSystemDirectory(file)) {
                    System.out.println("Проигнорирована системная директория: " + file.getAbsolutePath());
                } else {
                    findDuplicatesSize(file, fileSizeMap);
                }
            } else if (fileType.isMatch(file.getName())) {

                long fileSize = file.length();

                fileSizeMap.computeIfAbsent(fileSize, k -> new ArrayList<>()).add(file);

            }
        }
    }

    /**
     * Список системных директорий
     * @return
     */
    private List<String> getSystemDirectories() {

        String userName = System.getProperty("user.name");
        String appDataPath = "C:\\Users\\" + userName + "\\AppData";

        List<String> systemDirs = new ArrayList<>();
        systemDirs.add(System.getenv("SystemRoot"));
        systemDirs.add(System.getenv("PerfLogs"));
        systemDirs.add(System.getenv("Recovery"));
        systemDirs.add(System.getenv("ProgramFiles"));
        systemDirs.add(System.getenv("ProgramFiles(x86)"));
        systemDirs.add(System.getenv("Windows"));
        systemDirs.add(System.getenv("ProgramData"));
        systemDirs.add(appDataPath);
        return systemDirs;
    }

    /**
     * Проверка на системные директории
     * @param directory
     * @return
     */
    private boolean isSystemDirectory(File directory) {
        for (String systemPath : systemDirectories) {
            if (systemPath != null && directory.getAbsolutePath().startsWith(systemPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Создание миниатюры для вывода изображения
     * @param file
     * @return
     */
    private ImageView createImageView(File file) {
        ImageView imageView;
        if (isImageFile(file)) {
            Image image = new Image(file.toURI().toString(), 50, 50, true, true);
            imageView = new ImageView(image);
        } else {
            Image image = new Image(getClass().getResourceAsStream("/Images/1.png"), 50, 50, true, true);
            imageView = new ImageView(image);
        }
        return imageView;
    }

    /**
     * Проверка на тип данных
     * @param file
     * @return
     */
    private boolean isImageFile(File file) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "bmp", "gif"};
        String fileName = file.getName().toLowerCase();
        return Arrays.stream(imageExtensions).anyMatch(fileName::endsWith);
    }

    private void openFile(File file) {
        try {
            new ProcessBuilder("cmd", "/c", file.getAbsolutePath()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}