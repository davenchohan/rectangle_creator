package com.example;

public class Rectangle {
    private String name;
    private String colour;
    private int width;
    private int height;
    private String id;

    public String getName() {
        return this.name;
    }

    public String getColour(){
        return this.colour;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getId(){
        return this.id;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setColour(String c){
        this.colour = c;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public void setId(String i){
        this.id = i;
    }
}