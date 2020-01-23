package com.epam.entity;

import com.epam.dao.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class User {
    public int userID;
    public String name;
    public String secondName;
    public String lastName;
    public boolean isAdmin;
    public boolean isActive;
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
}
