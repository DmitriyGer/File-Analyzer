package main.SortMediaFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IsMediaFile {

    private static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "bmp", "gif"};
    private static final String[] VIDEO_EXTENSIONS = {"mp4", "avi", "mov", "wmv", "flv"};

    public static boolean isMediaFile(File file) {
        String fileName = file.getName().toLowerCase();
        for (String ext : IMAGE_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        for (String ext : VIDEO_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static List<File> getMediaFiles(File dir) {
        List<File> mediaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isMediaFile(file)) {
                    mediaFiles.add(file);
                }
            }
        }
        return mediaFiles;
    }
}

// import java.nio.file.Path;

// /**
//  * Проверяет, является ли файл медиафайлом (по расширению)
//  * @param file
//  * @return
//  */ 
// public class IsMediaFile {
//     public boolean isMediaFile(Path file) {
//         String fileName = file.toString().toLowerCase();
//         return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") ||
//                fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov");
//     }
// }