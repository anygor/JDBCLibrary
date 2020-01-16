package com.epam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Book {
    String bookName;
    int releaseYear;
    int authorID;
    int pageCount;
    String ISBN;
    int publisherID;
    private static final Logger log = LogManager.getLogger();

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;

    // Existing book from database
    public Book(String bookName){
        connection = UserWindow.connection;
        SQL = UserWindow.SQL;
        try {
            UserWindow.SQL = "SELECT * FROM Books WHERE bookName = '" + bookName + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            this.bookName = rs.getString(1);
            releaseYear = rs.getInt(2);
            authorID = rs.getInt(3);
            pageCount = rs.getInt(4);
            ISBN = rs.getString(5);
            publisherID = rs.getInt(6);
        }
        catch(SQLException e){
            log.error("User constructor exception," + e);
        }
    }
}
