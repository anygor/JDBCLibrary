package com.epam;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserWindow {
    Scanner scanner;
    private String username;
    private String password;
    private String URL; // db
    private String USER_NAME; // db
    private String DB_PASSWORD; // db
    private static final Logger log = LogManager.getLogger();

    static Connection connection;
    static String SQL;
    static PreparedStatement pst;
    static ResultSet rs;

    boolean loggedIn = false;

    public UserWindow(){
        try {
            // Initializing properties for database connection establishment.
            Properties property = new Properties();
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
            property.load(fileInputStream);

            URL = property.getProperty("db.url");
            USER_NAME = property.getProperty("db.username");
            DB_PASSWORD = property.getProperty("db.password");


            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(URL, USER_NAME, DB_PASSWORD);
        }
        catch(FileNotFoundException e){
            log.error("config.properties not found, " + e);
        }
        catch(SQLException e){
            log.error("SQLE at login, " + e);
        }
        catch (ClassNotFoundException e){
            log.error("CNFE at login, " + e);
        }
        catch(IOException e){
            log.error(e);
        }
    }

    public void login(){
        while(!loggedIn) {
            try {
                //userDataInput();
                username = "asgordeev";
                password = "password";
                SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                if (rs.getString(5).equals(username) &&
                        rs.getString(6).equals(password)) {
                    log.info("login success!");
                    loggedIn = true;
                    welcome();
                }
            } catch (SQLException e) {
                log.error("login or password incorrect!");
            }
        }
    }

    private void userDataInput(){
        scanner = new Scanner(System.in);
        log.info("Username: ");
        username = scanner.nextLine();
        log.info("Password: ");
        password = scanner.nextLine();
    }

    private void welcome(){
        String firstName;
        String lastName;

        try {
            SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            firstName = rs.getString(2);
            lastName = rs.getString(4);
            User user = new User(username);
            if (!user.isActive) {
                    log.info("Your account has been blocked.");
            }
            if(user.isAdmin) {
                user = new Admin(user);
                log.info("You logged in as administrator");
            }
            log.info("Welcome, " + firstName + " " + lastName + "!");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
