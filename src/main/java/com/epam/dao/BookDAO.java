package com.epam.dao;

import com.epam.UserWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO {

    Connection connection;
    String SQL;
    AuthorDAO authorDAO;
    private static final Logger log = LogManager.getLogger();

    public BookDAO(){
        connection = UserWindow.connection;
    }

    public void listOfBooks(){
        try(Statement statement = connection.createStatement()) {
            SQL = "SELECT bookName FROM Books WHERE isDeleted = 'False'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next()) {
                    log.info(resultSet.getString(1));
                }
            }
        }
        catch(SQLException e){
            log.error("listOfBooks exception sql");
        }
    }

    public void addBook(String bookName, int releaseYear, String author, int pageCount, String publisher){
        int authorID;
        int publisherID;
        String ISBN;

        authorDAO = new AuthorDAO();
        authorDAO.addAuthor(author);
        authorID = authorDAO.authorID(author);
        addPublisher(publisher);
        publisherID = publisherID(publisher);
        ISBN = generateISBN();

        if(bookExists(bookName)){
            log.info("Book is already in your library");
        }
        else {
            try (Statement statement = connection.createStatement();){
                SQL = "INSERT INTO \"JDBC\".\"BOOKS\" (BOOKNAME, RELEASEYEAR, AUTHORID, PAGECOUNT, ISBN, PUBLISHERID, ISDELETED) VALUES " +
                        "('" + bookName + "', '" + releaseYear + "', '" + authorID + "', '" + pageCount + "', '" + ISBN + "', '" + publisherID + "', 'False')";
                statement.execute(SQL);
                UserWindow.history.addToHistory("User " + UserWindow.id + " added book " + bookName + "\n");
            } catch (SQLException e) {
                log.error("Add book sql mistake");
            }
        }
    }

    private String generateISBN() {
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT ISBN FROM Books ORDER BY ISBN DESC";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                resultSet.next();
                String tmp = resultSet.getString(1);
                tmp = tmp.substring(4);
                long id = Long.parseLong(tmp);
                id++;
                return "978-" + id;
            }
        }
        catch(SQLException e){
            log.error("generate isbn sqlexeption");
            return "-1";
        }
    }

    private boolean publisherExists(String publisher){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT COUNT(*) FROM Publishers WHERE publisherName = '" + publisher + "'";
            try (ResultSet resultSet = statement.executeQuery(SQL)) {
                resultSet.next();
                return resultSet.getInt(1) != 0;
            }
        }
        catch (SQLException e){
            log.error("publisherExists method sql exception");
            return false;
        }
    }

    private int publisherID(String publisher){
        int id;
        try(Statement statement = connection.createStatement()){
            if(publisherExists(publisher)){
                SQL = "SELECT publisherID FROM Publishers WHERE publisherName = '" + publisher + "'";
                try(ResultSet resultSet = statement.executeQuery(SQL)) {
                    resultSet.next();
                    id = resultSet.getInt(1);
                    return id;
                }
            }
            else{
                SQL = "SELECT MAX(publisherID) FROM Publishers;";
                try (ResultSet resultSet = statement.executeQuery(SQL)) {
                    id = resultSet.getInt(1) + 1;
                    return id;
                }
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
            try (Statement statement = connection.createStatement()){
                int currentMaxID;
                SQL = "SELECT MAX(publisherID) FROM Publishers";
                try(ResultSet resultSet = statement.executeQuery(SQL)) {
                    resultSet.next();
                    currentMaxID = resultSet.getInt(1);
                }

                SQL = "INSERT INTO Publishers (publisherID, publisherName) VALUES (" + (currentMaxID + 1) + ", '" + publisher + "')";
                statement.execute(SQL);
            }
            catch(SQLException e) {
                log.error("add publisher sql excpetion");
            }
        }
    }

    public boolean bookExists(String bookName) {
        try (Statement statement = connection.createStatement()) {
            SQL = "SELECT COUNT(*) FROM Books WHERE bookName = '" + bookName + "' AND isDeleted = 'False'";
            try (ResultSet resultSet = statement.executeQuery(SQL)) {
                resultSet.next();
                int bookAmount = resultSet.getInt(1);
                return bookAmount != 0;
            }
        }
        catch(SQLException e){
            log.error("BookExists SQL exception");
            return false;
        }
    }

    public void removeBook(String bookName){
        try (Statement statement = connection.createStatement()){
            if (bookExists(bookName)) {
                SQL = "UPDATE BOOKS SET isDeleted = 'True' WHERE bookName = '" + bookName + "'";
                try(ResultSet resultSet = statement.executeQuery(SQL)) {
                    UserWindow.history.addToHistory("User " + UserWindow.id + " removed book " + bookName + "\n");
                    log.info("Book " + bookName + " removed");
                }
            }
            else{
                log.info("There is no such book");
            }
        }
        catch(SQLException e){
            log.error("remove book sql error");
        }
    }

    public void nameSearch(String bookName){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT bookName FROM Books WHERE bookName LIKE '%" + bookName + "%' AND isDeleted = 'False'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next())
                    log.info(resultSet.getString(1));
            }
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void authorSearch(String author){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT bookName, (SELECT name FROM Authors WHERE authorID = Books.authorID), (SELECT lastname FROM Authors WHERE authorID = Books.authorID) \n" +
                    "    FROM Books\n" +
                    "    WHERE isDeleted = 'False' AND (SELECT name FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%' OR " + "" +
                    "   (SELECT lastname FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next())
                    log.info(resultSet.getString(1) + " by " + resultSet.getString(2) + " " + resultSet.getString(3));
            }
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void ISBNSearch(String ISBN){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT bookName, ISBN FROM Books WHERE ISBN LIKE '%" + ISBN + "%'" + " AND isDeleted = 'False'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next())
                    log.info(resultSet.getString(1) + " - " + resultSet.getString(2));
            }
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void yearSearch(int min, int max){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT bookName, releaseYear FROM Books WHERE releaseYear > " + min + " AND releaseyear < " + max + " AND isDeleted = 'False'";
            try (ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next())
                    log.info(resultSet.getString(1) + " - " + resultSet.getInt(2));
            }
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void multipleSearch(int min, int max, int pageAmount, String author){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT * FROM Books WHERE releaseYear > " + min + " AND releaseyear < " + max  + " AND isDeleted = 'False'" + " AND pageCount = " + pageAmount + " AND\n" +
                    "                ((SELECT name FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author
                    + "%' OR (SELECT lastname FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%')";
            try (ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next())
                    log.info(resultSet.getString(1) + " - " + resultSet.getString(2));
            }
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }
}
