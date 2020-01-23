package com.epam.dao;

import com.epam.entity.User;
import com.epam.UserWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private static final Logger log = LogManager.getLogger();

    Connection connection;
    String SQL;
    Statement statement;
    ResultSet resultSet;
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
            statement = connection.createStatement();
            SQL = "SELECT * FROM Users WHERE login = '" + username + "'";
            resultSet = statement.executeQuery(SQL);
            resultSet.next();
            user.userID = resultSet.getInt(1);
            user.name = resultSet.getString(2);
            user.secondName = resultSet.getString(3);
            user.lastName = resultSet.getString(4);
            user.isAdmin = resultSet.getString(7).equals("Admin");
            user.isActive = resultSet.getString(8).equals("Active");
            UserWindow.id = user.userID;
            close();
        }
        catch(SQLException e){
            log.error("setUser exception, " + e);
        }
    }

    protected void close(){
        try {
            statement.close();
            resultSet.close();
        }
        catch (SQLException e){
            log.error("I didn't close sh*t");
        }
    }
}
