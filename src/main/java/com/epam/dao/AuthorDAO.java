package com.epam.dao;

import com.epam.UserWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthorDAO {

    Connection connection;
    String SQL;
    private static final Logger log = LogManager.getLogger();

    public AuthorDAO(){
        connection = UserWindow.connection;
    }

    public void listOfAuthors(){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT * FROM Authors WHERE isDeleted = 'False'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                while (resultSet.next()) {
                    if (!resultSet.getString(3).equals("null")) {
                        log.info(resultSet.getInt(1) + ". " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4));
                    } else log.info(resultSet.getInt(1) + ". " + resultSet.getString(2) + " " + resultSet.getString(4));
                }
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
        try(Statement statement = connection.createStatement()) {
            SQL = "SELECT COUNT(*) FROM Authors WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "' AND isDeleted = 'False'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                resultSet.next();
                if (resultSet.getInt(1) == 0) {
                    log.info("No such author, author will be added");
                    return false;
                } else {
                    log.info("Author is already in the library");
                    return true;
                }
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
                try (Statement statement = connection.createStatement()) {
                    SQL = "SELECT MAX(authorid) FROM Authors";
                    try(ResultSet resultSet = statement.executeQuery(SQL)) {
                        resultSet.next();
                        currentMaxID = resultSet.getInt(1);
                    }
                    SQL = "INSERT INTO \"JDBC\".\"AUTHORS\" (AUTHORID, NAME, SECONDNAME, LASTNAME, DOB, ISDELETED) VALUES " +
                            "('" + (currentMaxID + 1) + "', '" + authorFirstName + "', '" + authorSecondName + "', '" + authorLastName + "', '" + dob + "', '" + "False')";
                    try(ResultSet resultSet = statement.executeQuery(SQL)) {
                        UserWindow.history.addToHistory("User " + UserWindow.id + " added author " + authorFirstName + " " + authorLastName + "\n");
                        log.info("author added");
                    }
                }
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
            try(Statement statement = connection.createStatement()) {
                SQL = "UPDATE Books SET isDeleted = 'True' WHERE authorID = " + authorID(author);
                try(ResultSet resultSet = statement.executeQuery(SQL)) {
                    SQL = "UPDATE Authors SET isDeleted = 'True' WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "'";
                }
                 try(ResultSet resultSet = statement.executeQuery(SQL)){
                    UserWindow.history.addToHistory("User " + UserWindow.id + " removed author " + authorFirstName + " " + authorLastName + "\n");
                    log.info("Author " + authorFirstName + " " + authorLastName + " removed");
                }
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
        int id;
        if(authorStrings.length == 2){
            authorFirstName = authorStrings[0];
            authorLastName = authorStrings[1];
        }
        else{
            authorFirstName = authorStrings[0];
            authorLastName = authorStrings[2];
        }
        try{
            if(authorExists(author)) {
                try (Statement statement = connection.createStatement()) {
                    SQL = "SELECT authorID FROM Authors WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "' AND isDeleted = 'False'";
                    try(ResultSet resultSet = statement.executeQuery(SQL)) {
                        resultSet.next();
                        id = resultSet.getInt(1);
                        return id;
                    }
                }
            }
            else{
                try(Statement statement = connection.createStatement()) {
                    SQL = "SELECT MAX(authorid) FROM AUTHORS;";
                    try(ResultSet resultSet = statement.executeQuery(SQL)){
                        id = resultSet.getInt(1) + 1;
                        return id;
                    }
                }
            }
        }
        catch(SQLException e){
            return -1;
        }
    }
}
