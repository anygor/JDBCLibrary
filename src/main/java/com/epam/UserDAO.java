package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserDAO {
    private int userID;
    Scanner stringScanner;
    Scanner numberScanner;
    private static final Logger log = LogManager.getLogger();

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
    AuthorDAO authorDAO;

    public UserDAO(){
        authorDAO = new AuthorDAO();
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
    }

    public UserDAO(int _userID){
        userID = _userID;
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
    }

    public void setUser(User user, String username){
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
        try {
            SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            user.userID = rs.getInt(1);
            user.name = rs.getString(2);
            user.secondName = rs.getString(3);
            user.lastName = rs.getString(4);
            user.isAdmin = rs.getString(7).equals("Admin");
            user.isActive = rs.getString(8).equals("Active");
        }
        catch(SQLException e){
            log.error("setUser exception, " + e);
        }
    }

    public void addBookmark(){
        log.info("Book name to add a bookmark to: ");
        stringScanner = new Scanner(System.in);
        String bookName = stringScanner.nextLine();
        BookDAO bookDAO = new BookDAO();
        if(!bookDAO.bookExists(bookName)){
            log.info("No such book in your library");
        }
        else {
            log.info("Page number where your bookmark will be at: ");
            numberScanner = new Scanner(System.in);
            int page = numberScanner.nextInt();
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
