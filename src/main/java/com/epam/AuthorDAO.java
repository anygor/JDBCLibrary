package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorDAO {

    Connection connection;
    String SQL;
    PreparedStatement pst;
    ResultSet rs;
    private static final Logger log = LogManager.getLogger();

    public AuthorDAO(){
        connection = UserWindow.connection;
    }

    public void listOfAuthors(){
        try{
            SQL = "SELECT * FROM Authors WHERE isDeleted = 'False'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            while(rs.next()){
                if(!rs.getString(3).equals("null")) {
                    log.info(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
                }
                else log.info(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getString(4));
            }
        }
        catch(SQLException e){
            log.error("listOfAuthors exception");
        }
    }

    public boolean authorExists(String author){
        String[] authorStrings = author.split(" ");
        String authorFirstName;
        String authorSecondName;
        String authorLastName;
        if(authorStrings.length == 2){
            authorFirstName = authorStrings[0];
            authorLastName = authorStrings[1];
        }
        else{
            authorFirstName = authorStrings[0];
            authorLastName = authorStrings[2];
        }
        try {
            connection = UserWindow.connection;
            SQL = "SELECT COUNT(*) FROM Authors WHERE name = '" + authorFirstName + "' AND lastName = '" + authorLastName + "'";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            rs.next();
            if(rs.getInt(1) == 0){
                log.info("No such author, author will be added");
                return false;
            }
            else {
                log.info("Author is already in the library");
                return true;
            }
        }
        catch(SQLException e){
            log.error("AuthorExistsCheckError");
            return false;
        }
    }
}
