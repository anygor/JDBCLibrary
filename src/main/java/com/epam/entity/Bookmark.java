package com.epam.entity;

import com.epam.dao.BookmarkDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Bookmark {
    BookmarkDAO bookmarkDAO;
    String bookName;
    int pageNum;
    Scanner scanner;
    private static final Logger log = LogManager.getLogger();

    public Bookmark(int userID){
        bookmarkDAO = new BookmarkDAO(userID);
        scanner = new Scanner(System.in);
    }

    public void myBookmarks(){
        bookmarkDAO.myBookmarks();
    }

    public void addBookmark(){
        bookmarkDAO.addBookmark();
    }

    public void removeBookmark(){
        log.info("BookName: ");
        bookName = scanner.nextLine();
        log.info("Page you want to remove your bookmark from: ");
        pageNum = scanner.nextInt();
        scanner = new Scanner(System.in);
        bookmarkDAO.removeBookmark(bookName, pageNum);
    }
}
