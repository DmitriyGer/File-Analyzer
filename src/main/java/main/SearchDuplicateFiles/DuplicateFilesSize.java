package main.SearchDuplicateFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Исполняющая функция поиска дубликатов файлов
 */
public class DuplicateFilesSize {

    private final FileType fileType;
    private final List<String> systemDirectories;

    public DuplicateFilesSize(FileType fileType) {
        this.fileType = fileType;
        this.systemDirectories = getSystemDirectories();
    }

    public void processFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("Указанный путь не является директорией.");
            return;
        }

        if (isSystemDirectory(directory)) {
            System.out.println("Сканирование системных директорий запрещено.");
            return;
        }

        Map<String, List<FileInfo>> fileHashMap = new HashMap<>();
        try {
            findDuplicatesRecursive(directory, fileHashMap);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Произошла ошибка при обработке файлов: " + e.getMessage());
        }

        printDuplicates(fileHashMap);
    }

    private void findDuplicatesRecursive(File directory, Map<String, List<FileInfo>> fileHashMap) throws IOException, NoSuchAlgorithmException {
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

                FileInfo fileInfo = new FileInfo(file.getName(), file.getAbsolutePath(), file.length());
                fileHashMap.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(fileInfo);
            }
        }
    }

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

    private boolean isSystemDirectory(File directory) {
        for (String systemPath : systemDirectories) {
            if (systemPath != null && directory.getAbsolutePath().startsWith(systemPath)) {
                return true;
            }
        }
        return false;
    }

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
}