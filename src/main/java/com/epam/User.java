package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int userID;
    String name;
    String secondName;
    String lastName;
    boolean isAdmin;
    boolean isActive;
    private static final Logger log = LogManager.getLogger();

    public User(){
        name = "John";
        secondName = null;
        lastName = "Doe";
        isAdmin = false;
        isActive = false;
    }

    public User(String username){
        Connection connection = UserWindow.connection;
        String SQL = UserWindow.SQL;
        PreparedStatement pst;
        ResultSet rs;
        try {
            UserWindow.SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            userID = rs.getInt(1);
            name = rs.getString(2);
            secondName = rs.getString(3);
            lastName = rs.getString(4);
            isAdmin = rs.getString(7).equals("Admin");
            isActive = rs.getString(8).equals("Active");
        }
        catch(SQLException e){
            log.error("User constructor exception," + e);
        }
    }
}
