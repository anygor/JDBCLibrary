package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthorDAO {

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
    private static final Logger log = LogManager.getLogger();

    public AuthorDAO(){
        connection = UserWindow.connection;
    }

    public void listOfAuthors(){
        try{
            SQL = "SELECT * FROM Authors WHERE isDeleted = 'False'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next()){
                if(!rs.getString(3).equals("null")) {
                    log.info(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
                }
                else log.info(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getString(4));
            }
        }
        catch(SQLException e){
            log.error("listOfAuthors exception");
        }
    }

    public boolean authorExists(String author){
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
            SQL = "SELECT COUNT(*) FROM Authors WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "' AND isDeleted = 'False'";
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

    public void addAuthor(String author){
        Scanner stringScanner = new Scanner(System.in);
        if(authorExists(author)){
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

    public void removeAuthor(String author){
        if(!authorExists(author)){
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

    public int authorID(String author){
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

}
