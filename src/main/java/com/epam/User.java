package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;


public class User {
    private int userID;
    String name;
    String secondName;
    String lastName;
    boolean isAdmin;
    boolean isActive;
    Scanner stringScanner;
    Scanner numberScanner;
    UserDAO userDAO;
    private static final Logger log = LogManager.getLogger();

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;

    public User(){
        name = null;
        secondName = null;
        lastName = null;
        isAdmin = false;
        isActive = false;
    }

    public User(String username){
        userDAO = new UserDAO();
        userDAO.setUser(this, username);
    }

    public void addBook(){
        userDAO = new UserDAO();
        userDAO.addBook();
    }
}
