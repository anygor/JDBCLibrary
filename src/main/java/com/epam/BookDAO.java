package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO {

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
    AuthorDAO authorDAO;
    private static final Logger log = LogManager.getLogger();

    public BookDAO(){
        connection = UserWindow.connection;
    }

    public void listOfBooks(){
        try{
            SQL = "SELECT bookName FROM Books WHERE isDeleted = 'False'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next()){
                log.info(rs.getString(1));
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

    public boolean bookExists(String bookName){
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

    public void nameSearch(String bookName){
        try{
            SQL = "SELECT bookName FROM Books WHERE bookName LIKE '%" + bookName + "%'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next())
                log.info(rs.getString(1));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void authorSearch(String author){
        try{
            SQL = "SELECT bookName, (SELECT name FROM Authors WHERE authorID = Books.authorID), (SELECT lastname FROM Authors WHERE authorID = Books.authorID) \n" +
                    "    FROM Books\n" +
                    "    WHERE (SELECT name FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%' OR " + "" +
                    "   (SELECT lastname FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next())
                log.info(rs.getString(1) + " by " + rs.getString(2) + " " + rs.getString(3));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void ISBNSearch(String ISBN){
        try{
            SQL = "SELECT bookName, ISBN FROM Books WHERE ISBN LIKE '%" + ISBN + "%'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next())
                log.info(rs.getString(1) + " - " + rs.getString(2));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void yearSearch(int min, int max){
        try{
            SQL = "SELECT bookName, releaseYear FROM Books WHERE releaseYear > " + min + " AND releaseyear < " + max;
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next())
                log.info(rs.getString(1) + " - " + rs.getInt(2));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }

    public void multipleSearch(int min, int max, int pageAmount, String author){
        try{
            SQL = "SELECT * FROM Books WHERE releaseYear > " + min + " AND releaseyear < " + max  + " AND pageCount = " + pageAmount + " AND\n" +
                    "                ((SELECT name FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author
                    + "%' OR (SELECT lastname FROM Authors WHERE authorID = Books.authorID) LIKE '%" + author + "%')";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next())
                log.info(rs.getString(1) + " - " + rs.getString(2));
        }
        catch(SQLException e){
            log.error("namesearch sql exception");
        }
    }
}
