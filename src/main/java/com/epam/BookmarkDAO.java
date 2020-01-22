package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookmarkDAO {
    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
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
                SQL = "SELECT pageCount, ISBN FROM Books WHERE bookName = '" + bookName + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                if (rs.getInt(1) < page) {
                    log.info("This book can't have that bookmark");
                } else {
                    SQL = "INSERT INTO Bookmarks (userID, ISBN, pageNum, isDeleted) VALUES (" + this.userID + ", '" + rs.getString(2) + "', " + page + ", 'False')";
                    pst = connection.prepareStatement(SQL);
                    rs = pst.executeQuery();
                    UserWindow.history.addToHistory("User with ID " + this.userID + " added a bookmark: " + bookName + " -" + page + "\n");
                    log.info("bookmark added");
                }
            } catch (SQLException e) {
                log.error("addBookmarksqlexception");
            }
        }
    }

    private boolean bookmarkExists(String bookName, int pageNum){
        try{
            SQL = "SELECT COUNT(*) FROM Bookmarks WHERE userID = " + this.userID + " AND ISBN = " +
                    "(SELECT ISBN FROM Books WHERE ISBN = Bookmarks.ISBN AND bookName = '" + bookName + "') " +
                    " AND pageNum = " + pageNum + " AND isDeleted = 'False'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            if(rs.getInt(1) != 0){
                return true;
            }
            else return false;
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
                SQL = "UPDATE Bookmarks SET isDeleted = 'True' where ISBN = " +
                        "(SELECT ISBN FROM Books WHERE ISBN = Bookmarks.ISBN AND bookName = '" +
                        bookName + "' AND pageNum = " + pageNum + " AND userID = " + this.userID + ")";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                UserWindow.history.addToHistory("User with id " + this.userID + " removed a bookmark: " + bookName + " - " + pageNum + "\n");
                log.info("Bookmark removed");
            }
            catch(SQLException e){
                log.error("removeBookmarkSQLexception");
            }
        }
    }

    public void myBookmarks(){
        try{
            SQL = "SELECT (SELECT bookName FROM Books WHERE ISBN = Bookmarks.ISBN), pageNum FROM Bookmarks WHERE isDeleted = 'False' AND userID = " + this.userID;
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while (rs.next()) {
                log.info(rs.getString(1) + ": " + rs.getInt(2));
            }
        }
        catch(SQLException e){
            log.error("mybookmarks exception");
        }
    }
}
