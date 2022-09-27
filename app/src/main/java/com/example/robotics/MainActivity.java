package com.example.robotics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //Attributes
    private ListView listView;
    public static TextView displayTxt;
    private TextView pressedTxt;
    private Dpad dpad = new Dpad();

    //Bluetooth Attributes
    private final UUID UUID_PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice swarmDevice;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private String chosenSwarm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create variables for items
        displayTxt = (TextView) findViewById(R.id.displayTxt);
        pressedTxt = (TextView) findViewById(R.id.pressedTxt);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(deviceList);


    }// end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.BT_menu:
                turnBTOn();
                try{
                    if(socket != null && socket.isConnected()){
                        socket.close();
                        displayTxt.setText(R.string.swarmConnection);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                return true;

            case R.id.item1:
                Toast.makeText(this,"Item 1 Selected",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.exit:
                finish();
                return true;

            case R.id.settings:
                Toast.makeText(this,"Settings Selected",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.subItem1:
                Toast.makeText(this,"SubItem 1 Selected",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.subItem2:
                Toast.makeText(this,"SubItem 2 Selected",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }//end switch



    }//end itemSelected

    @SuppressLint("MissingPermission")
    private AdapterView.OnItemClickListener deviceList = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView parent, View view, int pos, long id){

            chosenSwarm = (String) listView.getItemAtPosition(pos);
            System.out.println("Swarm selected: "+ chosenSwarm);
            initSwarmConnection();
            listView.setVisibility(ListView.INVISIBLE);

        }
    };


    //Game Controller input Methods
    @Override
    public void onBackPressed() {

    }// end onBackPressed()

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //boolean handled = false

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {

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

                    return true;

                case Dpad.RIGHT:
                    // Do something for RIGHT direction press

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



    //Bluetooth Methods
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
    public void off(View v) {
        bluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("MissingPermission")
    public void visible(View v) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
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
                    displayTxt.setText("Connected to: " + chosenSwarm);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                break;
            }//end if device name == chosenSwarm
        }//end for loop pairedDevice
    }// end initSwarmConnection()

    private void sendData(String data){
        try {

            outputStream.write(data.getBytes());
            pressedTxt.setText("Button Pressed: " + data);

        }catch(IOException e){
            e.printStackTrace();
        }


    }//end sendData




}//end main activity

