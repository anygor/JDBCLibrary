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

    public UserDAO(){
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

        if(bookExists(bookName)){
            log.info("Book is already in your library");
        }
        else {
            try {
                SQL = "INSERT INTO \"JDBC\".\"BOOKS\" (BOOKNAME, RELEASEYEAR, AUTHORID, PAGECOUNT, ISBN, PUBLISHERID, ISDELETED) VALUES " +
                        "('" + bookName + "', '" + releaseYear + "', '" + authorID + "', '" + pageCount + "', 'ISBN" + "', '" + 1 + "', 'False')";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
            } catch (SQLException e) {
                log.error("Add book sql mistake");
            }
        }
    }

    private boolean authorExists(String author){
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
        try {
            connection = UserWindow.connection;
            SQL = "SELECT COUNT(*) FROM Authors WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            if(rs.getInt(1) == 0){
                log.info("No such author, author will be added");
                return false;
            }
            else {
                log.info("Author is already in the library");
                return true;
            }
        }
        catch(SQLException e){
            log.error("AuthorExistsCheckError");
            return false;
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
            if(authorExists(author)){
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
        if(authorExists(author)){
            log.info("Author exists");
        }
        else{
            String[] authorStrings = author.split(" ");
            String authorFirstName;
            String authorSecondName;
            String authorLastName;
            log.info("Author should have a birthdate, please enter one (DD-MMM-YYYY):");
            String dob = "20-AUG-1999";
            //String dob = stringScanner.nextLine();
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
}
