package com.example.robotics.hardware;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("WeakerAccess")
public class HardwareMap implements Iterable<HardwareDevice> {

    //------------------------------------------------------------------------------------------------
    // State
    //------------------------------------------------------------------------------------------------

    //Hardware
    public DeviceMapping<DcMotor> dcMotor = new DeviceMapping<DcMotor>(DcMotor.class);
    public DeviceMapping<Servo> servo = new DeviceMapping<Servo>(Servo.class);


    //Lists
    protected Map<String, List<HardwareDevice>> allDevicesMap         = new HashMap<String, List<HardwareDevice>>();
    protected List<HardwareDevice>              allDevicesList        = null;   // cache for iteration
    protected Map< HardwareDevice, Set<String>>  deviceNames           = new HashMap<HardwareDevice, Set<String>>();
    protected Map< HardwareDevice.HardwarePort, HardwareDevice> portNumberMap = new HashMap< HardwareDevice.HardwarePort, HardwareDevice>();

    public final List<DeviceMapping<? extends HardwareDevice>> allDeviceMappings;

    protected final Object lock = new Object();

    //------------------------------------------------------------------------------------------------
    // Construction
    //------------------------------------------------------------------------------------------------

    public HardwareMap() {

        this.allDeviceMappings = new ArrayList<DeviceMapping<? extends HardwareDevice>>(30);  // 30 is approximate
        this.allDeviceMappings.add(this.dcMotor);
        this.allDeviceMappings.add(this.servo);

    }

    //------------------------------------------------------------------------------------------------
    // Retrieval
    //------------------------------------------------------------------------------------------------

    /**
     * Retrieves the (first) device with the indicated name which is also an instance of the
     * indicated class or interface. If no such device is found, an exception is thrown. Example:
     *
     * <pre>
     *    DcMotor motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
     *    ColorSensor colorSensor = hardwareMap.get(ColorSensor.class, "myColorSensor");
     * </pre>
     *
     * @param classOrInterface  the class or interface indicating the type of the device object to be retrieved
     * @param deviceName        the name of the device object to be retrieved
     * @return a device with the indicated name which is an instance of the indicated class or interface
     * @see #get(String)
     * @see #getAll(Class)
//     * @see com.qualcomm.robotcore.hardware.HardwareMap.DeviceMapping#get(String)
     * @see #tryGet(Class, String)
     */
    public <T> T get(Class<? extends T> classOrInterface, String deviceName) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            T result = tryGet(classOrInterface, deviceName);
            if (result==null) throw new IllegalArgumentException(String.format("Unable to find a hardware device with name \"%s\" and type %s", deviceName, classOrInterface.getSimpleName()));
            return result;
        }
    }

    /**
     * Retrieves the (first) device with the indicated name which is also an instance of the
     * indicated class or interface. If no such device is found, null is returned.
     *
     * This is not commonly used; {@link #get} is the usual method for retreiving items from
     * the map.
     *
     */
    public @Nullable <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            List<HardwareDevice> list = allDevicesMap.get(deviceName);
            if (list != null) {
                for (HardwareDevice device : list) {
                    if (classOrInterface.isInstance(device)) {
                        return classOrInterface.cast(device);
                    }
                }
            }
            return null;
        }
    }

    /**
     * (Advanced) Returns the device with the indicated {@link com.example.robotics.hardware.HardwareDevice.HardwarePort}, if it exists,
     * cast to the indicated class or interface; otherwise, null.
     */
    public @Nullable <T> T get(Class<? extends T> classOrInterface, HardwareDevice.HardwarePort port) {
        synchronized (lock) {
            Object device = portNumberMap.get(port);
            if (device != null) {
                if (classOrInterface.isInstance(device)) {
                    return classOrInterface.cast(device);
                }
            }
            return null;
        }
    }

    /**
     * Returns the (first) device with the indicated name. If no such device is found, an exception is thrown.
     * Note that the compile-time type of the return value of this method is {@link HardwareDevice},
     * which is usually not what is desired in user code. Thus, the programmer usually casts the
     * return type to the target type that the programmer knows the returned value to be:
     *
     * <pre>
     *    DcMotor motorLeft = (DcMotor)hardwareMap.get("motorLeft");
     *    ColorSensor colorSensor = (ColorSensor)hardwareMap.get("myColorSensor");
     * </pre>
     *
     * @param deviceName  the name of the device object to be retrieved
     * @return a device with the indicated name.
     * @see #get(Class, String)
//     * @see com.qualcomm.robotcore.hardware.HardwareMap.DeviceMapping#get(String)
     */
    public HardwareDevice get(String deviceName) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            List<HardwareDevice> list = allDevicesMap.get(deviceName);
            if (list != null) {
                for (HardwareDevice device : list) {
                    return device;
                }
            }
            throw new IllegalArgumentException(String.format("Unable to find a hardware device with name \"%s\"", deviceName));
        }
    }

    /**
     * Returns all the devices which are instances of the indicated class or interface.
     * @param classOrInterface the class or interface indicating the type of the device object to be retrieved
     * @return all the devices registered in the map which are instances of classOrInterface
     * @see #get(Class, String)
     */
    public <T> List<T> getAll(Class<? extends T> classOrInterface) {
        synchronized (lock) {
            List<T> result = new LinkedList<T>();
            for (HardwareDevice device : this) {
                if (classOrInterface.isInstance(device)) {
                    result.add(classOrInterface.cast(device));
                }
            }
            return result;
        }
    }

    /**
     * Puts a device in the overall map without having it also reside in a type-specific DeviceMapping.
     * @param deviceName the name by which the device is to be known (case sensitive)
     * @param device     the device to be stored by that name
     */
    public void put(String deviceName, HardwareDevice device) {
        internalPut(null, deviceName, device);
    }

    /**
     * (Advanced) Puts a device in the overall map without having it also reside in a type-specific DeviceMapping.
     * @param port the {@link com.example.robotics.hardware.HardwareDevice.HardwarePort} of the device
     * @param deviceName   the name by which the device is to be known (case sensitive)
     * @param device       the device to be stored by that name
     */
    public void put(@NonNull HardwareDevice.HardwarePort port, @NonNull String deviceName, HardwareDevice device) {
        //Assert.assertNotNull(serialNumber);
        internalPut(port, deviceName, device);
    }

    protected void internalPut(@Nullable HardwareDevice.HardwarePort port, @NonNull String deviceName, HardwareDevice device) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            List<HardwareDevice> list = allDevicesMap.get(deviceName);
            if (list == null) {
                list = new ArrayList<HardwareDevice>(1);
                allDevicesMap.put(deviceName, list);
            }
            if (!list.contains(device)) {
                allDevicesList = null;
                list.add(device);
            }
            if (port != null) {
                portNumberMap.put(port, device);
            }
            rebuildDeviceNamesIfNecessary();
            recordDeviceName(deviceName, device);
        }
    }

    /**
     * (Advanced) Removes a device from the overall map, if present. If the device is also present in a
     * DeviceMapping, then the device should be removed using {@link DeviceMapping#remove}
     * instead of calling this method.
     *
     * <p>This is normally called only by code in the SDK itself, not by user code.</p>
     *
     * @param deviceName  the name of the device to remove
     * @param device      the device to remove under that name
     * @return whether a device was removed or not
     */
    public boolean remove(String deviceName, HardwareDevice device) {
        return remove(null, deviceName, device);
    }

    /**
     * (Advanced) Removes a device from the overall map, if present. If the device is also present in a
     * DeviceMapping, then the device should be removed using {@link DeviceMapping#remove}
     * instead of calling this method.
     *
     * <p>This is normally called only by code in the SDK itself, not by user code.</p>
     *
     * @param port        (optional) the serial number of the device
     * @param deviceName  the name of the device to remove
     * @param device      the device to remove under that name
     * @return whether a device was removed or not
     */
    public boolean remove(@Nullable HardwareDevice.HardwarePort port, String deviceName, HardwareDevice device) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            List<HardwareDevice> list = allDevicesMap.get(deviceName);
            if (list != null) {
                list.remove(device);
                if (list.isEmpty()) {
                    allDevicesMap.remove(deviceName);
                }
                allDevicesList = null;
                deviceNames = null;
                if (port != null) {
                    portNumberMap.remove(port);
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Returns all the names by which the device is known. Virtually always, there is but
     * a single name.
     * @param device the device whose names are desired.
     * @return the set of names by which that device is known
     */
    public @NonNull Set<String> getNamesOf(HardwareDevice device) {
        synchronized (lock) {
            rebuildDeviceNamesIfNecessary();
            Set<String> result = this.deviceNames.get(device);
            if (result==null) {
                result = new HashSet<String>();
            }
            return result;
        }
    }

    protected void recordDeviceName(String deviceName, HardwareDevice device) {
        deviceName = deviceName.trim();
        Set<String> names = this.deviceNames.get(device);
        if (names==null) {
            names = new HashSet<String>();
            this.deviceNames.put(device,names);
        }
        names.add(deviceName);
    }

    protected void rebuildDeviceNamesIfNecessary() {
        if (this.deviceNames == null) {
            this.deviceNames = new ConcurrentHashMap<HardwareDevice, Set<String>>();
            for (Map.Entry<String, List<HardwareDevice>> pair : allDevicesMap.entrySet()) {
                for (HardwareDevice device : pair.getValue()) {
                    recordDeviceName(pair.getKey(), device);
                }
            }
        }
    }

    private void buildAllDevicesList() {
        if (allDevicesList == null) {
            Set<HardwareDevice> set = new HashSet<HardwareDevice>();
            for (String key : allDevicesMap.keySet()) {
                set.addAll(allDevicesMap.get(key));
            }
            allDevicesList = new ArrayList<HardwareDevice>(set);
        }
    }

    /**
     * Returns the number of unique device objects currently found in this HardwareMap.
     * @return the number of unique device objects currently found in this HardwareMap.
     * @see #iterator()
     */
    public int size() {
        synchronized (lock) {
            buildAllDevicesList();
            return allDevicesList.size();
        }
    }

    /**
     * Returns an iterator of all the devices in the HardwareMap.
     * @return an iterator of all the devices in the HardwareMap.
     * @see #size()
     */
    @Override
    public @NonNull Iterator<HardwareDevice> iterator() {
        synchronized (lock) {
            buildAllDevicesList();
            return new ArrayList<>(allDevicesList).iterator(); // make copy for locking reasons
        }
    }

    //------------------------------------------------------------------------------------------------
    // Types
    //------------------------------------------------------------------------------------------------

    /**
     * A DeviceMapping contains a subcollection of the devices registered in a {@link HardwareMapp}
     * comprised of all the devices of a particular device type
     *
     * @param <DEVICE_TYPE>
//     * @see com.qualcomm.robotcore.hardware.HardwareMap.DeviceMapping#get(String)
     * @see #get(String)
     */
    public class DeviceMapping<DEVICE_TYPE extends HardwareDevice> implements Iterable<DEVICE_TYPE> {
        private Map <String, DEVICE_TYPE> map = new HashMap<String, DEVICE_TYPE>();
        private Class<DEVICE_TYPE> deviceTypeClass;

        public DeviceMapping(Class<DEVICE_TYPE> deviceTypeClass) {
            this.deviceTypeClass = deviceTypeClass;
        }

        /** Returns the runtime device type for this mapping */
        public Class<DEVICE_TYPE> getDeviceTypeClass() {
            return this.deviceTypeClass;
        }

        /** A small utility that assists in keeping the Java generics type system happy */
        public DEVICE_TYPE cast(Object obj) {
            return this.deviceTypeClass.cast(obj);
        }

        public DEVICE_TYPE get(String deviceName) {
            synchronized (lock) {
                deviceName = deviceName.trim();
                DEVICE_TYPE device = map.get(deviceName);
                if (device == null) {
                    String msg = String.format("Unable to find a hardware device with the name \"%s\"", deviceName);
                    throw new IllegalArgumentException(msg);
                }
                return device;
            }
        }

        /**
         * Registers a new device in this DeviceMapping under the indicated name. Any existing device
         * with this name in this DeviceMapping is removed. The new device is also added to the
         * overall collection in the overall map itself. Note that this method is normally called
         * only by code in the SDK itself, not by user code.
         *
         * @param deviceName  the name by which the new device is to be known (case sensitive)
         * @param device      the new device to be named
//         * @see HardwareMap#put(String, HardwareDevice)
         */
        public void put(String deviceName, DEVICE_TYPE device) {
            internalPut(null, deviceName, device);
        }

        /**
         * (Advanced) Registers a new device in this DeviceMapping under the indicated name. Any existing device
         * with this name in this DeviceMapping is removed. The new device is also added to the
         * overall collection in the overall map itself. Note that this method is normally called
         * only by code in the SDK itself, not by user code.
         *
         * @param port        the port number of the device
         * @param deviceName  the name by which the new device is to be known (case sensitive)
         * @param device      the new device to be named
//         * @see HardwareMap#put(String, HardwareDevice)
         */
        public void put(@NonNull HardwareDevice.HardwarePort port, String deviceName, DEVICE_TYPE device) {
            internalPut(port, deviceName, device);
        }

        protected void internalPut(@Nullable HardwareDevice.HardwarePort port, String deviceName, DEVICE_TYPE device) {
            synchronized (lock) {
                // remove whitespace at start & end
                deviceName = deviceName.trim();

                // Remove any existing device with that name
                remove(port, deviceName);

                // Remember the new device in the overall list
                HardwareMap.this.internalPut(port, deviceName, device);

                // Remember the new device here locally, too
                putLocal(deviceName, device);
            }
        }

        public void putLocal(String deviceName, DEVICE_TYPE device) {
            synchronized (lock) {
                deviceName = deviceName.trim();
                map.put(deviceName, device);
            }
        }

        /**
         * Returns whether a device of the indicated name is contained within this mapping
         * @param deviceName the name sought
         * @return whether a device of the indicated name is contained within this mapping
         */
        public boolean contains(String deviceName) {
            synchronized (lock) {
                deviceName = deviceName.trim();
                return map.containsKey(deviceName);
            }
        }

        /**
         * (Advanced) Removes the device with the indicated name (if any) from this DeviceMapping. The device
         * is also removed under that name in the overall map itself. Note that this method is normally
         * called only by code in the SDK itself, not by user code.
         *
         * @param deviceName  the name of the device to remove.
         * @return            whether any modifications were made to this DeviceMapping
//         * @see HardwareMap#remove
         */
        public boolean remove(String deviceName) {
            return remove(null, deviceName);
        }
        /**
         * (Advanced) Removes the device with the indicated name (if any) from this DeviceMapping. The device
         * is also removed under that name in the overall map itself. Note that this method is normally
         * called only by code in the SDK itself, not by user code.
         *
         * @param port        (optional) the serial number of the device to remove
         * @param deviceName  the name of the device to remove.
         * @return            whether any modifications were made to this DeviceMapping
//         * @see HardwareMap#remove
         */
        public boolean remove(@Nullable HardwareDevice.HardwarePort port, String deviceName) {
            synchronized (lock) {
                deviceName = deviceName.trim();
                HardwareDevice device = map.remove(deviceName);
                if (device != null) {
                    HardwareMap.this.remove(port, deviceName, device);
                    return true;
                }
                return false;
            }
        }

        /**
         * Returns an iterator over all the devices in this DeviceMapping.
         * @return an iterator over all the devices in this DeviceMapping.
         */
        @Override public @NonNull Iterator<DEVICE_TYPE> iterator() {
            synchronized (lock) {
                return new ArrayList<>(map.values()).iterator();
            }
        }

        /**
         * Returns a collection of all the (name, device) pairs in this DeviceMapping.
         * @return a collection of all the (name, device) pairs in this DeviceMapping.
         */
        public Set<Map.Entry<String, DEVICE_TYPE>> entrySet() {
            synchronized (lock) {
                return new HashSet<>(map.entrySet());
            }
        }

        /**
         * Returns the number of devices currently in this DeviceMapping
         * @return the number of devices currently in this DeviceMapping
         */
        public int size() {
            synchronized (lock) {
                return map.size();
            }
        }
    }

}