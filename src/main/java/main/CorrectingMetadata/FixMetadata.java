package main.CorrectingMetadata;

import java.io.File;

public class FixMetadata {

    public static void main(String[] args) {
        
        String directoryPath = "your/directory/path"; // укажите путь к вашей директории

        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Указанный путь не является директорией или не существует.");
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            System.out.println("Не удалось получить список файлов в директории.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                String fileExtension = GetFileExtension.getFileExtension(fileName);

                if (GetFileExtension.isImage(fileExtension)) {
                    try {
                        // CorrectMeta.fixMetaImage(file.getPath()); // Условие
                    } catch (Exception e) {
                        System.out.println("Ошибка при обработке изображения: " + fileName);
                        e.printStackTrace();
                    }
                } else if (GetFileExtension.isVideo(fileExtension)) {
                    try {
                        // CorrectMeta.fixMetaVideo(file.getPath()); // Условие
                    } catch (Exception e) {
                        System.out.println("Ошибка при обработке видео: " + fileName);
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    
}