package main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.MySQLConnection.DatabaseHandler;
import main.MySQLConnection.User;
import main.MySQLConnection.Animation.Shake;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class AuthorizationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAuthSigIn;

    @FXML
    private Button btnLoginSignUp;

    @FXML
    private TextField fieldLogin;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private Label labelError;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void loginUser(String loginText, String loginPassword) {

        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setUserName(loginText);
        user.setPassword(loginPassword);
        ResultSet result =  dbHandler.getUser(user);

        int counter = 0;

        try {
            while (result.next()) {
                counter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (counter >= 1) {
            showSuccessAlert("Авторизация прошла успешно!");
            stage.close();
        } else {
            Shake userLoginAnim = new Shake(fieldLogin);
            Shake userPassAnium = new Shake(fieldPassword);
            userLoginAnim.playAnim();
            userPassAnium.playAnim();
            labelError.setVisible(true);
        }
    }

    /**
     * Вывод сообщений об ошибке
     * @param title
     * @param content
     */
    private void showAlertERROR(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Функция для показа сообщения о успешном выполнении алгоритма
     * @param title
     * @param content
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Feeler Manager. Операция завершена");
        alert.setHeaderText(null); // Убираем заголовок
        alert.setContentText(message);

        // Загрузка изображения зеленой галочки
        Image image = new Image(getClass().getResourceAsStream("SortMediaFiles/Image/iconSuccessfully.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);

        // Установка изображения в качестве иконки
        alert.setGraphic(imageView);

        alert.showAndWait();
    }


    @FXML
    void initialize() {

        btnAuthSigIn.setOnAction(event -> {
            String loginText = fieldLogin.getText().trim();
            String loginPassword = fieldPassword.getText().trim();

            if (!loginText.equals("") && !loginPassword.equals("")) {
                loginUser(loginText, loginPassword);
            } else {
                showAlertERROR("Feeler Manager. Ошибка авторизации", "Введите логин и пароль!");
            }
        });
        
        btnLoginSignUp.setOnAction(event -> {

            btnLoginSignUp.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AuthorizationRegistration.fxml"));
            
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Feeler Manager. Регистрация");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        });

    }

}
