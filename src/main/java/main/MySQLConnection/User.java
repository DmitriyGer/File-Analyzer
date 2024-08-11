package main.MySQLConnection;

/**
 * Работа с пользовательскими данными
 */
public class User {

    private String firstName;   // Имя
    private String lastName;    // Фамилия
    private String userName;    // Логин
    private String password;    // Пароль
    private String email;       // Почта
    private String gender;      // Пол

    public User(String firstName, String lastName, String userName, String password, String email, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.gender = gender;
    }

    public User() {
        
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}