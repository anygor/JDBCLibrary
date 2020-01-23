package com.epam.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO extends UserDAO{
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public void addUser(){
        scanner = new Scanner(System.in);
        try{
            String name;
            String secondName;
            String lastName;
            String username;
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
            log.info("Username will be " + username);
            statement = connection.createStatement();
            SQL = "INSERT INTO \"JDBC\".\"USERS\" (USERID, NAME, SECONDNAME, LASTNAME, LOGIN, PASSWORD, ROLE, STATUS) VALUES " +
                    "(" + (currentMaxUserID() + 1) + ", '" + name + "', '" + secondName + "', '" + lastName + "', '" + username + "', '0000', 'User', 'Active')";
            resultSet = statement.executeQuery(SQL);
            log.info("Done");
            close();
        }
        catch(SQLException e){
            log.error("User addition SQL error");
        }
    }

    public void blockUser(){
        scanner = new Scanner(System.in);
        log.info("username to block:");
        String username = scanner.nextLine();
        try{
            if(userExists(username)){
                statement = connection.createStatement();
                SQL = "UPDATE Users SET status = 'Blocked' WHERE login = '" + username + "'";
                resultSet = statement.executeQuery(SQL);
                log.info("User blocked");
                close();
            }
            else log.info("no such user");
        }
        catch(SQLException e){
            log.error("blocusersqlexception");
        }
    }

    private boolean userExists(String username){
        try{
            statement = connection.createStatement();
            SQL = "SELECT COUNT(*) FROM Users WHERE login = '" + username + "'";
            resultSet = statement.executeQuery(SQL);
            resultSet.next();
            if (resultSet.getInt(1) == 0)
                return false;
            else return true;
        }
        catch(SQLException e) {
            log.error("userExistsSQLException");
            return false;
        }
    }

    private int currentMaxUserID(){
        int id;
        try{
            statement = connection.createStatement();
            SQL = "SELECT userID FROM Users ORDER BY userID DESC";
            try(ResultSet resultSet = statement.executeQuery(SQL)){
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
