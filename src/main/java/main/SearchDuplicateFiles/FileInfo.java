package main.SearchDuplicateFiles;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileInfo {
    private final StringProperty fileName;
    private final StringProperty filePath;
    private final SimpleLongProperty fileSize;

    public FileInfo(String fileName, String filePath, long fileSize) {
        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.fileSize = new SimpleLongProperty(fileSize);
    }

    public String getFileName() {
        return fileName.get();
    }

    public String getFilePath() {
        return filePath.get();
    }

    public long getFileSize() {
        return fileSize.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize.set(fileSize);
    }

}