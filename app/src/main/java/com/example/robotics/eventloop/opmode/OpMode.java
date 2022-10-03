package com.example.robotics.eventloop.opmode;



import com.example.robotics.hardware.Gamepad;
import com.example.robotics.hardware.HardwareMap;
import com.example.robotics.Telemetry;

import java.util.concurrent.TimeUnit;

/**
 * Base class for user defined operation modes (op modes).
 */
public abstract class OpMode {

    /**
     * Gamepad 1
     */
    public Gamepad gamepad1 = new Gamepad();

    /**
     * Gamepad 2
     */
    public Gamepad gamepad2 = new Gamepad();

    /**
     * Telemetry Data
     */
    public Telemetry telemetry = new Telemetry();

    /**
     * Hardware Mappings
     */
    public HardwareMap hardwareMap = new HardwareMap();

    /**
     * number of seconds this op mode has been running, this is
     * updated before every call to loop.
     */
    public double time = 0.0;

    // internal time tracking
    private long startTime = 0; // in nanoseconds

    /**
     * OpMode constructor
     * <p>
     * The op mode name should be unique. It will be the name displayed on the driver station. If
     * multiple op modes have the same name, only one will be available.
     */
    public OpMode() {
        startTime = System.nanoTime();
    }

    /**
     * User defined start method
     * <p>
     * This method will be called when this op mode is first enabled
     */
    abstract public void start();

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    abstract public void loop();

    /**
     * User defined stop method
     * <p>
     * This method will be called when this op mode is first disabled
     */
    abstract public void stop();

    /**
     * Get the number of seconds this op mode has been running
     * <p>
     * This method has sub millisecond accuracy.
     * @return number of seconds this op mode has been running
     */
    public double getRuntime() {
        final double NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);
        return (System.nanoTime() - startTime) / NANOSECONDS_PER_SECOND;
    }

}