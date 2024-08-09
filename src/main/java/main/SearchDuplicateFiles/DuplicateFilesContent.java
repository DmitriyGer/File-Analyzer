package main.SearchDuplicateFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public DuplicateFilesContent(FileType fileType, ObservableList<FileInfo> fileDataList) {
        this.fileType = fileType;
        this.systemDirectories = getSystemDirectories();
        this.fileDataList = fileDataList;
    }

    /**
     * Проверка и исключение
     * @param directoryPath
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
                        fileDataList.add(new FileInfo(index++, file.getName(), imageView, file.getAbsolutePath(), file.length()));
                        // System.out.println("Вывод через хэш есть");
                        
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
        try {
            String fileExtension = getFileExtension(file);
            if (isImageFileType(fileExtension)) {
                Image image = new Image(file.toURI().toString(), 75, 75, true, true);
                return new ImageView(image);
            } else {
                // Укажите путь к изображению по умолчанию для не изображений
                Image defaultImage = getImage("Images/1.png");
                return new ImageView(defaultImage);
            }
        } catch (Exception e) {
            // Укажите путь к изображению по умолчанию на случай исключения
            Image defaultImage = getImage("Images/1.png");
            return new ImageView(defaultImage);
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * Проверка на тип данных
     * @param file
     * @return
     */
    private boolean isImageFileType(String fileExtension) {
        // Сравните расширение файла с известными типами изображений
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp"};
        for (String extension : imageExtensions) {
            if (extension.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    private Image getImage(String resourcePath) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)), 75, 75, true, true);
    } 
}