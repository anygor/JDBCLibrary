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


            Class.forName(property.getProperty("db.driverSetup"));
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
                userDataInput();
                //username = "asgordeev";
                //password = "password";
                SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                if (rs.getString(5).equals(username) &&
                        rs.getString(6).equals(password)) {
                    log.info("login success!");
                    loggedIn = true;
                }
                else{
                    log.info("Wrong password, try again.");
                }
            } catch (SQLException e) {
                log.info("No such user, try again.");
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

    public void welcome(){
        try {
            SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            User user = new User(username);
            if (!user.isActive) {
                log.info("Your account has been blocked.");
            }
            else {
                log.info("Welcome, " + user.name + " " + user.lastName + "!");
                if(user.isAdmin){
                    log.info("You logged in as administrator");
                    Admin admin = new Admin(user);
                    menu(admin);
                }
                else{
                    menu(user);
                }
            }
        }
        catch (SQLException e) {
            log.error("Welcome SQL exception");
        }
    }

    private void menu(User user){
        log.info("\n\n\n\n");
        log.info("MAIN MENU");
        log.info("Type one of the control commands. To see those type help. ");
        log.info("To quit type quit.");


        String command;
        scanner = new Scanner(System.in);

        while(true) {
            command = scanner.nextLine();
            // command = "addBook";
            if(command.equals("help")){
                log.info("list of supported commands:");
                log.info("help - stop it, get some help");
                log.info("addAuthor - to add a new author");
                log.info("addBook - to add some boox");
                log.info("removeBook - to remove book");
                log.info("quit - to quit");
            }
            else if (command.equals("addAuthor")){
                String author;
                log.info("Author FN/SN/LN: ");
                author = scanner.nextLine();
                user.addAuthor(author);
            }
            else if(command.equals("addBook")){
                user.addBook();
            }
            else if(command.equals("removeBook")){
                String bookName;
                log.info("Book name:");
                bookName = scanner.nextLine();
                user.removeBook(bookName);
            }
            else if (command.equals("quit")) {
                log.info("Goodbye.");
                break;
            }
            else log.info("The cycle proceeds");
        }
    }

    private void menu(Admin user){
        log.info("\n\n\n\n");
        log.info("MAIN MENU");
        log.info("Type one of the control commands. To see those type help. ");
        log.info("To quit type quit.");


        String command;
        scanner = new Scanner(System.in);

        while(true) {
            command = scanner.nextLine();
            if(command.equals("help")){
                log.info("list of supported commands:");
                log.info("help - stop it, get some help");
                log.info("addBook - to add some boox");
                log.info("addAuthor - to add a new author");
                log.info("addUser - to add some new user");
                log.info("removeBook - to remove book");
                log.info("quit - to quit");
            }
            else if (command.equals("addUser")){
                user.addUser();
            }
            else if (command.equals("addAuthor")){
                String author;
                log.info("Author FN/SN/LN: ");
                author = scanner.nextLine();
                user.addAuthor(author);
            }
            else if(command.equals("addBook")){
                user.addBook();
            }
            else if(command.equals("removeBook")){
                String bookName;
                log.info("Book name:");
                bookName = scanner.nextLine();
                user.removeBook(bookName);
            }
            else if (command.equals("quit")) {
                log.info("Goodbye.");
                break;
            }
            else log.info("The cycle proceeds");
        }
    }
}
