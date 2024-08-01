package main.CorrectingMetadata;

public class GetFileExtension {

    protected static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }
    
    protected static boolean isImage(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return true;
            default:
                return false;
        }
    }
    
    protected static boolean isVideo(String extension) {
        switch (extension) {
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
            case "flv":
                return true;
            default:
                return false;
        }
    }
    
}