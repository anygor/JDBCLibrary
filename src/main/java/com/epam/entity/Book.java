package com.epam.entity;

import com.epam.dao.BookDAO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Properties;
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
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Book(){
        bookDAO = new BookDAO();
        scanner = new Scanner(System.in);
    }

    public Book(String bookName, int releaseYear, String author, int pageCount, String publisher){
        this.bookName = bookName;
        this.releaseYear = releaseYear;
        this.author = author;
        this.pageCount = pageCount;
        this.publisher = publisher;
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

    public void addBook(String bookName, int releaseYear, String author, int pageCount, String publisher){
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

    public void bookJson() {
        BufferedReader in = null;
        StringBuilder json = new StringBuilder("");
        try {
            Properties property = new Properties();
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
            property.load(fileInputStream);
            in = new BufferedReader(new FileReader((property.getProperty("db.catalogJSON"))));
            String buffer;
            while ((buffer = in.readLine()) != null) {
                json.append(buffer);
            }
            log.info(json);
            String[] array = json.toString().split("}, {2}\\{");
            for (String s : array) {
                parseBooks(s);
            }
        }
         catch (FileNotFoundException e) {
            log.error("bookjson exception");
        }
        catch(IOException e){
            log.error(e);
        }
    }

    private void parseBooks(String json){
        String _bookName = json.substring(json.indexOf("\"bookName\": \"") + 13, json.indexOf("\",    \"releaseYear\":"));
        int _releaseYear = Integer.parseInt(json.substring(json.indexOf("\"releaseYear\": ") + 15, json.indexOf(",    \"pageCount\"")));
        String _author;
        String name = json.substring(json.indexOf("\"name\": \"") + 9, json.indexOf("\",      \"secondName\": "));
        String secondName = json.substring(json.indexOf("\"secondName\": \"") + 15, json.indexOf("\",      \"lastName\":"));
        String lastName = json.substring(json.indexOf("\"lastName\": \"") + 13, json.indexOf("\",      \"dob\""));
        if(!secondName.equals("")) {
            _author = name + " " + secondName + " " + lastName;
        }
        else{
            _author = name + " " + lastName;
        }
        int _pageCount = Integer.parseInt(json.substring(json.indexOf("\"pageCount\": ") + 13, json.indexOf(",    \"ISBN\"")));
        String _publisher = json.substring(json.indexOf("\"publisher\": \"") + 14, json.indexOf("\",    \"author\""));

        String _dob = json.substring(json.indexOf("\"dob\": \"") + 8, json.indexOf("\"    }"));

        log.info(_bookName);
        log.info(_releaseYear);
        log.info(_author);
        log.info(_pageCount);
        log.info(_publisher);
        log.info(_dob);
        addBook(_bookName, _releaseYear, _author, _pageCount, _publisher);
    }

    public void csv(){
        try {
            Properties property = new Properties();
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
            property.load(fileInputStream);
            BufferedReader in = new BufferedReader(new FileReader(property.getProperty("db.catalogCSV")));
            CSVParser csvParser = new CSVParser(in,CSVFormat.DEFAULT.
                    withHeader("Book Name", "Release Year", "Page Amount", "Publisher", "Last Name", "First Name", "Second Name", "Date of Birth"));
            for(CSVRecord csvRecord : csvParser){
                String _bookName = csvRecord.get("Book Name");
                if(_bookName.equals("Book Name")) continue;  // Костыль жоский, но ничего другого в голову не пришло
                int _releaseYear = Integer.parseInt(csvRecord.get("Release Year"));
                int _pageAmount = Integer.parseInt(csvRecord.get("Page Amount"));
                String _publisher = csvRecord.get(4);
                String _authorName;
                if(csvRecord.get(7).equals("")) {
                    _authorName = csvRecord.get(6) + " " + csvRecord.get(5);
                }
                else _authorName = csvRecord.get(6) + " " + csvRecord.get(7) + " " + csvRecord.get(5);
                String _dob = csvRecord.get(8);
                log.info(_bookName);
                log.info(_releaseYear);
                log.info(_authorName);
                log.info(_pageAmount);
                log.info(_publisher);
                log.info(_dob);
                addBook(_bookName, _releaseYear, _authorName, _pageAmount, _publisher);
            }
        }
        catch (FileNotFoundException e){
            log.error("filenotfoundcsv");
        }
        catch (IOException e) {
            log.error("ioexception csv");
        }
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
