package main.SearchDuplicateFiles;

public class DuplicateFilesProcessor {
    
    private FileType fileType;
    private boolean checkName;
    private boolean checkSize;
    private boolean checkContent;

    public DuplicateFilesProcessor(FileType fileType, boolean checkName, boolean checkSize, boolean checkContent) {
        this.fileType = fileType;
        this.checkName = checkName;
        this.checkSize = checkSize;
        this.checkContent = checkContent;
    }

    public void processFiles(String directoryPath) {

        if (checkName) {
            DuplicateFilesName duplicateFilesName = new DuplicateFilesName(fileType);
            duplicateFilesName.processFiles(directoryPath);
        }

        if (checkSize) {
            DuplicateFilesSize duplicateFilesSize = new DuplicateFilesSize(fileType);
            duplicateFilesSize.processFiles(directoryPath);
        }

        if (checkContent) {
            DuplicateFilesContent duplicateFilesContent = new DuplicateFilesContent(fileType);
            duplicateFilesContent.processFiles(directoryPath);
        }
    }
}
