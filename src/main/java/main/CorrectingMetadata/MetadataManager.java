package main.CorrectingMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataManager {
    
    // // Метод для чтения метаданных изображений
    // public static Map<String, String> readImageMetadata(String imagePath) throws IOException {
    //     File file = new File(imagePath);
    //     ImageMetadata metadata = Imaging.getMetadata(file);

    //     Map<String, String> metadataMap = new HashMap<>();
    //     if (metadata instanceof JpegImageMetadata) {
    //         List<ImageMetadataItem> items = metadata.getItems();
    //         for (ImageMetadataItem item : items) {
    //             metadataMap.put(item.getKeyword(), item.getText());
    //         }
    //     }
    //     return metadataMap;
    // }

    // // Метод для записи метаданных изображений
    // public static void writeImageMetadata(String imagePath, Map<String, String> newMetadata) throws IOException {
    //     File file = new File(imagePath);
    //     JpegImageMetadata metadata = (JpegImageMetadata) Imaging.getMetadata(file);

    //     if (metadata != null) {
    //         try (var os = new FileOutputStream(imagePath)) {
    //             new ExifRewriter().updateExifMetadataLossless(file, os, newMetadata);
    //         }
    //     }
    // }

    // // Метод для чтения метаданных видео
    // public static Map<String, String> readVideoMetadata(String videoPath) throws IOException {
    //     FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
    //     FFmpegProbeResult probeResult = ffprobe.probe(videoPath);

    //     Map<String, String> metadataMap = new HashMap<>();
    //     probeResult.getFormat().tags.forEach(metadataMap::put);
    //     return metadataMap;
    // }

    // // Метод для записи метаданных видео
    // public static void writeVideoMetadata(String videoPath, Map<String, String> newMetadata) throws IOException {
    //     FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
    //     FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");

    //     StringBuilder metadataString = new StringBuilder();
    //     newMetadata.forEach((key, value) -> metadataString.append(String.format("-metadata %s=\"%s\" ", key, value)));

    //     FFmpegBuilder builder = new FFmpegBuilder()
    //             .setInput(videoPath)
    //             .addOutput("output_video.mp4")
    //             .setFormat("mp4")
    //             .addExtraArgs(metadataString.toString().split(" "))
    //             .done();

    //     ffmpeg.run(builder);
    // }

    // public static void main(String[] args) {
    //     try {
    //         // Пример использования для изображений
    //         String imagePath = "example.jpg";
    //         Map<String, String> imageMetadata = readImageMetadata(imagePath);
    //         System.out.println("Image Metadata: " + imageMetadata);

    //         Map<String, String> newImageMetadata = new HashMap<>();
    //         newImageMetadata.put("Author", "John Doe");
    //         writeImageMetadata(imagePath, newImageMetadata);

    //         // Пример использования для видео
    //         String videoPath = "example.mp4";
    //         Map<String, String> videoMetadata = readVideoMetadata(videoPath);
    //         System.out.println("Video Metadata: " + videoMetadata);

    //         Map<String, String> newVideoMetadata = new HashMap<>();
    //         newVideoMetadata.put("title", "Example Title");
    //         newVideoMetadata.put("comment", "This is an example comment.");
    //         writeVideoMetadata(videoPath, newVideoMetadata);

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

}
