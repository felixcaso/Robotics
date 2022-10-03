package com.example.robotics.hardware;

import com.example.robotics.Telemetry;
import com.example.robotics.hardware.HardwareMap;

public class DcMotor extends HardwareMap{

    //Attributes
    private String motorName;
    private Ports port;
    private Direction direction;
    private double power;


    //Accessible  Enums to choose
    public enum Direction{
        FORWARD,REVERSE
    }

    public DcMotor(String name, Ports port ){
        this.motorName = name;
        this.port = port;
        //addData();

    }

    //Methods (setters and getters )
     public void setMotorName(String name){
        this.motorName = name;
     }
     public String getMotorName(){
        return this.motorName;
     }


     public void setDirection(Direction dir){
        this.direction = dir;
     }
     public Direction getDirection(){
        return this.direction;
     }


    public void setPower(double pwr){
        this.power = pwr;
    }
    public double getPower(){
        return this.power;
    }

    public void setPort(Ports port){
        this.port = port;
    }

    public HardwareMap.Ports getPort(){
        return this.port;
    }



}
