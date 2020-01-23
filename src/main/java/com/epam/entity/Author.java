package com.epam.entity;

import com.epam.dao.AuthorDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Author {
    AuthorDAO authorDAO;
    String authorName;
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public Author(){
        authorDAO = new AuthorDAO();
        scanner = new Scanner(System.in);
    }

    public void listOfAuthors(){
        authorDAO.listOfAuthors();
    }

    public void addAuthor(){
        log.info("Author FN/SN/LN: ");
        authorName = scanner.nextLine();
        authorDAO.addAuthor(authorName);
    }

    public void removeAuthor(){
        log.info("Author FN/SN/LN: ");
        authorName = scanner.nextLine();
        authorDAO.removeAuthor(authorName);
    }
}
