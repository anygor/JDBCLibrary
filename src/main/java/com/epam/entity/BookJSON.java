package com.epam.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class BookJSON {
    String bookName;
    int releaseYear;
    int pageCount;
    String ISBN;
    String publisher;
    AuthorJSON author;

    private static final Logger log = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public void json(){
        Properties property = new Properties();
        StringBuilder json = new StringBuilder("");
        BufferedReader in;
        String buffer;

        Book bookCache = new Book();
        String bookNameCache;
        int releaseYearCache;
        int pageCountCache;
        String publisherCache;
        String authorCache;

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
            property.load(fileInputStream);
            in = new BufferedReader(new FileReader((property.getProperty("db.catalogJSON"))));
            while ((buffer = in.readLine()) != null) {
                json.append(buffer);
            }
            BookJSON[] books = gson.fromJson(json.toString(), BookJSON[].class);
            for(int i = 0; i < books.length; i++){
                bookNameCache = books[i].bookName;
                releaseYearCache = books[i].releaseYear;
                pageCountCache = books[i].pageCount;
                publisherCache = books[i].publisher;
                if(books[i].author.secondName.equals("")){
                    authorCache = books[i].author.name + " " + books[i].author.lastName;
                }
                else authorCache = books[i].author.name + " " + books[i].author.secondName + " " + books[i].author.lastName;
                log.info(books[i].author.dob);
                bookCache.addBook(bookNameCache, releaseYearCache, authorCache, pageCountCache, publisherCache);
            }
        }
        catch(FileNotFoundException e) {
            log.error(e);
        }
        catch(IOException e){
            log.error(e);
        }
    }
    public BookJSON(){
        // it just works
    }

    public BookJSON(String bookName, int releaseYear, int pageCount, String ISBN, String publisher, AuthorJSON author){
        this.bookName = bookName;
        this.releaseYear = releaseYear;
        this.pageCount = pageCount;
        this.ISBN = ISBN;
        this.publisher = publisher;
        this.author = author;
    }
}
