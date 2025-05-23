package main.SearchDuplicateFiles;

import javafx.beans.property.*;
import javafx.scene.image.ImageView;

public class FileInfo {

    private final IntegerProperty number;
    private final StringProperty name;
    private final ObjectProperty<ImageView> imageView;
    private final StringProperty path;
    private final LongProperty size;

    public FileInfo(int number, String name, ImageView imageView, String path, long size) {
        this.number = new SimpleIntegerProperty(number);
        this.name = new SimpleStringProperty(name);
        this.imageView = new SimpleObjectProperty<>(imageView);
        this.path = new SimpleStringProperty(path);
        this.size = new SimpleLongProperty(size);
    }

    public int getNumber() {
        return number.get();
    }

    public IntegerProperty numberProperty() {
        return number;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ImageView getImageView() {
        return imageView.get();
    }

    public ObjectProperty<ImageView> imageViewProperty() {
        return imageView;
    }

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public long getSize() {
        return size.get();
    }

    public LongProperty sizeProperty() {
        return size;
    }
}