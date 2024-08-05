package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

/**
 * ProgressBar
 * Показывает прогресс работы алгоритма
 */
public class ProgressDialogController {
    @FXML
    private Label labelProgress;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button btnCancel;

    private Stage stage;

    private volatile boolean cancelled = false;

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> {
            cancelled = true;
            stage.close(); 
        });
    }

    public Label getLabelProgress() {
        return labelProgress;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    
    public void cancelAnalysis() {
        cancelled = true;
    }
}