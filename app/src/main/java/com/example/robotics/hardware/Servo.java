package com.example.robotics.hardware;

public class Servo implements HardwareDevice{

    //Attributes
    private String name;
    private int port;

    public Servo(String name){
        this.name = name;
        this. port = -1;
    }
    public Servo(String name, int port){
        this.name = name;
        this.port = port;
    }
    public Servo(int port){
        this.name = "servo";
        this.port = port;
    }
    public Servo(){
        this.name = "servo";
        this.port = -1;
    }

    //Methods (setters and getters )
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setPort(int port){
        this.port = port;
    }
    public int getPort(){
        return this.port;
    }



}
