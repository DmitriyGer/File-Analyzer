package main.SearchDuplicateFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.FindDuplicatesController;

/**
 * Исполняющая функция поиска дубликатов файлов
 */
public class DuplicateFilesContent extends FindDuplicatesController {

    public static ObservableList<FileInfo> fileDataList = FXCollections.observableArrayList();

    private final FileType fileType;
    private final List<String> systemDirectories;

    public DuplicateFilesContent(FileType fileType) {
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

            Map<String, List<File>> fileMap = new HashMap<>();
            findDuplicatesRecursive(directory, fileMap);

            
            int index = 1;
            for (List<File> files : fileMap.values()) {
                if (files.size() > 1) {
                    for (File file : files) {
                        ImageView imageView = createImageView(file);
                        imageView.setOnMouseClicked(event -> openFile(file));
                        fileDataList.add(new FileInfo(index++, file.getName(), imageView, file.getAbsolutePath(), file.length()));
                        System.out.println("Вывод через хэш есть");
                        
                    }
                }
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Произошла ошибка при обработке файлов: " + e.getMessage());
        }

    }

    /**
     * Функция поиска дубликатов файлов
     * @param directory
     * @param fileHashMap
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private void findDuplicatesRecursive(File directory, Map<String, List<File>> fileHashMap) throws IOException, NoSuchAlgorithmException {
        File[] files = directory.listFiles();

        if (files == null) return;

        MessageDigest digest = MessageDigest.getInstance("MD5");

        for (File file : files) {
            if (file.isDirectory()) {
                if (isSystemDirectory(file)) {
                    System.out.println("Проигнорирована системная директория: " + file.getAbsolutePath());
                } else {
                    findDuplicatesRecursive(file, fileHashMap);
                }
            } else if (fileType.isMatch(file.getName())) {

                String fileHash = getFileHash(file, digest);

                fileHashMap.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(file);

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
     * Вывод результатов сканирования в консоль
     * @param fileHashMap
     */
    /*
    private void printDuplicates(Map<String, List<FileInfo>> fileHashMap) {
        for (Map.Entry<String, List<FileInfo>> entry : fileHashMap.entrySet()) {
            List<FileInfo> fileList = entry.getValue();
            if (fileList.size() > 1) {
                FileInfo originalFile = fileList.get(0);
                System.out.println("Найден дубликат:");
                System.out.println("Оригинальный файл: " + originalFile.getFileName() + ", Путь: " + originalFile.getFilePath() + ", Размер: " + originalFile.getFileSize() + " байт");
                for (int i = 1; i < fileList.size(); i++) {
                    FileInfo duplicateFile = fileList.get(i);
                    System.out.println("Дубликат " + i + ": " + duplicateFile.getFileName() + ", Путь: " + duplicateFile.getFilePath() + ", Размер: " + duplicateFile.getFileSize() + " байт");
                }
                System.out.println(); // Интервал между выводами
            }
        }
    }
    */

    /**
     * Получить файловый хэш
     * @param file
     * @param digest
     * @return
     * @throws IOException
     */
    private String getFileHash(File file, MessageDigest digest) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        fis.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
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