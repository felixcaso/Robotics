package com.example.robotics.hardware;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HardwareMap {
    public enum Ports{
        PORT1,PORT2,PORT3,PORT4,PORT5,
        PORT6,PORT7,PORT8,PORT9,PORT10,
    }

    public static class DeviceMapping<DEVICE_TYPE> implements Iterable<DEVICE_TYPE> {

        private Map<String, DEVICE_TYPE> map = new HashMap<String, DEVICE_TYPE>();

        public DEVICE_TYPE get(String deviceName) {
            DEVICE_TYPE device = map.get(deviceName);
            if (device == null) {
                String msg = String.format("Unable to find a hardware device with the name \"%s\"", deviceName);
                throw new IllegalArgumentException(msg);
            }
            return device;
        }

        public void put(String deviceName, DEVICE_TYPE device) {
            map.put(deviceName, device);
        }

        public Iterator<DEVICE_TYPE> iterator() {
            return map.values().iterator();
        }

        public Set<Map.Entry<String, DEVICE_TYPE>> entrySet() {
            return map.entrySet();
        }

        public int size() {
            return map.size();
        }
    }

    public Context appContext;
    public DeviceMapping<Servo> servo = new DeviceMapping<Servo>();
    public DeviceMapping<DcMotor> dcMotor = new DeviceMapping<DcMotor>();

//    public DeviceMapping<DcMotorController> dcMotorController = new DeviceMapping<DcMotorController>();
//    public DeviceMapping<ServoController> servoController = new DeviceMapping<ServoController>();
//    public DeviceMapping<LegacyModule> legacyModule = new DeviceMapping<LegacyModule>();
//    public DeviceMapping<AccelerationSensor> accelerationSensor = new DeviceMapping<AccelerationSensor>();
//    public DeviceMapping<CompassSensor> compassSensor = new DeviceMapping<CompassSensor>();
//    public DeviceMapping<GyroSensor> gyroSensor = new DeviceMapping<GyroSensor>();
//    public DeviceMapping<IrSeekerSensor> irSeekerSensor = new DeviceMapping<IrSeekerSensor>();
//    public DeviceMapping<LightSensor> lightSensor = new DeviceMapping<LightSensor>();
//    public DeviceMapping<UltrasonicSensor> ultrasonicSensor = new DeviceMapping<UltrasonicSensor>();
//    public DeviceMapping<VoltageSensor> voltageSensor = new DeviceMapping<VoltageSensor>();



}






