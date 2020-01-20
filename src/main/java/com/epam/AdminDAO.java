package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Scanner;

public class AdminDAO extends UserDAO{
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public void addUser(){
        scanner = new Scanner(System.in);
        try{
            String name;
            String secondName;
            String lastName;
            String username;
            log.info("Adding new user: \n First Name:");
            name = scanner.nextLine();
            log.info("Second name (optional):");
            secondName = scanner.nextLine();
            log.info("Last Name:");
            lastName = scanner.nextLine();
            if (secondName != null)
                username = (name.substring(0, 1) + secondName.substring(0, 1) + lastName).toLowerCase();
            else username = (name.substring(0, 1) + lastName).toLowerCase();
            log.info("Username will be " + username);
            SQL = "INSERT INTO \"JDBC\".\"USERS\" (USERID, NAME, SECONDNAME, LASTNAME, LOGIN, PASSWORD, ROLE, STATUS) VALUES " +
                    "('2', '" + name + "', '" + secondName + "', '" + lastName + "', '" + username + "', '0000', 'User', 'Active')";
            pst = connection.prepareStatement(SQL);
            rs = pst.executeQuery();
            log.info("Done");
        }
        catch(SQLException e){
            log.error("User addition SQL error");
        }

    }
}
