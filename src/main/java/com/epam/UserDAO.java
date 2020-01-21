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

    public void addBook(){
        String bookName;
        int releaseYear;
        String author;
        int pageCount;
        String publisher;
        int authorID;
        int publisherID;
        String ISBN;
        Scanner numberScanner = new Scanner(System.in);

        stringScanner = new Scanner(System.in);
        log.info("addBook method called.");
        log.info("Book Name: ");
        bookName = stringScanner.nextLine();
        log.info("Release year: ");
        releaseYear = numberScanner.nextInt();
        log.info("Author (FN/SN/LN): ");
        author = stringScanner.nextLine();
        log.info("Amount of pages: ");
        pageCount = numberScanner.nextInt();
        log.info("Publisher: ");
        publisher = stringScanner.nextLine();
        addAuthor(author);
        authorID = authorID(author);
        addPublisher(publisher);
        publisherID = publisherID(publisher);
        ISBN = generateISBN();

        if(bookExists(bookName)){
            log.info("Book is already in your library");
        }
        else {
            try {
                SQL = "INSERT INTO \"JDBC\".\"BOOKS\" (BOOKNAME, RELEASEYEAR, AUTHORID, PAGECOUNT, ISBN, PUBLISHERID, ISDELETED) VALUES " +
                        "('" + bookName + "', '" + releaseYear + "', '" + authorID + "', '" + pageCount + "', '" + ISBN + "', '" + publisherID + "', 'False')";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
            } catch (SQLException e) {
                log.error("Add book sql mistake");
            }
        }
    }

    private String generateISBN() {
        try{
            SQL = "SELECT ISBN FROM Books ORDER BY ISBN DESC";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            String tmp = rs.getString(1);
            tmp = tmp.substring(4);
            long id = Long.parseLong(tmp);
            id++;
            return "978-" + id;
        }
        catch(SQLException e){
            log.error("generate isbn sqlexeption");
            return "-1";
        }
    }

    private int authorID(String author){
        String[] authorStrings = author.split(" ");
        String authorFirstName;
        String authorSecondName;
        String authorLastName;
        if(authorStrings.length == 2){
            authorFirstName = authorStrings[0];
            authorLastName = authorStrings[1];
        }
        else{
            authorFirstName = authorStrings[0];
            authorLastName = authorStrings[2];
        }
        try{
            if(authorDAO.authorExists(author)){
                SQL = "SELECT authorID FROM Authors WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                return rs.getInt(1);
            }
            else{
                SQL = "SELECT MAX(authorid) FROM AUTHORS;";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                return rs.getInt(1) + 1;
            }
        }
        catch(SQLException e){
            return -1;
        }
    }

    private boolean publisherExists(String publisher){
        try{
            SQL = "SELECT COUNT(*) FROM Publishers WHERE publisherName = '" + publisher + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            if(rs.getInt(1) != 0){
                return true;
            }
            else return false;
        }
        catch (SQLException e){
            log.error("publisherExists method sql exception");
            return false;
        }
    }

    private int publisherID(String publisher){
        try{
            if(publisherExists(publisher)){
                SQL = "SELECT publisherID FROM Publishers WHERE publisherName = '" + publisher + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                return rs.getInt(1);
            }
            else{
                SQL = "SELECT MAX(publisherID) FROM Publishers;";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                return rs.getInt(1) + 1;
            }
        }
        catch(SQLException e){
            return -1;
        }
    }

    private void addPublisher(String publisher){
        if(publisherExists(publisher)){
            log.info("publisher exists");
        }
        else{
            try {
                int currentMaxID;
                SQL = "SELECT MAX(publisherID) FROM Publishers";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                currentMaxID = rs.getInt(1);

                SQL = "INSERT INTO Publishers (publisherID, publisherName) VALUES (" + (currentMaxID + 1) + ", '" + publisher + "')";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
            }
            catch(SQLException e) {
                log.error("add publisher sql excpetion");
            }
        }
    }

    private boolean bookExists(String bookName){
        try {
            SQL = "SELECT COUNT(*) FROM Books WHERE bookName = '" + bookName + "' AND isDeleted = 'False'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            int bookAmount = rs.getInt(1);
            if (bookAmount == 0) {
                return false;
            } else return true;
        }
        catch(SQLException e){
            log.error("BookExists SQL exception");
            return false;
        }
    }

    public void addAuthor(String author){
        stringScanner = new Scanner(System.in);
        if(authorDAO.authorExists(author)){
            log.info("Author exists");
        }
        else{
            String[] authorStrings = author.split(" ");
            String authorFirstName;
            String authorSecondName;
            String authorLastName;
            log.info("Author should have a birthdate, please enter one (DD-MMM-YYYY):");
            String dob = stringScanner.nextLine();
            if(authorStrings.length == 2){
                authorFirstName = authorStrings[0];
                authorSecondName = null;
                authorLastName = authorStrings[1];
            }
            else{
                authorFirstName = authorStrings[0];
                authorSecondName = authorStrings[1];
                authorLastName = authorStrings[2];
            }
            try {
                int currentMaxID;
                SQL = "SELECT MAX(authorid) FROM Authors";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                rs.next();
                currentMaxID = rs.getInt(1);
                SQL = "INSERT INTO \"JDBC\".\"AUTHORS\" (AUTHORID, NAME, SECONDNAME, LASTNAME, DOB, ISDELETED) VALUES " +
                        "('" + (currentMaxID + 1) + "', '" + authorFirstName + "', '" + authorSecondName + "', '" + authorLastName + "', '" + dob + "', '" + "False')";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                log.info("author added");
            }
            catch(SQLException e){
                log.error("sql exception at addauthor");
            }
        }
    }

    public void removeBook(String bookName){
        try {
            if (bookExists(bookName)) {
                SQL = "UPDATE BOOKS SET isDeleted = 'True' WHERE bookName = '" + bookName + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                log.info("Book " + bookName + " removed");
            }
            else{
                log.info("There is no such book");
            }
        }
        catch(SQLException e){
            log.error("remove book sql error");
        }
    }

    public void removeAuthor(String author){
        if(!authorDAO.authorExists(author)){
            log.info("No such author");
        }
        else {
            String[] authorStrings = author.split(" ");
            String authorFirstName;
            String authorSecondName;
            String authorLastName;
            if (authorStrings.length == 2) {
                authorFirstName = authorStrings[0];
                authorSecondName = null;
                authorLastName = authorStrings[1];
            } else {
                authorFirstName = authorStrings[0];
                authorSecondName = authorStrings[1];
                authorLastName = authorStrings[2];
            }
            try {
                SQL = "UPDATE Books SET isDeleted = 'True' WHERE authorID = " + authorID(author);
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                SQL = "UPDATE Authors SET isDeleted = 'True' WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                log.info("Author " + authorFirstName + " " + authorLastName + " removed");
            }
            catch(SQLException e){
                log.error("author remove error");
            }
        }
    }

    public void addBookmark(){
        log.info("Book name to add a bookmark to: ");
        stringScanner = new Scanner(System.in);
        String bookName = stringScanner.nextLine();
        if(!bookExists(bookName)){
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
