package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;


public class User {
    int userID;
    String name;
    String secondName;
    String lastName;
    boolean isAdmin;
    boolean isActive;
    UserDAO userDAO;
    private static final Logger log = LogManager.getLogger();


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

    public void addAuthor(String author){
        userDAO = new UserDAO();
        userDAO.addAuthor(author);
    }

    public void removeBook(String bookName){
        userDAO = new UserDAO();
        userDAO.removeBook(bookName);
    }

    public void removeAuthor(String author){
        userDAO = new UserDAO();
        userDAO.removeAuthor(author);
    }

    public void listOfBooks(){
        userDAO = new UserDAO();
        userDAO.listOfBooks();
    }

    public void listOfAuthors(){
        userDAO = new UserDAO();
        userDAO.listOfAuthors();
    }

    public void addBookmark(){
        userDAO = new UserDAO(userID);
        userDAO.addBookMark();
    }
}
