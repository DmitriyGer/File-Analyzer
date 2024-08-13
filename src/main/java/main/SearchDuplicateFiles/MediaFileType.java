package main.SearchDuplicateFiles;

/**
 * Поддержка типов данных "Медиа"
 */
public class MediaFileType implements FileType{

    private static final String[] EXTENSIONS = {"png", "PNG", "jpeg", "jpg", "JPG", "gif", "mp4", "mp3", "waw", "avi"};

    @Override
    public boolean isMatch(String filename) {
        for (String extension : EXTENSIONS) {
            if (filename.endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }
}

