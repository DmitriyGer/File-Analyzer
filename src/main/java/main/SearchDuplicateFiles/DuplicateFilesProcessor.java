package main.SearchDuplicateFiles;

import java.util.function.BiConsumer;

import javafx.collections.ObservableList;

public class DuplicateFilesProcessor {

    private FileType fileType;
    private boolean checkName;
    private boolean checkSize;
    private boolean checkContent;
    private ObservableList<FileInfo> fileDataList;
    private BiConsumer<Double, String> progressUpdateHandler;
    private volatile boolean cancelled = false;

    public DuplicateFilesProcessor(FileType fileType, boolean checkName, boolean checkSize, boolean checkContent) {
        this.fileType = fileType;
        this.checkName = checkName;
        this.checkSize = checkSize;
        this.checkContent = checkContent;
    }

    public void setFileDataList(ObservableList<FileInfo> fileDataList) {
        this.fileDataList = fileDataList;
    }
    
    public void setOnProgressUpdate(BiConsumer<Double, String> progressUpdateHandler) {
        this.progressUpdateHandler = progressUpdateHandler;
    }
    
    public void cancel() {
        this.cancelled = true;
    }

    private void updateProgress(double progress, String message) {
        if (progressUpdateHandler != null) {
            progressUpdateHandler.accept(progress, message);
        }
    }

    public void processFiles(String directoryPath) {
        if (cancelled) return;
        updateProgress(0.0, "Начало обработки файлов...");
        if (cancelled) return;
        if (checkName) {
            updateProgress(0.3, "Поиск по имени...");
            DuplicateFilesName duplicateFilesName = new DuplicateFilesName(fileType, fileDataList);
            duplicateFilesName.processFiles(directoryPath);
        }
        if (cancelled) return;
        if (checkSize) {
            updateProgress(0.6, "Поиск по размеру...");
            DuplicateFilesSize duplicateFilesSize = new DuplicateFilesSize(fileType, fileDataList);
            duplicateFilesSize.processFiles(directoryPath);
        }
        if (cancelled) return;
        if (checkContent) {
            updateProgress(0.9, "Поиск по содержимому...");
            DuplicateFilesContent duplicateFilesContent = new DuplicateFilesContent(fileType, fileDataList);
            duplicateFilesContent.processFiles(directoryPath);
        }
        if (cancelled) return;
        updateProgress(1.0, "Обработка завершена.");
    }
}