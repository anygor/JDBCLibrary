package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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

    public void addBookmark(){
        userDAO = new UserDAO(userID);
        userDAO.addBookmark();
    }

    public void myBookmarks(){
        userDAO = new UserDAO(userID);
        userDAO.myBookmarks();
    }

    public void removeBookmark(String bookName, int pageNum){
        userDAO = new UserDAO(userID);
        userDAO.removeBookmark(bookName, pageNum);
    }
}
