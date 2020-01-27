package com.epam.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;
import sun.security.provider.MD5;

public class AdminDAO extends UserDAO{
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public void addUser(){
        scanner = new Scanner(System.in);
        try(Statement statement = connection.createStatement()){
            String name;
            String secondName;
            String lastName;
            String username;
            String password;
            log.info("Adding new user");
            log.info("First Name:");
            name = scanner.nextLine();
            log.info("Second name (optional):");
            secondName = scanner.nextLine();
            log.info("Last Name:");
            lastName = scanner.nextLine();
            if (!secondName.equals(""))
                username = (name.substring(0, 1) + secondName.substring(0, 1) + lastName).toLowerCase();
            else {
                username = (name.substring(0, 1) + lastName).toLowerCase();
                secondName = "null";
            }
            log.info("Digit user's password:");
            password = scanner.nextLine();
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(password.getBytes());
                byte[] digest = messageDigest.digest();
                password = DatatypeConverter.printHexBinary(digest).toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                log.error(e);
            }
            log.info("Username will be " + username);
            SQL = "INSERT INTO \"JDBC\".\"USERS\" (USERID, NAME, SECONDNAME, LASTNAME, LOGIN, PASSWORD, ROLE, STATUS) VALUES " +
                    "(" + (currentMaxUserID() + 1) + ", '" + name + "', '" + secondName + "', '" + lastName + "', '" + username + "', '" + password + "', 'User', 'Active')";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                log.info("Done");
            }
        }
        catch(SQLException e){
            log.error("User addition SQL error");
        }
    }

    public void blockUser(){
        scanner = new Scanner(System.in);
        log.info("username to block:");
        String username = scanner.nextLine();
        try(Statement statement = connection.createStatement()){
            if(userExists(username)){
                SQL = "UPDATE Users SET status = 'Blocked' WHERE login = '" + username + "'";
                try(ResultSet resultSet = statement.executeQuery(SQL)) {
                    log.info("User blocked");
                }
            }
            else log.info("no such user");
        }
        catch(SQLException e){
            log.error("blocusersqlexception");
        }
    }

    private boolean userExists(String username){
        try(Statement statement = connection.createStatement()){
            SQL = "SELECT COUNT(*) FROM Users WHERE login = '" + username + "'";
            try(ResultSet resultSet = statement.executeQuery(SQL)) {
                resultSet.next();
                return resultSet.getInt(1) != 0;
            }
        }
        catch(SQLException e) {
            log.error("userExistsSQLException");
            return false;
        }
    }

    private int currentMaxUserID(){
        int id;
        try{
            SQL = "SELECT userID FROM Users ORDER BY userID DESC";
            try(Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(SQL)){
                resultSet.next();
                id = resultSet.getInt(1);
                return id;
            }

        }
        catch(SQLException e){
            log.error("currentmaxuserid sql");
            return -1;
        }
    }
}
