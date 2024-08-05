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
 * Логика агализатора
 */
public class AnalyzerVideo {

    private HashMap<String, Long> sizes;

    public Map<String, Long> calculateDirectorySize(Path path) {
        try {
            sizes = new HashMap<>();
            Files.walkFileTree(
                path,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        long size = Files.size(file);
                        updateDirSize(file, size);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.SKIP_SUBTREE; // Если возникает ошибка в директории мы пропускаем ее полностью
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