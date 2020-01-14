package com.epam;

public class Admin extends User {
    public Admin(User user){
        name = user.name;
        secondName = user.secondName;
        lastName = user.lastName;
        isAdmin = true;
        isActive = user.isActive;
    }
}
