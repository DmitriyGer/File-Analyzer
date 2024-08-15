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

            List<Future<TreeItem<StackPane>>> futures = new ArrayList<>();
            TreeItem<StackPane> rootItem = AnalyzerConductor.createTreeItem(directory, rootTotalSpace, directoryTreeView);

            /**
             * При добавлении данного кода появляются проценты в прогресс баре и информация при выводе
             * начинает дублироваться
             */
            // for (File file : files) {
            //    futures.add(executorService.submit(() -> AnalyzerConductor.createTreeItem(file, rootTotalSpace, directoryTreeView)));
            // }

            for (Future<TreeItem<StackPane>> future : futures) {
                if (isCancelled()) {
                    break;
                }

                filesProcessed++;
                TreeItem<StackPane> fileTreeItem = future.get();

                Platform.runLater(() -> rootItem.getChildren().add(fileTreeItem));

                double progress = (double) filesProcessed / totalFiles;
                updateProgress(progress, 1.0);
                updateMessage((int) (progress * 100) + "%");
            }

            Platform.runLater(() -> directoryTreeView.setRoot(rootItem));
        }

        return null;
    }
}