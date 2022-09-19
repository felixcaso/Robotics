package com.example.robotics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final UUID UUID_PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private boolean devicePaired = false;
    private BluetoothDevice device;
    private BluetoothAdapter BT = BluetoothAdapter.getDefaultAdapter();
    private TextView displayTxt;
    private TextView pressedTxt;
    private Button connBtn;
    private Dpad dpad = new Dpad();

    private BluetoothSocket socket;
//    private InputStream inStream;
    private OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create variables for items
        displayTxt = (TextView) findViewById(R.id.displayTxt);
        pressedTxt = (TextView) findViewById(R.id.pressedTxt);
        connBtn = findViewById(R.id.connBtn);

        if( initBT() ){
            System.out.println("BT ON");
        }


        connBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button


            }
        });//end onClickListener

    }// end onCreate

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
                    sendData("C");
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
        sendData(String.valueOf(x));

    }//end processJoyStickInput

    private boolean initBT(){
        boolean found = false;

        if (BT != null) { // if BT is null, device does not support Bluetooth
            if (!BT.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return found;
                }
                startActivityForResult(enableBtIntent, 1);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Set<BluetoothDevice> pairedDevice = BT.getBondedDevices();

            if (pairedDevice.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No devices paired", Toast.LENGTH_SHORT).show();
            } else {
                final String ROBOT_ADDRESS = "00:20:01:31:DF:F9";

                for (BluetoothDevice iterator : pairedDevice) {
                    if (iterator.getAddress().equals(ROBOT_ADDRESS)) {
                        device = iterator;
                        devicePaired = true;
                        Toast.makeText(getApplicationContext(), "Device Paired", Toast.LENGTH_SHORT).show();
                        try{
                            socket = device.createRfcommSocketToServiceRecord(UUID_PORT);
                            socket.connect();
                            outputStream = socket.getOutputStream();
                            displayTxt.setText("Connected");

                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        found = true;
                        break;
                    }//if
                }// for

            }//else
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        return found;
    }

    private void sendData(String data){
        try {

            outputStream.write(data.getBytes());
            pressedTxt.setText("Button Pressed: "+data);

        }catch(IOException e){
            e.printStackTrace();
        }


    }//end sendData







}//end main activity

