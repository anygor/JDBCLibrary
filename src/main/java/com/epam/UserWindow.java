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
    static History history;
    static int id;
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
            history = new History();
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
        history.addToHistory(user.name + " " + user.lastName + " logged in\n");
        log.info("\n\n\n\n");
        log.info("MAIN MENU");
        log.info("Type one of the control commands. To see those type help. ");
        log.info("To quit type quit.");


        String command;
        scanner = new Scanner(System.in);

        label:
        while(true) {
            command = scanner.nextLine();
            switch (command) {
                case "help": {
                    log.info("list of supported commands:");
                    log.info("help - stop it, get some help");
                    log.info("listOfBooks - list of books in the library");
                    log.info("listOfAuthors - list of authors in the library");
                    log.info("myBookmarks - my bookmarks");
                    log.info("addBookmark - to add bookmark");
                    log.info("removeBookmark - remove bookmark");
                    log.info("addAuthor - to add a new author");
                    log.info("addBook - to add some boox");
                    log.info("removeBook - to remove book");
                    log.info("removeAuthor - to remove author and their books");
                    log.info("search - to go to search sub-menu");
                    log.info("quit - to quit");
                    break;
                }
                case "listOfBooks":{
                    BookDAO bookDAO = new BookDAO();
                    bookDAO.listOfBooks();
                    break;
                }
                case "listOfAuthors":{
                    new AuthorDAO().listOfAuthors();
                    break;
                }
                case "myBookmarks": {
                    new Bookmark(user.userID).myBookmarks();
                    break;
                }
                case "addBookmark": {
                    new Bookmark(user.userID).addBookmark();
                    break;
                }
                case "removeBookmark": {
                    new Bookmark(user.userID).removeBookmark();
                    break;
                }
                case "addAuthor": {
                    new Author().addAuthor();
                    break;
                }
                case "addBook": {
                    new Book().addBook();
                    break;
                }
                case "removeBook": {
                    new Book().removeBook();
                    break;
                }
                case "removeAuthor": {
                    new Author().removeAuthor();
                    break;
                }
                case "search": {
                    new Book().search();
                    break;
                }
                case "quit":
                    log.info("Goodbye.");
                    closeConnection();
                    history.addToHistory("User " + user.name + " " + user.lastName + " logged out\n");
                    History.fileHistoryOutput();
                    break label;
                default:
                    log.info("invalid command");
                    break;
            }
        }
    }

    private void menu(Admin user){
        log.info("\n\n\n\n");
        log.info("MAIN MENU");
        log.info("Type one of the control commands. To see those type help. ");
        log.info("To quit type quit.");


        String command;
        scanner = new Scanner(System.in);

        label:
        while(true) {
            command = scanner.nextLine();
            switch (command) {
                case "help": {
                    log.info("list of supported commands:");
                    log.info("help - stop it, get some help");
                    log.info("addUser - to add new user");
                    log.info("blockUser - to block user");
                    log.info("listOfBooks - list of books in the library");
                    log.info("listOfAuthors - list of authors in the library");
                    log.info("myBookmarks - my bookmarks");
                    log.info("addBookmark - to add bookmark");
                    log.info("removeBookmark - remove bookmark");
                    log.info("addAuthor - to add a new author");
                    log.info("addBook - to add some boox");
                    log.info("removeBook - to remove book");
                    log.info("removeAuthor - to remove author and their books");
                    log.info("search - to go to search sub-menu");
                    log.info("quit - to quit");
                    break;
                }
                case "listOfBooks": {
                    new Book().listOfBooks();
                    break;
                }
                case "listOfAuthors": {
                    new Author().listOfAuthors();
                    break;
                }
                case "myBookmarks": {
                    new Bookmark(user.userID).myBookmarks();
                    break;
                }
                case "addBookmark": {
                    new Bookmark(user.userID).addBookmark();
                    break;
                }
                case "removeBookmark": {
                    new Bookmark(user.userID).removeBookmark();
                    break;
                }
                case "addUser": {
                    user.addUser();
                    break;
                }
                case "blockUser" : {
                    user.blockUser();
                    break;
                }
                case "addAuthor": {
                    new Author().addAuthor();
                    break;
                }
                case "addBook": {
                    new Book().addBook();
                    break;
                }
                case "removeBook": {
                    new Book().removeBook();
                    break;
                }
                case "removeAuthor": {
                    new Author().removeAuthor();
                    break;
                }
                case "search": {
                    new Book().search();
                    break;
                }
                case "quit":
                    log.info("Goodbye.");
                    closeConnection();
                    break label;
                default:
                    log.info("invalid command");
                    break;
            }
        }
    }

    private void closeConnection(){
        try{
            connection.close();
        }
        catch (SQLException e){
            log.error("close connection error");
        }
    }
}
