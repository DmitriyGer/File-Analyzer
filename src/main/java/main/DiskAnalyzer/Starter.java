// package main.DiskAnalyzer;

// import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;
// import main.DiskAnalyzerController;

// public class Starter extends Application {

//     public static void main(String[] args) {
//         launch(args);
//     }

//     @Override
//     public void start(Stage primaryStage) throws Exception {
//         FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/DiskAnalyzer/main.fxml"));
//         Parent root = loader.load();
        
//         DiskAnalyzerController controller = loader.getController();
//         controller.setStage(primaryStage);

//         primaryStage.setTitle("Disk Analyzer");
//         primaryStage.setScene(new Scene(root, 900, 600));
//         primaryStage.show();
//     }
// }