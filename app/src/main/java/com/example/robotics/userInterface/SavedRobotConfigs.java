package com.example.robotics.userInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.robotics.R;

import java.util.ArrayList;

public class SavedRobotConfigs extends AppCompatActivity {

    private Button savedConfig1;
    private Button savedConfig2;
    private Button savedConfig3;
    private Button savedConfig4;
    private Button savedConfig5;
    private Button savedConfig6;
    private Button newBtn;
    private Button clearBtn;



    private ArrayList<SharedPreferences> savedConfigList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_robot_configs);

        SharedPreferences saveConfig1 = getApplicationContext().getSharedPreferences("savedConfig1", MODE_PRIVATE);
        SharedPreferences saveConfig2 = getApplicationContext().getSharedPreferences("savedConfig2", MODE_PRIVATE);
        SharedPreferences saveConfig3 = getApplicationContext().getSharedPreferences("savedConfig3", MODE_PRIVATE);
        SharedPreferences saveConfig4 = getApplicationContext().getSharedPreferences("savedConfig4", MODE_PRIVATE);
        SharedPreferences saveConfig5 = getApplicationContext().getSharedPreferences("savedConfig5", MODE_PRIVATE);
        SharedPreferences saveConfig6 = getApplicationContext().getSharedPreferences("savedConfig6", MODE_PRIVATE);

        savedConfig1 = (Button) findViewById(R.id.savedConfig1);
        savedConfig2 = (Button) findViewById(R.id.savedConfig2);
        savedConfig3 = (Button) findViewById(R.id.savedConfig3);
        savedConfig4 = (Button) findViewById(R.id.savedConfig4);
        savedConfig5 = (Button) findViewById(R.id.savedConfig5);
        savedConfig6 = (Button) findViewById(R.id.savedConfig6);

        savedConfig1.setVisibility(View.INVISIBLE);
        savedConfig2.setVisibility(View.INVISIBLE);
        savedConfig3.setVisibility(View.INVISIBLE);
        savedConfig4.setVisibility(View.INVISIBLE);
        savedConfig5.setVisibility(View.INVISIBLE);
        savedConfig6.setVisibility(View.INVISIBLE);

        savedConfigList.add(saveConfig1);
        savedConfigList.add(saveConfig2);
        savedConfigList.add(saveConfig3);
        savedConfigList.add(saveConfig4);
        savedConfigList.add(saveConfig5);
        savedConfigList.add(saveConfig6);

        displaySavedConfiguration();

    }// end on create()

    public void onNewClicked(View v){
        Intent intent = new Intent(this,MotorConfiguration.class);
        startActivity(intent);
    }

    public void onClearClicked(View v){
        for(SharedPreferences pref: savedConfigList){
            SharedPreferences.Editor configEditor = pref.edit();
            configEditor.clear();
            configEditor.apply();
        }
        savedConfig1.setVisibility(View.INVISIBLE);
        savedConfig2.setVisibility(View.INVISIBLE);
        savedConfig3.setVisibility(View.INVISIBLE);
        savedConfig4.setVisibility(View.INVISIBLE);
        savedConfig5.setVisibility(View.INVISIBLE);
        savedConfig6.setVisibility(View.INVISIBLE);

    }//end onClearClicked()

    public void displaySavedConfiguration(){

        for(SharedPreferences pref: savedConfigList){

            if( pref.contains("configName")){
                int id = pref.getInt("id", -1);
                String configName = pref.getString("configName","No name set");

                switch(id){
                    case 1:
                        savedConfig1.setVisibility(View.VISIBLE);
                        savedConfig1.setText(configName);
                        break;
                    case 2:
                        savedConfig2.setVisibility(View.VISIBLE);
                        savedConfig2.setText(configName);
                        break;
                    case 3:
                        savedConfig3.setVisibility(View.VISIBLE);
                        savedConfig3.setText(configName);
                        break;
                    case 4:
                        savedConfig4.setVisibility(View.VISIBLE);
                        savedConfig4.setText(configName);
                        break;
                    case 5:
                        savedConfig5.setVisibility(View.VISIBLE);
                        savedConfig5.setText(configName);
                        break;
                    case 6:
                        savedConfig6.setVisibility(View.VISIBLE);
                        savedConfig6.setText(configName);
                        break;
                    default:
                        break;

                }//end switch
            }
        }



    }


    // Configuration Pressed
    public void config1Clicked(View v){


        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList){
            int id = pref.getInt("id",-1);
            if(id == 1) {
                String configName = pref.getString("configName", "NADA AKI");


                intent.putExtra( "configName", configName );
                intent.putExtra("isChosen", true);

                //Retrieving port configuration
                final int MAX_PORTS = 20;
                for(int portNum = 1; portNum <= MAX_PORTS; portNum++){

                    String portName = pref.getString("port"+portNum,"NADA AKI");
                    intent.putExtra("port"+portNum, portName);

                }
                break;
            }
        }
        startActivity(intent);

    }//end savedConfig1Clicked()

    public void config2Clicked(View v)  {
        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList){
            int id = pref.getInt("id",-1);
            if(id == 2) {
                String configName = pref.getString("configName", "NADA AKI");


                intent.putExtra( "configName", configName );
                intent.putExtra("isChosen", true);

                //Retrieving port configuration
                final int MAX_PORTS = 20;
                for(int portNum = 1; portNum <= MAX_PORTS; portNum++){

                    String portName = pref.getString("port"+portNum,"NADA AKI");
                    intent.putExtra("port"+portNum, portName);

                }
                break;
            }
        }
        startActivity(intent);

    }

    public void config3Clicked(View v){
        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList){
            int id = pref.getInt("id",-1);
            if(id == 3) {
                String configName = pref.getString("configName", "NADA AKI");


                intent.putExtra( "configName", configName );
                intent.putExtra("isChosen", true);

                //Retrieving port configuration
                final int MAX_PORTS = 20;
                for(int portNum = 1; portNum <= MAX_PORTS; portNum++){

                    String portName = pref.getString("port"+portNum,"NADA AKI");
                    intent.putExtra("port"+portNum, portName);

                }
                break;
            }
        }
        startActivity(intent);

    }

    public void config4Clicked(View v){
        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList){
            int id = pref.getInt("id",-1);
            if(id == 4) {
                String configName = pref.getString("configName", "NADA AKI");


                intent.putExtra( "configName", configName );
                intent.putExtra("isChosen", true);

                //Retrieving port configuration
                final int MAX_PORTS = 20;
                for(int portNum = 1; portNum <= MAX_PORTS; portNum++){

                    String portName = pref.getString("port"+portNum,"NADA AKI");
                    intent.putExtra("port"+portNum, portName);

                }
                break;
            }
        }
        startActivity(intent);

    }

    public void config5Clicked(View v){
        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList){
            int id = pref.getInt("id",-1);
            if(id == 5) {
                String configName = pref.getString("configName", "NADA AKI");


                intent.putExtra( "configName", configName );
                intent.putExtra("isChosen", true);

                //Retrieving port configuration
                final int MAX_PORTS = 20;
                for(int portNum = 1; portNum <= MAX_PORTS; portNum++){

                    String portName = pref.getString("port"+portNum,"NADA AKI");
                    intent.putExtra("port"+portNum, portName);

                }
                break;
            }
        }
        startActivity(intent);

    }

    public void config6Clicked(View v){
        Intent intent = new Intent(this,MainActivity.class);

        for(SharedPreferences pref: savedConfigList){
            int id = pref.getInt("id",-1);
            if(id == 6) {
                String configName = pref.getString("configName", "NADA AKI");


                intent.putExtra( "configName", configName );
                intent.putExtra("isChosen", true);

                //Retrieving port configuration
                final int MAX_PORTS = 20;
                for(int portNum = 1; portNum <= MAX_PORTS; portNum++){

                    String portName = pref.getString("port"+portNum,"NADA AKI");
                    intent.putExtra("port"+portNum, portName);

                }
                break;
            }
        }
        startActivity(intent);

    }


}//end class