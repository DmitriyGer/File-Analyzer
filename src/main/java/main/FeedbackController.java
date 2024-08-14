package main;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


public class FeedbackController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea messageArea;

    @FXML
    private void handleSendFeedback() {
        String name = nameField.getText();
        String email = emailField.getText();
        String message = messageArea.getText();

        // Проверка заполненности полей
        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            showAlert("Feeler Manager. Ошибка", "Все поля должны быть заполнены!");
        } else {
            try {
                sendEmail(name, email, message);
                showAlert("Feeler Manager. Успешно", "Сообщение отправленно успешно!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Feeler Manager. Ошибка", "Не удалось отправить сообщение:" + e.getMessage());
            }
        }
    }

    private void sendEmail(String name, String email, String message) throws MessagingException {
        final String username = "dim1a15@mail.ru"; // email адрес отправителя
        final String password = "m23EEtt507s5nzwYS8Dz"; // пароль для внешних подключений

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mail.ru");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.enable", "false");

        if (username.isEmpty() || password.isEmpty()) {
            throw new MessagingException("Имя пользователя и/или пароль не заданы.");
        }

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dim1a15@mail.ru")); // ящик получателя
            mimeMessage.setSubject("Обратная связь от " + name);
            mimeMessage.setText("Имя: " + name + "\nПочта: " + email + "\nСообщение:\n" + message);

            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MessagingException("Error sending email: " + e.getMessage(), e);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}