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

    public void search(){
        log.info("To search book by name, enter byName");
        log.info("To search book by author, enter byAuthor");
        log.info("To search book by ISBN, enter byISBN");
        log.info("To search book in range of years, enter byYears");
        log.info("To search book by range of years, amount of pages and partially by name, enter byMultiple");
        String command = scanner.nextLine();
        switch (command){
            case "byName":{
                log.info("Enter a line, the program will show all books that contain this line in their name");
                command = scanner.nextLine();
                bookDAO.nameSearch(command);
                break;
            }
            case "byAuthor":{
                log.info("Enter a line, the program will show all books that have an author with such line in their name");
                command = scanner.nextLine();
                bookDAO.authorSearch(command);
                break;
            }
            case "byISBN":{
                log.info("Enter ISBN");
                command = scanner.nextLine();
                bookDAO.ISBNSearch(command);
                break;
            }
            case "byYears":{
                log.info("The range will begin at year:");
                int min = scanner.nextInt();
                scanner = new Scanner(System.in);
                log.info("... and it will end at year:");
                int max = scanner.nextInt();
                scanner = new Scanner(System.in);
                bookDAO.yearSearch(min, max);
                break;
            }
            case "byMultiple":{
                log.info("Year range.");
                log.info("The range will begin at year:");
                int min = scanner.nextInt();
                scanner = new Scanner(System.in);
                log.info("... and it will end at year:");
                int max = scanner.nextInt();
                scanner = new Scanner(System.in);
                log.info("Amount of pages:");
                int pageAmount = scanner.nextInt();
                scanner = new Scanner(System.in);
                log.info("Part of author's name:");
                command = scanner.nextLine();
                bookDAO.multipleSearch(min, max, pageAmount, command);
                break;
            }
            default:{
                log.info("invalid command");
                break;
            }
        }
    }
}
