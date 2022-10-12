package com.example.robotics.eventloop.opmode;

import com.example.robotics.userInterface.MainActivity;

public abstract class LinearOpModeV2 extends MainActivity {
    boolean startLoop = true;

    abstract public void runOpMode() throws InterruptedException;

    public void loop() throws InterruptedException{
        while( startLoop ){
            try{
                runOpMode();
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }

        }
    }

    public void runOpMode2() throws InterruptedException{

    }

    public void startLoop(){
        this.startLoop=true;
    }

    public void stopLoop(){
        this.startLoop = false;
    }

    public boolean isLoopStarted(){
        return this.startLoop;
    }


}
