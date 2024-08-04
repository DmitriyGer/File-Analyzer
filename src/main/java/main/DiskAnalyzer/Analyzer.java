package main.DiskAnalyzer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Исполняющуя функция для анализа диска
 */
public class Analyzer {

    private HashMap<String, Long> sizes;

    public Map<String, Long> calculateDirectorySize(Path path) {
        try {
            sizes = new HashMap<>();
            Files.walkFileTree(
                path,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            long size = Files.size(file);
                            updateDirSize(file, size);
                        } catch (IOException e) {
                            System.err.format("Unable to access file %s: %s%n", file, e.getMessage());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        System.err.format("Unable to access file %s: %s%n", file, exc.getMessage());
                        return FileVisitResult.CONTINUE; // Игнорировать ошибки и продолжить обход
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE; // Сначала посетить директорию, прежде чем посещать файлы внутри нее
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc != null) {
                            System.err.format("Unable to access directory %s: %s%n", dir, exc.getMessage());
                        }
                        return FileVisitResult.CONTINUE; // Игнорировать ошибки после обхода директории
                    }
                }
            );
            return sizes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateDirSize(Path path, Long size) {
        String key = path.toString();
        sizes.put(key, size + sizes.getOrDefault(key, 0L));

        Path parent = path.getParent();

        if (parent != null) {
            updateDirSize(parent, size);
        }
    }
}
