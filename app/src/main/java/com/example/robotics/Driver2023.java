package com.example.robotics;

//import com.example.robotics.eventloop.opmode.LinearOpMode;
import com.example.robotics.eventloop.opmode.LinearOpMode;
import com.example.robotics.eventloop.opmode.LinearOpModeV2;
import com.example.robotics.hardware.DcMotor;
import com.example.robotics.hardware.HardwareDevice;
import com.example.robotics.hardware.Servo;
import com.example.robotics.userInterface.MainActivity;

public class Driver2023 extends LinearOpModeV2 {
    int i = 0;

    /**
     * Enter code below to control your robot
     * */
    @Override
    public void runOpMode(){

        System.out.println("********FROM OP-MODE******"+i);
        i++;

        if(i == 100){
            stopLoop();
        }

    }

}
