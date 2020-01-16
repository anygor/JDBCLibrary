package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private int userID;
    String name;
    String secondName;
    String lastName;
    boolean isAdmin;
    boolean isActive;
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;

    public User(){
        name = "John";
        secondName = null;
        lastName = "Doe";
        isAdmin = false;
        isActive = false;
    }

    public User(String username){
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
        try {
            UserWindow.SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            userID = rs.getInt(1);
            name = rs.getString(2);
            secondName = rs.getString(3);
            lastName = rs.getString(4);
            isAdmin = rs.getString(7).equals("Admin");
            isActive = rs.getString(8).equals("Active");
        }
        catch(SQLException e){
            log.error("User constructor exception," + e);
        }
    }

    public void addBook(){
        String bookName;
        int releaseYear;
        String author;
        int pageCount;
        String publisher;
        int authorID;

        scanner = new Scanner(System.in);
        log.info("addBook method called.\n Book Name: ");
        bookName = scanner.nextLine();
        log.info("Release year: ");
        releaseYear = scanner.nextInt();
        log.info("Author (FN/SN/LN): ");
        author = scanner.nextLine();
        log.info("Amount of pages: ");
        pageCount = scanner.nextInt();
        log.info("Publisher: ");
        publisher = scanner.nextLine();

        int id = authorExistsID(author);
        if (id != -1) {
            authorID = id;
        }
        else {
            authorID = id;
        }

        try {
            UserWindow.SQL = "INSERT INTO \"JDBC\".\"BOOKS\" (BOOKNAME, RELEASEYEAR, AUTHORID, PAGECOUNT, ISBN, PUBLISHERID) VALUES " +
                    "('" + bookName + "', '" + releaseYear + "', '" + authorID + "', '" + pageCount + "', '" + 1 + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
        }
        catch(SQLException e){
            log.error("Add book sql mistake");
        }
    }

    private int authorExistsID(String author){
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
            UserWindow.SQL = "SELECT * FROM Authors WHERE (name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "')";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch(SQLException e){
            return -1;
        }
    }

    public void addAuthor(String author){
        if(authorExistsID(author)!= -1){
            log.info("Author exists");
        }
        else{
            String[] authorStrings = author.split(" ");
            String authorFirstName;
            String authorSecondName;
            String authorLastName;
            log.info("Author should have a birthdate, please enter one (DD-MMM-YYYY):");
            String dob = scanner.nextLine();
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
                UserWindow.SQL = "SELECT MAX(authorid) FROM AUTHORS;";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
                currentMaxID = rs.getInt(1);
                UserWindow.SQL = "INSERT INTO \"JDBC\".\"USERS\" (AUTHORID, NAME, SECONDNAME, LASTNAME, DOB) VALUES " +
                        "('" + (currentMaxID + 1) + "', '" + authorFirstName + "', '" + authorSecondName + "', '" + authorLastName + "', '" + dob + "'";
                pst = connection.prepareStatement(SQL);
                rs = pst.executeQuery();
            }
            catch(SQLException e){
                log.error("sql exception at addauthor");
            }
        }
    }
}
