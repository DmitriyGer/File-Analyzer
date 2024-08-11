package main.MySQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Обработчик базы данных
 */
public class DatabaseHandler extends Configs {

    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {

        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        Class.forName("com.mysql.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;

    }

    /**
     * Метод для передачи информации о пользователе в ДБ
     * @param user
     */
    public void signUpUser(User user) {
        
        String insert = "INSERT INTO " + Const.USER_TABLE + "("  + Const.USERS_FIRSTNAME + "," + 
                                        Const.USERS_LASTNAME + "," + Const.USERS_USERNAME + "," + 
                                        Const.USERS_PASSWORD + "," + Const.USERS_EMAIL + "," +  
                                        Const.USERS_GENDER + ")" +"VALUES(?,?,?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, user.getFirstName());
            prSt.setString(2, user.getLastName());
            prSt.setString(3, user.getUserName());
            prSt.setString(4, user.getPassword());
            prSt.setString(5, user.getEmail());
            prSt.setString(6, user.getGender());
            
            prSt.executeUpdate(); // Обновление информации в БД
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Метод для получения информации из БД
     * @param user
     * @return
     */
    public ResultSet getUser(User user) {

        ResultSet resSet = null;

        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USERS_USERNAME + 
                        "=? AND " + Const.USERS_PASSWORD + "=?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getUserName());
            prSt.setString(2, user.getPassword());
            
            resSet = prSt.executeQuery(); // Получение информации из БД

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resSet;
    }

    /**
     * Метод для проверки уникальности аккаунта
     * @param email
     * @return
     */
    public ResultSet checkEmail(User user) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USERS_EMAIL + "=?";
        
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getEmail());
            
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return resSet;
    }
    
}
