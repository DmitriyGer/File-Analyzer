package main.DiskAnalyzer;

import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Исполняющая функция анализатора дискового пространства для исследования в проводнике
 */
public class AnalyzerConductor {
    private static final Map<File, Long> sizeCache = new ConcurrentHashMap<>();

    public static TreeItem<StackPane> createTreeItem(File file, long parentTotalSpace, TreeView<StackPane> treeView) {
        long usedSpace = getDirectorySize(file);
        double spacePercentage = (double) usedSpace / parentTotalSpace * 100;
        StackPane treeItemContent = createTreeItemContent(file, usedSpace, spacePercentage, treeView);

        TreeItem<StackPane> treeItem = new TreeItem<>(treeItemContent);

        if (file.isDirectory()) {
            addChildrenToTreeItem(file, treeItem, parentTotalSpace, treeView);
        }
        return treeItem;
    }

    public static void addChildrenToTreeItem(File directory, TreeItem<StackPane> parentItem, long parentTotalSpace, TreeView<StackPane> treeView) {
        List<File> filesList = new ArrayList<>();
        List<File> skippedFiles = new ArrayList<>();

        try {
            Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    File file = dir.toFile();

                    if (!file.equals(directory)) {
                        filesList.add(file);
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    filesList.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    if (exc instanceof AccessDeniedException || exc instanceof FileSystemException) {
                        skippedFiles.add(file.toFile());
                        return FileVisitResult.CONTINUE;
                    }

                    return FileVisitResult.TERMINATE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        filesList.sort((f1, f2) -> Long.compare(getDirectorySize(f2), getDirectorySize(f1)));

        for (File file : filesList) {
            TreeItem<StackPane> newItem = createTreeItem(file, parentTotalSpace, treeView);
            parentItem.getChildren().add(newItem);
        }

        if (!skippedFiles.isEmpty()) {
            System.out.println("Пропущенные файлы из-за отсутствия доступа:");
            for (File skippedFile : skippedFiles) {
                System.out.println(skippedFile.getAbsolutePath());
            }
        }
    }

    public static StackPane createTreeItemContent(File file, long usedSpace, double spacePercentage, TreeView<StackPane> treeView) {
        DecimalFormat df = new DecimalFormat("#.##");
        double sizeInGB = (double) usedSpace / (1024 * 1024 * 1024);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Text fileNameText = new Text(file.getName() + " ");
        Text sizeText = new Text("Размер: " + df.format(sizeInGB) + " ГБ ");
        Text dateText = new Text("Создано: " + getCreationDate(file) + " ");
        Text percentageText = new Text("(" + df.format(spacePercentage) + "%) ");

        TextFlow textFlow = new TextFlow(fileNameText);

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox additionalInfoBox = new HBox(dateText, sizeText, percentageText);
        additionalInfoBox.setSpacing(10);
        additionalInfoBox.setAlignment(Pos.CENTER_RIGHT);

        hbox.getChildren().addAll(textFlow, spacer, additionalInfoBox);

        Rectangle background = new Rectangle();
        background.setFill(Color.GREEN);
        background.setOpacity(0.5);
        background.widthProperty().bind(treeView.widthProperty().subtract(50).multiply(spacePercentage / 100));
        background.setHeight(20);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(background, hbox);
        StackPane.setAlignment(background, Pos.CENTER_LEFT);
        StackPane.setAlignment(hbox, Pos.CENTER_LEFT);
        stackPane.setId(file.getAbsolutePath());

        return stackPane;
    }

    public static long getDirectorySize(File file) {
        if (sizeCache.containsKey(file)) {
            return sizeCache.get(file);
        }

        long size = 0;
        if (file.isFile()) {
            size = file.length();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    size += getDirectorySize(f);
                }
            }
        }
        sizeCache.put(file, size);
        return size;
    }

    public static String getCreationDate(File file) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            return sdf.format(attrs.creationTime().toMillis());
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public static void updateTreeItems(File directory, TreeItem<StackPane> parentItem, long newTotalSpace, TreeView<StackPane> treeView) {
        parentItem.getChildren().clear();
        if (directory.isDirectory()) {
            addChildrenToTreeItem(directory, parentItem, newTotalSpace, treeView);
        }
    }
}