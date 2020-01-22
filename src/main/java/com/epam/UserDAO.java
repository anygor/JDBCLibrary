package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private static final Logger log = LogManager.getLogger();

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
    AuthorDAO authorDAO;

    public UserDAO(){
        authorDAO = new AuthorDAO();
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
    }

    public void setUser(User user, String username){
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
        try {
            SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            user.userID = rs.getInt(1);
            user.name = rs.getString(2);
            user.secondName = rs.getString(3);
            user.lastName = rs.getString(4);
            user.isAdmin = rs.getString(7).equals("Admin");
            user.isActive = rs.getString(8).equals("Active");
        }
        catch(SQLException e){
            log.error("setUser exception, " + e);
        }
    }
}
