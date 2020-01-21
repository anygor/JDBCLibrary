package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO {

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
    private static final Logger log = LogManager.getLogger();

    public BookDAO(){
        connection = UserWindow.connection;
    }

    public void listOfBooks(){
        try{
            SQL = "SELECT bookName FROM Books WHERE isDeleted = 'False'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next()){
                log.info(rs.getString(1));
            }
        }
        catch(SQLException e){
            log.error("listOfBooks exception sql");
        }
    }

}
