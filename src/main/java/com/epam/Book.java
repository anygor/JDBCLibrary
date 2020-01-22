package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Book {
    BookDAO bookDAO;
    String bookName;
    int releaseYear;
    String author;
    int pageCount;
    String publisher;
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public Book(){
        bookDAO = new BookDAO();
        scanner = new Scanner(System.in);
    }

    public void addBook(){
        log.info("addBook method called.");
        log.info("Book Name: ");
        bookName = scanner.nextLine();
        log.info("Release year: ");
        releaseYear = scanner.nextInt();
        scanner = new Scanner(System.in);
        log.info("Author (FN/SN/LN): ");
        author = scanner.nextLine();
        log.info("Amount of pages: ");
        pageCount = scanner.nextInt();
        scanner = new Scanner(System.in);
        log.info("Publisher: ");
        publisher = scanner.nextLine();
        bookDAO.addBook(bookName, releaseYear, author, pageCount, publisher);
    }

    public void removeBook(){
        log.info("Book name:");
        bookName = scanner.nextLine();
        bookDAO.removeBook(bookName);
    }

    public void listOfBooks(){
        bookDAO.listOfBooks();
    }
}
