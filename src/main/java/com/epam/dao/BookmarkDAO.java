package com.epam.dao;

import com.epam.UserWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookmarkDAO {
    Connection connection;
    String SQL;
    Statement statement;
    ResultSet resultSet;
    int userID;
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public BookmarkDAO(int userID){
        connection = UserWindow.connection;
        this.userID = userID;
    }

    public void addBookmark(){
        log.info("Book name to add a bookmark to: ");
        scanner = new Scanner(System.in);
        String bookName = scanner.nextLine();
        BookDAO bookDAO = new BookDAO();
        if(!bookDAO.bookExists(bookName)){
            log.info("No such book in your library");
        }
        else {
            log.info("Page number where your bookmark will be at: ");
            scanner = new Scanner(System.in);
            int page = scanner.nextInt();
            scanner = new Scanner(System.in);
            try {
                statement = connection.createStatement();
                SQL = "SELECT pageCount, ISBN FROM Books WHERE bookName = '" + bookName + "'";
                resultSet = statement.executeQuery(SQL);
                resultSet.next();
                if (resultSet.getInt(1) < page) {
                    log.info("This book can't have that bookmark");
                } else {
                    statement = connection.createStatement();
                    SQL = "INSERT INTO Bookmarks (userID, ISBN, pageNum, isDeleted) VALUES (" + this.userID + ", '" + resultSet.getString(2) + "', " + page + ", 'False')";
                    resultSet = statement.executeQuery(SQL);
                    UserWindow.history.addToHistory("User with ID " + this.userID + " added a bookmark: " + bookName + " -" + page + "\n");
                    log.info("bookmark added");
                }
                close();
            } catch (SQLException e) {
                log.error("addBookmarksqlexception");
            }
        }
    }

    private boolean bookmarkExists(String bookName, int pageNum){
        try{
            statement = connection.createStatement();
            SQL = "SELECT COUNT(*) FROM Bookmarks WHERE userID = " + this.userID + " AND ISBN = " +
                    "(SELECT ISBN FROM Books WHERE ISBN = Bookmarks.ISBN AND bookName = '" + bookName + "') " +
                    " AND pageNum = " + pageNum + " AND isDeleted = 'False'";
            resultSet = statement.executeQuery(SQL);
            resultSet.next();
            if(resultSet.getInt(1) != 0){
                close();
                return true;
            }
            else {
                close();
                return false;
            }
        }
        catch(SQLException e){
            log.error("bookmark exists exception sql");
            return false;
        }
    }

    public void removeBookmark(String bookName, int pageNum){
        if(!bookmarkExists(bookName, pageNum)){
            log.info("No such bookmark");
        }
        else{
            try {
                statement = connection.createStatement();
                SQL = "UPDATE Bookmarks SET isDeleted = 'True' where ISBN = " +
                        "(SELECT ISBN FROM Books WHERE ISBN = Bookmarks.ISBN AND bookName = '" +
                        bookName + "' AND pageNum = " + pageNum + " AND userID = " + this.userID + ")";
                resultSet = statement.executeQuery(SQL);
                UserWindow.history.addToHistory("User with id " + this.userID + " removed a bookmark: " + bookName + " - " + pageNum + "\n");
                log.info("Bookmark removed");
                close();
            }
            catch(SQLException e){
                log.error("removeBookmarkSQLexception");
            }
        }
    }

    public void myBookmarks(){
        try{
            statement = connection.createStatement();
            SQL = "SELECT (SELECT bookName FROM Books WHERE ISBN = Bookmarks.ISBN), pageNum FROM Bookmarks WHERE isDeleted = 'False' AND userID = " + this.userID;
            resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                log.info(resultSet.getString(1) + ": " + resultSet.getInt(2));
            }
            close();
        }
        catch(SQLException e){
            log.error("mybookmarks exception");
        }
    }

    private void close(){
        try {
            resultSet.close();
            statement.close();
        }
        catch (SQLException e){
            log.error("I didn't close anything");
        }
    }
}
