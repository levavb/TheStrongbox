package com.example.thestrongbox.Model;

public class Account {
    private String userName, note, url, date, dataKey;

    // default constructor
    public Account() {
    }

    // constructor
    public Account(String dataKey, String userName, String note, String Url, String date) {
        this.userName = userName;
        this.note = note;
        this.url = Url;
        this.date = date;
        this.dataKey = dataKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
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

}
