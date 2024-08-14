package main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.MySQLConnection.DatabaseHandler;
import main.MySQLConnection.User;

public class AuthorizationRegistrationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox boxAuthorization;

    @FXML
    private Button btnSignUp;

    @FXML
    private Button btnAuthorization;

    @FXML
    private TextField fieldLogin;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private CheckBox signUpCheckMan;

    @FXML
    private CheckBox signUpCheckWoman;

    @FXML
    private TextField signUpEmail;

    @FXML
    private TextField signUpLastname;

    @FXML
    private TextField signUpName;

    @FXML
    private Label textAuthorization;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Кнопка перехода на страницу входа
     * @param event
     */
    @FXML
    void btnAuthorizationPressed(ActionEvent event) {
        try {
            Stage currentStage = (Stage) btnAuthorization.getScene().getWindow();
            currentStage.close();
    
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Authorization.fxml"));
            Parent root = fxmlLoader.load();
            AuthorizationController controller = fxmlLoader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);
    
            newStage.setResizable(false);
            newStage.setTitle("Feeler Manager. Авторизация");
            newStage.setScene(new Scene(root));
    
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initStyle(StageStyle.DECORATED);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для проверки заполненности полей
     */
    private String validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        if (signUpName.getText().isEmpty()) {
            errorMessage.append("Поле Имя не заполнено.\n");
        }
        if (signUpLastname.getText().isEmpty()) {
            errorMessage.append("Поле Фамилия не заполнено.\n");
        }
        if (fieldLogin.getText().isEmpty()) {
            errorMessage.append("Поле Логин не заполнено.\n");
        }
        if (fieldPassword.getText().isEmpty()) {
            errorMessage.append("Поле Пароль не заполнено.\n");
        }
        if (signUpEmail.getText().isEmpty()) {
            errorMessage.append("Поле Email не заполнено.\n");
        }
        // if (!signUpCheckMan.isSelected() && !signUpCheckWoman.isSelected()) {
        //     errorMessage.append("Не выбран пол.\n");
        // }

        return errorMessage.toString();
    }

    /**
     * Метод для проверки уникальности почты
     * @param email
     * @return
     * @throws SQLException 
     */
    private boolean isUserExist(String email) throws SQLException {
        boolean userExist = false;
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setEmail(email);
        ResultSet result = dbHandler.checkEmail(user);

        try {
            if (result != null && result.next()) {
                userExist = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return userExist;
    }

    /**
     * Добавление нового пользователя
     */
    private void signUpNewUser() {

        DatabaseHandler dbHendler = new DatabaseHandler();

        String firstName = signUpName.getText();
        String lastName = signUpLastname.getText();
        String userName = fieldLogin.getText();
        String password = fieldPassword.getText();
        String email = signUpEmail.getText();
        String gender = "муж"; // Заглушка
        // if (signUpCheckMan.isSelected()) {
        //     gender = "Муж";
        // } else {
        //     gender = "Жен";
        // }

        User user = new User(firstName, lastName, userName, password, email, gender);

        dbHendler.signUpUser(user);
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
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Загрузка изображения зеленой галочки
        Image image = new Image(getClass().getResourceAsStream("SortMediaFiles/Image/iconSuccessfully.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);

        // Установка изображения в качестве иконки
        alert.setGraphic(imageView);

        alert.setOnHidden(evt -> {
            if (stage != null) {
                stage.close();
            }
        });

        alert.showAndWait();
    }

    private void loadAuthorizationWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Authorization.fxml"));
            Parent root = fxmlLoader.load();
            AuthorizationController controller = fxmlLoader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);

            newStage.setResizable(false);
            newStage.setTitle("Feeler Manager. Авторизация");
            newStage.setScene(new Scene(root));

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initStyle(StageStyle.DECORATED);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ЧАСТИЧНО 1. Добавить разлиыне проверки для полей
     *    - Почта. Проверить, чтобы почта указывалась корректно, создать шаблоны.
     *      Реализовать проверку на уникальноть почты.
     *    - Пароль. Пароль должен содержать определенное кол-во символов. Создать
     *      алгоритм, для проверки пароля на легксть
     *    - Логин. Пароль должен содержать определенное кол-во символов. Реализовать
     *      проверку на уникальноть логина.
     * РЕАЛИЗОВАНЫ проверки на пустоту
     * 
     * ЕСТЬ 2. При наведении на кнопку "Зарегестрироваться" должен меняться цает, а при ее нажатии
     *    сделать анимацию, что кнопка нажимается. Так же, если регестрация прошла без ошибок
     *    сделать переход на страницу войти.
     * 
     * ЕСТЬ 3. Решено иначе. Добавить в окно зарегистрироваться checkBox рядом с логином (Такой же, как почта)
     * 
     * ЕСТЬ 4. После успешной регистрации закрывать окно
     */
    @FXML
    void initialize() {
        
        // Добавляем слушатели изменений состояния CheckBox
        signUpCheckMan.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                signUpCheckWoman.setSelected(false);
            }
        });

        signUpCheckWoman.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                signUpCheckMan.setSelected(false);
            }
        });

        // Добавляем слушатель изменений на текстовое поле signUpEmail
        signUpEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            fieldLogin.setText(newValue);
        });

        // Действие при нажатии на кнопку "Зарегестрироваться"
        btnSignUp.setOnAction(event -> {
            String validationMessage = validateFields();
            if (!validationMessage.isEmpty()) {
                showAlertERROR("Feeler Manager. Ошибка регистрации", validationMessage);
            } else {
                String email = signUpEmail.getText();
                try {
                    if (isUserExist(email)) {
                        boxAuthorization.setVisible(true);
                    } else {
                        signUpNewUser();
                        showSuccessAlert("Вы успешно зарегистрированы.");
                        loadAuthorizationWindow();
                        Stage currentStage = (Stage) btnSignUp.getScene().getWindow();
                        currentStage.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlertERROR("Feeler Manager. Ошибка", "Ошибка при доступе к базе данных.");
                }
            }
        });

    }
}
