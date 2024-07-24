package main.SearchDuplicateFiles;

public class FileInfo {
    private final String fileName;
    private final String filePath;
    private final long fileSize;

    public FileInfo(String fileName, String filePath, long fileSize) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

}