package com.epam.entity;


import com.epam.UserWindow;
import com.epam.dao.AdminDAO;

public class Admin extends User {
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

    public void blockUser(){
        adminDAO = new AdminDAO();
        adminDAO.blockUser();
    }

    public void showHistory(){
        UserWindow.history.showHistory();
    }
}
