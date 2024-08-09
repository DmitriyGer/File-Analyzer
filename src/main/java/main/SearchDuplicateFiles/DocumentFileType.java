package main.SearchDuplicateFiles;

/**
 * Поддержка типов данных "Документ"
 */
public class DocumentFileType implements FileType {

    private static final String[] EXTENSIONS = {"doc", "docx", "xls", "xlsx", "pdf", "ppt", "pptx", "txt"};

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
