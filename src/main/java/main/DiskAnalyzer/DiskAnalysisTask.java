package main.DiskAnalyzer;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DiskAnalysisTask extends Task<Void> {
    private final File directory;
    private final long rootTotalSpace;
    private final TreeView<StackPane> directoryTreeView;
    private final ExecutorService executorService;
    private static final int BUFFER_SIZE = 20;

    public DiskAnalysisTask(File directory, long rootTotalSpace, TreeView<StackPane> directoryTreeView) {
        this.directory = directory;
        this.rootTotalSpace = rootTotalSpace;
        this.directoryTreeView = directoryTreeView;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected Void call() throws Exception {
        long totalSize = AnalyzerConductor.getDirectorySize(directory);
        File[] files = directory.listFiles();

        if (files != null) {
            int totalFiles = files.length;
            int filesProcessed = 0;
            List<Future<FileTreeItem>> futures = new ArrayList<>();
            List<TreeItem<StackPane>> buffer = new ArrayList<>(BUFFER_SIZE);

            TreeItem<StackPane> rootItem = AnalyzerConductor.createTreeItem(directory, rootTotalSpace, directoryTreeView);

            for (File file : files) {
                if (isCancelled()) {
                    break;
                }
                futures.add(executorService.submit(() -> new FileTreeItem(file, rootTotalSpace, directoryTreeView)));
            }

            for (Future<FileTreeItem> future : futures) {
                if (isCancelled()) {
                    break;
                }

                filesProcessed++;
                FileTreeItem fileTreeItem = future.get();
                buffer.add(fileTreeItem.treeItem);

                double progress = (double) filesProcessed / totalFiles;
                updateProgress(progress, 1.0);
                updateMessage((int) (progress * 100) + "%");

                if (buffer.size() >= BUFFER_SIZE || filesProcessed == totalFiles) {
                    final List<TreeItem<StackPane>> itemsToAdd = new ArrayList<>(buffer);
                    buffer.clear();
                    Platform.runLater(() -> rootItem.getChildren().addAll(itemsToAdd));
                }
            }

            if (!buffer.isEmpty()) {
                final List<TreeItem<StackPane>> itemsToAdd = new ArrayList<>(buffer);
                buffer.clear();
                Platform.runLater(() -> rootItem.getChildren().addAll(itemsToAdd));
            }

            Platform.runLater(() -> directoryTreeView.setRoot(rootItem));
        }

        executorService.shutdown();
        return null;
    }

    private static class FileTreeItem {
        final File file;
        final TreeItem<StackPane> treeItem;

        FileTreeItem(File file, long rootTotalSpace, TreeView<StackPane> directoryTreeView) {
            this.file = file;
            this.treeItem = AnalyzerConductor.createTreeItem(file, rootTotalSpace, directoryTreeView);
        }
    }
}