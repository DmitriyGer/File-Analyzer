package main.DiskAnalyzer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Исполняющая функция анализатора дискового пространства
 */
public class Analyzer {

    private HashMap<String, Long> sizes;
    private ProgressCallback progressCallback;
    private long totalSize = 0;
    private AtomicLong processedSize = new AtomicLong(0);

    public interface ProgressCallback {
        void updateProgress(long processedSize, long totalSize);
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public Map<String, Long> calculateDirectorySize(Path path) {
        try {
            sizes = new HashMap<>();
            totalSize = calculateTotalSize(path);

            Files.walkFileTree(
                path,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            long size = Files.size(file);
                            updateDirSize(file, size);
                            long currentProcessedSize = processedSize.addAndGet(size);
                            if (progressCallback != null) {
                                progressCallback.updateProgress(currentProcessedSize, totalSize);
                            }
                        } catch (IOException e) {
                            System.err.format("Unable to access file %s: %s%n", file, e.getMessage());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        System.err.format("Unable to access file %s: %s%n", file, exc.getMessage());
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc != null) {
                            System.err.format("Unable to access directory %s: %s%n", dir, exc.getMessage());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                }
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sizes;
    }

    private long calculateTotalSize(Path path) {
        final AtomicLong size = new AtomicLong(0);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size.get();
    }

    private void updateDirSize(Path file, long size) {
        Path dir = file.getParent();
        sizes.put(dir.toString(), sizes.getOrDefault(dir.toString(), 0L) + size);
        sizes.put(file.toString(), size); // Размер конкретного файла
    }
}