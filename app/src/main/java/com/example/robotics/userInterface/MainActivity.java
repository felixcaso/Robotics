package com.example.robotics.userInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robotics.Driver2023;
import com.example.robotics.GPad;
import com.example.robotics.hardware.DcMotor;
import com.example.robotics.hardware.Dpad;
import com.example.robotics.R;
import com.example.robotics.hardware.Gamepad;
import com.example.robotics.hardware.HardwareMap;
import com.example.robotics.hardware.Servo;
import com.example.robotics.hardware.Telemetry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements Serializable {


    /**
     * Establishing global objects for driver control
     *
     **/
    public Gamepad gamepad1 = new Gamepad();
    public Gamepad gamepad2 = new Gamepad();
    public final Telemetry telemetry = new Telemetry();
    public static HardwareMap hardwareMap = new HardwareMap();
    private final Dpad dpad = new Dpad();

    /**
    * Main Class Attributes
    * 1) Establishing User Interface Component variables
    * 2) Establishing Bluetooth Component variables
    * 3) Establishing Variables for display
    */
    // User Interface Component variables
    private ListView listView;
    public TextView displayTxt;
    public TextView configTxt;
    private TextView pressedTxt;


    //Establishing Bluetooth Component variables
    private final UUID UUID_PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice swarmDevice;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    // Variables for display
    private String chosenSwarm;
    private boolean swarmConnected = false;
    private String configName = "Robot Not Configured";
    private boolean isConfigChosen = false;

    private ArrayList<SharedPreferences> savedConfigList = new ArrayList<>();
    Driver2023 opMode;
    MediaPlayer music;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUIComponents();

    }// end onCreate


    /**
    * Game Controller input Methods
    * */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //boolean handled = false
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD && (swarmConnected)) {

            if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                sendData("A");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                sendData("B");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
                sendData("X");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                sendData("Y");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
                sendData("R1");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
                sendData("L1");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
                sendData("R2");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
                sendData("L2");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
                sendData("L3");
            }

            if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
                 sendData("R3");
            }


            if (keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                 sendData("Start");
            }

//            if (keyCode == KeyEvent.KEYCODE_BUTTON_SELECT) {
//
//            }
//
//            if (keyCode == KeyEvent.KEYCODE_BUTTON_MODE) {
//
//            }

        }// end if Device == game-pad

        return super.onKeyDown(keyCode, event);
    }// EndOnKeyDown()

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        // Check if this event if from a D-pad and process accordingly.
        if (Dpad.isDpadDevice(event)) {

            int press = dpad.getDirectionPressed(event);
            switch (press) {
                case Dpad.LEFT:
                    // Do something for LEFT direction press
                    sendData("L");
                    return true;

                case Dpad.RIGHT:
                    // Do something for RIGHT direction press
                    sendData("R");
                    return true;

                case Dpad.UP:
                    // Do something for UP direction press
                    sendData("U");
                    return true;

                case Dpad.DOWN:
                    sendData("D");
                    return true;

                case Dpad.CENTER:

                    return true;

            }
        }
        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);

            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);

    }//end onGenericMotionEvent

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis) :
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {

        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        }

        // Update the ship object based on the new x and y values
//        pressedTxt.setText("X axis: "+String.valueOf(x) + " Y axis: "+String.valueOf(y));
        //sendData(String.valueOf(x));

    }//end processJoyStickInput

    /**
    * Bluetooth Methods
    * Do not change Anything below this Code
    * This will interrupt Bluetooth connectivity
    * This must all happen in this class
    * */

    @SuppressLint("MissingPermission")
    public void turnBTOn() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 1);
            Toast.makeText(getApplicationContext(), "Bluetooth Turned On", Toast.LENGTH_LONG).show();

        } else {
            listPairedSwarms();
        }
    }

    @SuppressLint("MissingPermission")
    public void listPairedSwarms() {
        Set <BluetoothDevice> deviceList = bluetoothAdapter.getBondedDevices();// A list of Bluetooth devices
        ArrayList <String> deviceNames = new ArrayList<String>();// A list of device names

        // looping through deviceList to populate deviceNames
        for(BluetoothDevice device : deviceList) deviceNames.add(device.getName());


        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, deviceNames);

        listView.setAdapter(adapter);
        listView.setVisibility(ListView.VISIBLE);
    }

    @SuppressLint("MissingPermission")
    private void initSwarmConnection(){
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

        for(BluetoothDevice pDevice: pairedDevice){
            if(pDevice.getName().equals(chosenSwarm)){
                swarmDevice = pDevice;
                try{
                    socket = swarmDevice.createRfcommSocketToServiceRecord(UUID_PORT);
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                    swarmConnected = true;
                    displayTxt.setText("Connected to: " + chosenSwarm);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                break;
            }//end if device name == chosenSwarm
        }//end for loop pairedDevice
    }// end initSwarmConnection()

    public void sendData(String data){
        if(data != null) {
            try {
                outputStream.write(data.getBytes());
                pressedTxt.setText("Button Pressed: " + data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }//end sendData

//    void beginListenForData(){
//        final Handler handler = new Handler();
//        stopThread = false;
//        buffer = new byte[1024];
//        Thread thread  = new Thread(new Runnable()
//        {
//            public void run()
//            {
//                while(!Thread.currentThread().isInterrupted() && !stopThread)
//                {
//                    try
//                    {
//                        int byteCount = inStream.available();
//                        if(byteCount > 0)
//                        {
//                            byte[] rawBytes = new byte[byteCount];
//                            int bytes = inStream.read(rawBytes);
//                            final String string=new String(rawBytes,"UTF-8");
//                            System.out.println(string);
//                            System.out.println(bytes);
//
//                            final String[] str = string.split("#");
//
//                            handler.post(new Runnable() {
//                                public void run() {
//                                    if (str.length == 4) {
//                                        temp = str[0] + "\u00B0";
//                                        ph = str[1];
//
//                                        //turbidity
//                                        switch(str[2].toUpperCase()) {
//                                            case "D":
//                                                turb = "Dirty";
//                                                break;
//                                            case "S":
//                                                turb = "Soapy";
//                                                break;
//                                            case "C":
//                                                turb = "Clean";
//                                                break;
//                                            default:
//                                                turb = "n/a";
//                                                break;
//                                        }
//
//                                        //water lvl
//                                        switch(str[3].toUpperCase()) {
//                                            case "L":
//                                                lvl = "Low";
//                                                break;
//                                            case "H":
//                                                lvl = "High";
//                                                mBuilder.setContentTitle("BathSense Water Level Alert!");
//                                                mBuilder.setContentText("Water level is HIGH. Tend to bath IMMEDIATELY! to prevent overflow.");
//                                                NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                                                mBuilder.setVibrate(v);
//                                                mBuilder.setSound(uri);
//                                                mNotificationManager.notify(0, mBuilder.build());
//                                                break;
//                                            default:
//                                                lvl = "n/a";
//                                                break;
//                                        }
//
//                                        //Update Screen
//                                        temperature.setText(temp);
//                                        pHBalance.setText(ph);
//                                        turbidity.setText(turb);
//                                        waterLvl.setText(lvl);
//
//                                    }
//                                }
//                            });
//
//                        }
//                    }
//                    catch (IOException ex)
//                    {
//                        stopThread = true;
//                    }
//                }
//            }
//        });
//
//        thread.start();
//    }//end beginListeningForData()



    /**
    * User Interface
    * Code below is for User Interface
    * Setting up text boxes menus and buttons
    *
    * Overriding builtin function for our own use
    * Mostly being used for User Interfacing
    * DO NOT Edited unless necessary
    */
    private void setUIComponents(){
        //create variables for UI Components
        displayTxt = (TextView) findViewById(R.id.displayTxt);
        pressedTxt = (TextView) findViewById(R.id.pressedTxt);
        configTxt =  (TextView) findViewById(R.id.configNameTxt);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(deviceList);
        listView.setVisibility(View.INVISIBLE);

        opMode = new Driver2023();
        music = MediaPlayer.create(MainActivity.this, R.raw.swarm);

        isConfigChosen = getIntent().getBooleanExtra("isChosen",false);

        if(isConfigChosen){
            setConfiguredHardware();
        }
        configTxt.setText(configName);


    }//end setUIComponents

    //Retrieving and routing port configuration
    private void setConfiguredHardware(){

        configName = getIntent().getStringExtra("configName");

        //Dc Motors PORT1 - PORT8
        for(int portNum = 1; portNum <= 8; portNum++){

            System.out.println(getIntent().getStringExtra("port"+portNum));
            hardwareMap.put(getIntent().getStringExtra("port"+portNum), new DcMotor(portNum));
        }

        //Servos PORT10 - PORT15
        for(int portNum = 10; portNum <= 15; portNum++){

            System.out.println(getIntent().getStringExtra("port"+portNum));
            hardwareMap.put(getIntent().getStringExtra("port"+portNum), new Servo(portNum));
        }

    } // end setConfiguredHardware()

    private final AdapterView.OnItemClickListener deviceList = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView parent, View view, int pos, long id){
            chosenSwarm = (String) listView.getItemAtPosition(pos);
            System.out.println("Swarm selected: "+ chosenSwarm);
            initSwarmConnection();
            listView.setVisibility(ListView.INVISIBLE);
        }
    };


    /**
     * Settings and Configuration Menu
     *
     * Code below is for Interfacing with the menu on
     * the main activity. Allowing setting to be configured
     * DO NOT Edited unless Instructed
     */
    private boolean setMenuOptions(MenuItem item){
        switch(item.getItemId()){
            case R.id.BT_menu:
                turnBTOn();
                try{
                    if(socket != null && socket.isConnected()){
                        socket.close();
                        swarmConnected = false;
                        displayTxt.setText(R.string.swarmConnection);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                return true;

            case R.id.BATT_life:
                Toast.makeText(this,"Robot Battery Health Coming Soon!!!",Toast.LENGTH_SHORT).show();
//                sendData("batteryCheck");
                music.start();
                return true;

            case R.id.settings:
                Toast.makeText(this,"Settings Coming Soon!!!",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.restart:
                Toast.makeText(this,"Robot Restart Coming Soon!!!",Toast.LENGTH_SHORT).show();
                if(socket.isConnected() && socket != null){
                    sendData("restart");
                }
                return true;

            case R.id.RobotConfig:
                //Toast.makeText(this,"Motor Configuration Coming Soon!!!",Toast.LENGTH_SHORT).show();
                Intent robotConfig = new Intent(this, SavedRobotConfigs.class);
                //config.putExtra("hardware", (Serializable) hardwareMap);
                startActivity(robotConfig);
                return true;

            case R.id.prog:
//                Toast.makeText(this,"Program and Manage Coming Soon!!!",Toast.LENGTH_SHORT).show();
                try {
                    opMode.loop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.selfCheck:
                Toast.makeText(this,"Self Check Coming Soon!!!",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.virtualController:
                Intent i = new Intent(this, GPad.class);
                i.putExtra("device",swarmDevice);
                startActivity(i);
                return true;

            case R.id.exit:
                //Toast.makeText(this,"Program Exit Coming Soon!!!",Toast.LENGTH_SHORT).show();
                System.exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }//end switch
    }

    @Override
    public void onBackPressed() {

    }// end onBackPressed()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options,menu);
        return true;
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return setMenuOptions(item);
    }//end itemSelected




}//end main activity