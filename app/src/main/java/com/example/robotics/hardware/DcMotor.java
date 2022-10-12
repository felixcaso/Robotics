package com.example.robotics.hardware;

public class DcMotor implements HardwareDevice {

    //Attributes
    private String motorName;
    private double power;
    private int port;

        /***Constructors***/
    public DcMotor(String name, int port) {
        this.motorName = name;
        this.port = port;
    }

    public DcMotor(String name){
        this.motorName = name;
    }

    public DcMotor(int port){
        this.motorName="motor";
        this.port = port;
    }

    public DcMotor(){
        this.motorName = "";
        this.port=-1;
    }


        /*** Methods ***/
     public void setName(String name){
        this.motorName = name;
     }
     public String getName(){
        return this.motorName;
     }


     public void setPower(double pwr){
        this.power = pwr;
    }
     public double getPower(){
        return this.power;
    }

     public void setPort(int port){
        this.port = port;
    }
     public int getPort(){
        return this.port;
    }




}//end class
