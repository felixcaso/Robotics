package com.example.robotics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robotics.userInterface.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GPad extends AppCompatActivity {
    private ListView listView;
    public TextView displaySwarmTxt;
    private String  chosenSwarm;

    private final UUID UUID_PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice swarmDevice;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private boolean swarmConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpad);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(deviceList);
        listView.setVisibility(View.INVISIBLE);

    }//end onCreate

    private final AdapterView.OnItemClickListener deviceList = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView parent, View view, int pos, long id){
            chosenSwarm = (String) listView.getItemAtPosition(pos);
            System.out.println("Swarm selected: "+ chosenSwarm);
            initSwarmConnection();
            listView.setVisibility(ListView.INVISIBLE);
        }
    };

    //======================== Bluetooth Functions ========================
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
        ArrayList<String> deviceNames = new ArrayList<String>();// A list of device names

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
                    swarmConnected = true;
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                break;
            }//end if device name == chosenSwarm
        }//end for loop pairedDevice
    }// end initSwarmConnection()

    @SuppressLint("MissingPermission")
    public void sendData(String data){
        if(!socket.isConnected()){
            try{
                socket = swarmDevice.createRfcommSocketToServiceRecord(UUID_PORT);
                socket.connect();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(data != null) {
            try {

                outputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }//end sendData


    //======================== Button Functions ========================
    public void onClickBt(View v){
        turnBTOn();
        try{
            if(socket != null && socket.isConnected()){
                socket.close();
                swarmConnected = false;
//                displayTxt.setText(R.string.swarmConnection);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void onClickUp(View v){
        System.out.println("Up");
        sendData("U");
    }

    public void onClickDown(View v){
        System.out.println("Down");
        sendData("D");
    }

    public void onClickLeft(View v){
        System.out.println("Left");
        sendData("L");
    }

    public void onClickRight(View v){
        System.out.println("Right");
        sendData("R");
    }

    public void onClickY(View v){
        System.out.println("Y");
        sendData("Y");
    }

    public void onClickA(View v){
        System.out.println("A");
        sendData("A");
    }

    public void onClickX(View v){
        System.out.println("X");
        sendData("X");
    }

    public void onClickB(View v){
        System.out.println("B");
        sendData("B");
    }

}//Class