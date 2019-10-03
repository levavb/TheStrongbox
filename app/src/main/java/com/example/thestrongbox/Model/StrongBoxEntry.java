package com.example.thestrongbox.Model;

public class StrongBoxEntry {
    private String Email, note, url;
    private String password;



    // default constructor
    public StrongBoxEntry() {
    }

    // constructor
    public StrongBoxEntry(String userName, String note, String password,String Url) {
        this.Email = userName;
        this.note = note;
        this.password = password;
        this.url = Url;

    }

}
