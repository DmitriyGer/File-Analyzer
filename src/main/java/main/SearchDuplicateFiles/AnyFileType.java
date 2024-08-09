package main.SearchDuplicateFiles;

/**
 * Прогонка по всем поддерживаемым файлам
 */
public class AnyFileType implements FileType {

    private static final String[] EXTENSIONS = {"doc", "docx", "xls", "xlsx", "pdf", "ppt", "pptx", "txt", "png", "jpeg", "JPG", "gif", "mp4", "mp3", "waw", "avi"};

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
