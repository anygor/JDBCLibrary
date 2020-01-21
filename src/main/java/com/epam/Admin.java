package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Admin extends User {
    private static final Logger log = LogManager.getLogger();
    AdminDAO adminDAO;
    public Admin(User user){
        userID = user.userID;
        name = user.name;
        secondName = user.secondName;
        lastName = user.lastName;
        isAdmin = true;
        isActive = user.isActive;
    }

    public void addUser(){
        adminDAO = new AdminDAO();
        adminDAO.addUser();
    }
}
