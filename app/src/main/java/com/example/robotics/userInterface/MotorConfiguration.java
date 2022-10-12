package com.example.robotics.userInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.robotics.R;

import java.io.Serializable;
import java.util.ArrayList;

public class MotorConfiguration extends AppCompatActivity implements Serializable {
    //dc motors
    private EditText port1_txt;
    private EditText port2_txt;
    private EditText port3_txt;
    private EditText port4_txt;
    private EditText port5_txt;
    private EditText port6_txt;
    //servos
    private EditText port10_txt;
    private EditText port11_txt;
    private EditText port12_txt;
    private EditText port13_txt;
    private EditText port14_txt;
    private EditText port15_txt;
    private EditText configNametxt;

    private final ArrayList<EditText> portsTxt = new ArrayList<>();


    private Button saveBtn;
    private Button clearBtn;


    private ArrayList<SharedPreferences> savedConfigList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_configuration);

        SharedPreferences saveConfig1 = getApplicationContext().getSharedPreferences("savedConfig1", MODE_PRIVATE);
        SharedPreferences saveConfig2 = getApplicationContext().getSharedPreferences("savedConfig2", MODE_PRIVATE);
        SharedPreferences saveConfig3 = getApplicationContext().getSharedPreferences("savedConfig3", MODE_PRIVATE);
        SharedPreferences saveConfig4 = getApplicationContext().getSharedPreferences("savedConfig4", MODE_PRIVATE);
        SharedPreferences saveConfig5 = getApplicationContext().getSharedPreferences("savedConfig5", MODE_PRIVATE);
        SharedPreferences saveConfig6 = getApplicationContext().getSharedPreferences("savedConfig6", MODE_PRIVATE);

        port1_txt = (EditText) findViewById(R.id.port1_txt);
        port2_txt = (EditText) findViewById(R.id.port2_txt);
        port3_txt = (EditText) findViewById(R.id.port3_txt);
        port4_txt = (EditText) findViewById(R.id.port4_txt);
        port5_txt = (EditText) findViewById(R.id.port5_txt);
        port6_txt = (EditText) findViewById(R.id.port6_txt);

        port10_txt = (EditText) findViewById(R.id.port10_txt);
        port11_txt = (EditText) findViewById(R.id.port11_txt);
        port12_txt = (EditText) findViewById(R.id.port12_txt);
        port13_txt = (EditText) findViewById(R.id.port13_txt);
        port14_txt = (EditText) findViewById(R.id.port14_txt);
        port15_txt = (EditText) findViewById(R.id.port15_txt);

        configNametxt = (EditText) findViewById(R.id.configNametxt);
        saveBtn         = (Button) findViewById(R.id.saveBtn);

        //Saving a total of 6 different configurations for mapping
        savedConfigList.add(saveConfig1);
        savedConfigList.add(saveConfig2);
        savedConfigList.add(saveConfig3);
        savedConfigList.add(saveConfig4);
        savedConfigList.add(saveConfig5);
        savedConfigList.add(saveConfig6);

    }

    public void onSaveClicked(View v){
        int id = 1;
        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList ){

            //checking for an unused SharedPreference slot
            if( pref.getString("configName","No Config").equals("No Config") ){
                // for saving configuration
                SharedPreferences.Editor configEditor = pref.edit();
                configEditor.clear(); //clear all data before

                configEditor.putInt( "id", id );
                configEditor.putString( "configName", configNametxt.getText().toString() );
                configEditor.putString( "port1", port1_txt.getText().toString() );
                configEditor.putString( "port2", port2_txt.getText().toString() );
                configEditor.putString( "port3", port3_txt.getText().toString() );
                configEditor.putString( "port4", port4_txt.getText().toString() );
                configEditor.putString( "port5", port5_txt.getText().toString() );
                configEditor.putString( "port6", port6_txt.getText().toString() );
                configEditor.putString( "port10", port10_txt.getText().toString() );
                configEditor.putString( "port11", port11_txt.getText().toString() );
                configEditor.putString( "port12", port12_txt.getText().toString() );
                configEditor.putString( "port13", port13_txt.getText().toString() );
                configEditor.putString( "port14", port14_txt.getText().toString() );
                configEditor.putString( "port15", port15_txt.getText().toString() );
                configEditor.apply(); // Apply the edits!

                System.out.println("Configuration number "+id+" saved ");
                Toast.makeText(this,"Configuration saved",Toast.LENGTH_SHORT).show();
                break;
            }
            id++;
        }
        intent.putExtra("isChosen",true);
        intent.putExtra("configName", configNametxt.getText().toString() );
        intent.putExtra("port1", port1_txt.getText().toString() );
        intent.putExtra("port2", port2_txt.getText().toString() );
        intent.putExtra("port3", port3_txt.getText().toString() );
        intent.putExtra("port4", port4_txt.getText().toString() );
        intent.putExtra("port5", port5_txt.getText().toString() );
        intent.putExtra("port6", port6_txt.getText().toString() );

        intent.putExtra("port10", port10_txt.getText().toString() );
        intent.putExtra("port11", port11_txt.getText().toString() );
        intent.putExtra("port12", port12_txt.getText().toString() );
        intent.putExtra("port13", port13_txt.getText().toString() );
        intent.putExtra("port14", port14_txt.getText().toString() );
        intent.putExtra("port15", port15_txt.getText().toString() );

        startActivity(intent);
    }


}//end class