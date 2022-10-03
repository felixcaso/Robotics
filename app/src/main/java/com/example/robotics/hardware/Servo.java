package com.example.robotics.hardware;

public class Servo {
    //Attributes
    private String name;
    private int position;
    private boolean isEnabled;
    private int servoPort;

    public Servo(String name,int port){
        this.name = name;
        this.servoPort = port;
    }

    //Methods (setters and getters )
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }


    public void setPosition(int pos){
        this.position = pos;
    }
    public int getPosition(){
        return this.position;
    }


    public void setPort(int port){
        this.servoPort = port;
    }
    public int getPort(){
        return this.servoPort;
    }



}
