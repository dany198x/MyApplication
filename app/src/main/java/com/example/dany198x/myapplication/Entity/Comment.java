package com.example.dany198x.myapplication.Entity;

public class Comment {
    private String username;
    private String imagePath;
    private float rate;
    private String content;
    private String date;

    public Comment(String username, String imagePath, float rate, String content, String date) {
        this.username = username;
        this.imagePath = imagePath;
        this.rate = rate;
        this.content = content;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }
    public String getImagePath() {
        return imagePath;
    }
    public float getRate() { return rate; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}
