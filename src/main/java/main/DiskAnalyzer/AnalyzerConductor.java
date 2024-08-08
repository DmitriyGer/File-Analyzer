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
import java.nio.file.Files;
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
            File[] files = file.listFiles();
            if (files != null) {
                List<File> fileList = new ArrayList<>(Arrays.asList(files));
                fileList.sort(Comparator.comparingLong(AnalyzerConductor::getDirectorySize).reversed());

                for (File f : fileList) {
                    treeItem.getChildren().add(createTreeItem(f, parentTotalSpace, treeView));
                }
            }
        }
        return treeItem;
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
            File[] files = directory.listFiles();
            if (files != null) {
                List<File> fileList = new ArrayList<>(Arrays.asList(files));
                fileList.sort(Comparator.comparingLong(AnalyzerConductor::getDirectorySize).reversed());

                for (File f : fileList) {
                    TreeItem<StackPane> newItem = createTreeItem(f, newTotalSpace, treeView);
                    parentItem.getChildren().add(newItem);
                }
            }
        }
    }
}