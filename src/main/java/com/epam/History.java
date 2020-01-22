package com.epam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class History {
    private static Queue<String> history;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static final Logger log = LogManager.getLogger();
    public History(){
        history = new LinkedList<>();
        fileHistoryInput();
        try {
            out = new BufferedWriter(new PrintWriter(new FileOutputStream("src/main/resources/historyText.txt", false)));
        }
        catch(FileNotFoundException e) {
            log.error(e);
        }

    }
    public static void fileHistoryInput(){
        try {
            in = new BufferedReader(new FileReader("src/main/resources/historyText.txt"));
            String buffer;
            while ((buffer = in.readLine()) != null) {
                history.add(buffer+'\n');
            }
        }
        catch(FileNotFoundException e){
            log.error(e);
        }
        catch(IOException e){
            log.error(e);
        }
    }
    public static void fileHistoryOutput(){
        try {
            while (!history.isEmpty()) {
                out.write(history.poll());
                out.flush();
            }
        }
        catch(IOException e){
            log.error(e);
        }
    }

    public void addToHistory(String s){
        history.add(s);
    }
    public void showHistory(){
        for (String message: history) {
            log.info(message);
        }
    }

}
