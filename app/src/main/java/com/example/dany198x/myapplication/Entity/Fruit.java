package com.example.dany198x.myapplication.Entity;

public class Fruit {
    private String id;
    private String name;
    private String imagePath;
    private float rate;
    private float avgCost;
    private String address;
    private String type;

    public Fruit(String id, String name, String imagePath, float rate, float avgCost, String address, String type) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.rate = rate;
        this.avgCost = avgCost;
        this.address = address;
        this.type = type;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getImagePath() {
        return imagePath;
    }
    public float getRate() { return rate; }
    public float getAvgCost() { return avgCost; }
    public String getAddress() { return address; }
    public String getType() { return type; }

}
