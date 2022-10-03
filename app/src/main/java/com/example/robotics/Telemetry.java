package com.example.robotics;

import java.util.HashMap;

public class Telemetry{
    //Attributes
    private HashMap<String,Float> dataNumbers;
    private HashMap<String,String> dataStrings;


    public Telemetry(){
        this.dataNumbers = new HashMap<>();
        this.dataStrings = new HashMap<>();
    }

    public void addData(String key, float val){
        dataNumbers.put(key,val);
    }

    public void addData(String key, double val){
        dataNumbers.put(key,(float)val);
    }

    public void addData(String key, int val){
        dataNumbers.put(key, (float)val);
    }

    public void addData(String key, String val){
        dataStrings.put(key,val);
    }

    public boolean hasData() {
        return (!dataStrings.isEmpty() || !dataNumbers.isEmpty());
    }

    public void clearData() {
        dataStrings.clear();
        dataNumbers.clear();
    }

    public void update(){
        String msg = dataNumbers.toString();
        System.out.println("Update function: "+msg);
    }

    public HashMap<String,String> getDataStrings(){
        return this.dataStrings;
    }

    public HashMap<String,Float> getDataNumbers(){
        return this.dataNumbers;
    }





}
