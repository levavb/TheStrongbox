package com.example.thestrongbox.Model;

public class Account {
    private String userName, note, url, password, date;

    // default constructor
    public Account() {
    }

    // constructor
    public Account(String userName, String note, String password, String Url, String date) {
        this.userName = userName;
        this.note = note;
        this.password = password;
        this.url = Url;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
