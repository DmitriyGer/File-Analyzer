package main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class InstructionController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView imageInsAnalyzerDisk;

    @FXML
    private ImageView imageInsFindDublicatFiles;

    @FXML
    private ImageView imageInsSortFiles;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Загрузка необходимой страницы
     * @param fxmlFile
     */
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent content = loader.load();
            
            Object controller = loader.getController();
            if(controller instanceof InstructionFDController) {
                ((InstructionFDController) controller).setMainController(mainController);
            } else if (controller instanceof InstructionSMController) {
                ((InstructionSMController) controller).setMainController(mainController);
            } else if (controller instanceof InstructionDAController) {
                ((InstructionDAController) controller).setMainController(mainController);
            }

            mainController.setContent(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void initialize() {
        
        imageInsFindDublicatFiles.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> loadContent("InstructionFD.fxml"));
        imageInsSortFiles.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> loadContent("InstructionSM.fxml"));
        imageInsAnalyzerDisk.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> loadContent("InstructionDA.fxml"));
        
    }

}
