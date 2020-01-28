package com.epam.dto;

public class AuthorJSON {
    String name;
    String secondName;
    String lastName;
    String dob;

    public AuthorJSON(String name, String secondName, String lastName, String dob){
        this.name = name;
        this.secondName = secondName;
        this.lastName = lastName;
        this.dob = dob;
    }
}
