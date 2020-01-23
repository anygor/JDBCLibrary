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
    Statement statement;
    ResultSet resultSet;
    AuthorDAO authorDAO;
    private static final Logger log = LogManager.getLogger();

    public BookDAO(){
        connection = UserWindow.connection;
    }

    public void listOfBooks(){
        try{
            statement = connection.createStatement();
            SQL = "SELECT bookName FROM Books WHERE isDeleted = 'False'";
            resultSet = statement.executeQuery(SQL);
            while(resultSet.next()){
                log.info(resultSet.getString(1));
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
            try {
                statement = connection.createStatement();
                SQL = "INSERT INTO \"JDBC\".\"BOOKS\" (BOOKNAME, RELEASEYEAR, AUTHORID, PAGECOUNT, ISBN, PUBLISHERID, ISDELETED) VALUES " +
                        "('" + bookName + "', '" + releaseYear + "', '" + authorID + "', '" + pageCount + "', '" + ISBN + "', '" + publisherID + "', 'False')";
                resultSet = statement.executeQuery(SQL);
                UserWindow.history.addToHistory("User " + UserWindow.id + " added book " + bookName + "\n");
            } catch (SQLException e) {
                log.error("Add book sql mistake");
            }
        }
    }

    private String generateISBN() {
        try{
            statement = connection.createStatement();
            SQL = "SELECT ISBN FROM Books ORDER BY ISBN DESC";
            resultSet = statement.executeQuery(SQL);
            resultSet.next();
            String tmp = resultSet.getString(1);
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

    private boolean publisherExists(String publisher){
        try{
            statement = connection.createStatement();
            SQL = "SELECT COUNT(*) FROM Publishers WHERE publisherName = '" + publisher + "'";
            resultSet = statement.executeQuery(SQL);
            resultSet.next();
            if(resultSet.getInt(1) != 0){
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
                statement = connection.createStatement();
                SQL = "SELECT publisherID FROM Publishers WHERE publisherName = '" + publisher + "'";
                resultSet = statement.executeQuery(SQL);
                resultSet.next();
                return resultSet.getInt(1);
            }
            else{
                statement = connection.createStatement();
                SQL = "SELECT MAX(publisherID) FROM Publishers;";
                resultSet = statement.executeQuery(SQL);
                return resultSet.getInt(1) + 1;
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
                statement = connection.createStatement();
                SQL = "SELECT MAX(publisherID) FROM Publishers";
                resultSet = statement.executeQuery(SQL);
                resultSet.next();
                currentMaxID = resultSet.getInt(1);

                statement = connection.createStatement();
                SQL = "INSERT INTO Publishers (publisherID, publisherName) VALUES (" + (currentMaxID + 1) + ", '" + publisher + "')";
                resultSet = statement.executeQuery(SQL);
            }
            catch(SQLException e) {
                log.error("add publisher sql excpetion");
            }
        }
    }

    public boolean bookExists(String bookName){
        try {
            statement = connection.createStatement();
            SQL = "SELECT COUNT(*) FROM Books WHERE bookName = '" + bookName + "' AND isDeleted = 'False'";
            resultSet = statement.executeQuery(SQL);
            resultSet.next();
            int bookAmount = resultSet.getInt(1);
            if (bookAmount == 0) {
                return false;
            } else return true;
        }
        catch(SQLException e){
            log.error("BookExists SQL exception");
            return false;
        }
    }

    public void removeBook(String bookName){
        try {
            if (bookExists(bookName)) {
                statement = connection.createStatement();
                SQL = "UPDATE BOOKS SET isDeleted = 'True' WHERE bookName = '" + bookName + "'";
                resultSet = statement.executeQuery(SQL);
                UserWindow.history.addToHistory("User " + UserWindow.id + " removed book " + bookName + "\n");
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

    public void nameSearch(String bookName){
        try{
            statement = connection.createStatement();
            SQL = "SELECT bookName FROM Books WHERE bookName LIKE '%" + bookName + "%'";
            resultSet = statement.executeQuery(SQL);
            while(resultSet.next())
                log.info(resultSet.getString(1));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void authorSearch(String author){
        try{
            statement = connection.createStatement();
            SQL = "SELECT bookName, (SELECT name FROM Authors WHERE authorID = Books.authorID), (SELECT lastname FROM Authors WHERE authorID = Books.authorID) \n" +
                    "    FROM Books\n" +
                    "    WHERE (SELECT name FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%' OR " + "" +
                    "   (SELECT lastname FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%'";
            resultSet = statement.executeQuery(SQL);
            while(resultSet.next())
                log.info(resultSet.getString(1) + " by " + resultSet.getString(2) + " " + resultSet.getString(3));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void ISBNSearch(String ISBN){
        try{
            statement = connection.createStatement();
            SQL = "SELECT bookName, ISBN FROM Books WHERE ISBN LIKE '%" + ISBN + "%'";
            resultSet = statement.executeQuery(SQL);
            while(resultSet.next())
                log.info(resultSet.getString(1) + " - " + resultSet.getString(2));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void yearSearch(int min, int max){
        try{
            statement = connection.createStatement();
            SQL = "SELECT bookName, releaseYear FROM Books WHERE releaseYear > " + min + " AND releaseyear < " + max;
            resultSet = statement.executeQuery(SQL);
            while(resultSet.next())
                log.info(resultSet.getString(1) + " - " + resultSet.getInt(2));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void multipleSearch(int min, int max, int pageAmount, String author){
        try{
            statement = connection.createStatement();
            SQL = "SELECT * FROM Books WHERE releaseYear > " + min + " AND releaseyear < " + max  + " AND pageCount = " + pageAmount + " AND\n" +
                    "                ((SELECT name FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author
                    + "%' OR (SELECT lastname FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%')";
            resultSet = statement.executeQuery(SQL);
            while(resultSet.next())
                log.info(resultSet.getString(1) + " - " + resultSet.getString(2));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }
}
