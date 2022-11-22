// ********** SWARM CONTROL-HUB (Arduino Mega Version)*********
/*           Created Sept/20/22


        ^^      .-=-=-=-.  ^^
    ^^        (`-=-=-=-=-`)         ^^
            (`-=-=-=-=-=-=-`)  ^^         ^^
      ^^   (`-=-=-=-=-=-=-=-`)   ^^              ^^
          ( `-=-= S.A.R =-=-` )      ^^
          (`-=-=-=-=-=-=-=-=-`)  ^^
          (`-=-=-=-=-=-=-=-=-`)              ^^
          (`-=-=-=-=-=-=-=-=-`)                      ^^
          (`-=-=-=-=-=-=-=-=-`)  ^^
           (`-=-=-=-=-=-=-=-`)          ^^
            (`-=-=-=-=-=-=-`)  ^^                 ^^
              (`-=-=-=-=-`)
               `-=-=-=-=-`



   NOTE: Arduino Mega recommended for extra digital and analog Pins

   Update on Sept/26/22 (commited to github)
   -- First H-bridge successfully configured with speed control
   -- Able to gather more than a char/byte from bluetooth data

   Update on Oct/2/22
   -- Created DcMotor Class simple motor configuration
   -- Created A2A telemtry communication for autonomuous mode use ( {key=value} )
      -- NOT TESTED YET
   -- Added BEEautiful swarm and be ASCII ART
   Update on Oct/12/22
   --Creating a loop to check connection to swarm
*/
//  DO NOT CHANGE ANY VARIABLE NAME OR VALUE BELOW UNLESS OTHERWISE INSTRUCTED

/* Included Libraries */
#include <DcMotor.h>// DcMotor control library
#include <Ultrasonic.h> // Servo control library
#include <Servo.h> // Servo control library

/* Global and Autonomous mode Constants */
unsigned long autoDelay;
double autoPower;
boolean enableSwarmMind = false;
const int FORWARD = 1;
const int REVERSE = 0;
const String TEST_MSG_FOR_A2A ="{m1_port=1,m1_power=.5,delay=1000.0,m1_name=leftFrontWheel}";

/* First H-Bridge Constants */
//Motor1(Left)
const int MOTOR1_DIRECTION_PIN = 53;
const int MOTOR1_POWER_PIN = 2;
const int MOTOR2_DIRECTION_PIN = 52;
const int MOTOR2_POWER_PIN = 3;

const int MOTOR3_DIRECTION_PIN = 51;
const int MOTOR3_POWER_PIN = 4;
const int MOTOR4_DIRECTION_PIN = 50;
const int MOTOR4_POWER_PIN = 5;

const int TRIG1_PIN = 5 ;
const int ECHO1_PIN = 6 ;

/* Global Objects */
//DcMotor ports 0-5 (6 motor ports)
DcMotor dcMotor1("port0", MOTOR1_POWER_PIN, MOTOR1_DIRECTION_PIN); // (motorName, powerPin, directionPin)
DcMotor dcMotor2("port1", MOTOR2_POWER_PIN, MOTOR2_DIRECTION_PIN); // (motorName, powerPin, directionPin)
DcMotor dcMotor3("port2", MOTOR3_POWER_PIN, MOTOR3_DIRECTION_PIN); // (motorName, powerPin, directionPin)
DcMotor dcMotor4("port3", MOTOR4_POWER_PIN, MOTOR4_DIRECTION_PIN); // (motorName, powerPin, directionPin)

//Servo ports 6-10 (5 servo ports)
//Servo objects here
Servo servo0;


//Analog sensor ports 10-15 (6 ports)
Ultrasonic ult1("port", ECHO1_PIN, TRIG1_PIN);


/*


   %           %
       %           %
          %           %
             %          %
               %          %
                 %          %                   :::
                  %          %                ::::::
               %%%%%%  %%%%%%%%%            ::::::::
            %%%%%ZZZZ%%%%%%   %%%ZZZZ     ::::::::::         ::::::
           %%%ZZZZZ%%%%%%%%%%%%%%ZZZZZZ  :::::::::::    :::::::::::::::::
           ZZZ%ZZZ%%%%%%%%%%%%%%%ZZZZZZZ::::::::::***:::::::::::::::::::::
        ZZZ%ZZZZZZ%%%%%%%%%%%%%%ZZZZZZZZZ::::::***:::::::::::::::::::::::
      ZZZ%ZZZZZZZZZZ%%%%%%%%%%ZZZZZZ%ZZZZ:::***:::::::::::::::::::::::
     ZZ%ZZZZZZZZZZZZZZZZZZZZZZZ%%%%% %ZZZ:**::::::::::::::::::::::
    ZZ%ZZZZZZZZZZZZZZZZZZZ%%%%% | | %ZZZ *:::::::::::::::::::
    Z%ZZZZZZZZZZZZZZZ%%%%%%%%%%%%%%%ZZZ::::::::::::::::::::::::::
     ZZZZZZZZZZZ%%%%%ZZZZZZZZZZZZZZZZZ%%%%:::ZZZZ:::::::::::::::::
       ZZZZ%%%%%ZZZZZZZZZZZZZZZZZZ%%%%%ZZZ%%ZZZ%ZZ%%*:::::::::::
          ZZZZZZZZZZZZZZZZZZ%%%%%%%%%ZZZZZZZZZZ%ZZ%:::*:::::::
          *:::%%% SWARM ROBOTICS %%%%ZZZZZZZZZZ%%%*::::*::::
        *:::::::%%%%%%%%%%%%%%%%%%%%%%%ZZZZZ%%      *:::Z
       **:ZZZZ:::%%%%%%%%%%%%%%%%%%%%%%%%%%%ZZ      ZZZZZ
      *:ZZZZZZZ       %%%%%%%%%%%%%%%%%%%%%ZZZZ    ZZZZZZZ
     *::::ZZZZZZ         %%%%%%%%%%%%%%%ZZZZZZZ      ZZZ
      *::ZZZZZZ           Z%%%%%%%%%%%ZZZZZZZ%%
        ZZZZ              ZZZZZZZZZZZZZZZZ%%%%%
                         %%%ZZZZZZZZZZZ%%%%%%%%
                        Z%%%%%%%%%%%%%%%%%%%%%
                        ZZ%%%%%%%%%%%%%%%%%%%
                        %ZZZZZZZZZZZZZZZZZZZ
                        %%ZZZZZZZZZZZZZZZZZ
                         %%%%%%%%%%%%%%%%
                          %%%%%%%%%%%%%
                           %%%%%%%%%
                            ZZZZ
                            ZZZ
                           ZZ
                          Z
                         /



/*          Variables
    CHANGES HERE ARE ACCEPTABLE
*/

const int POWER_INCREMENT = 15;
int power = 0;
const int LED13 = 13;
boolean startHub = false;

void setup()
{
  Serial.begin(9600);  //Arduino's serial port for displaying incoming data
  Serial3.begin(9600); //Bluetooth's serial port for receiving data

  pinMode(13,OUTPUT);
  pinMode(12,OUTPUT);
  pinMode(11,OUTPUT);
  pinMode(10,OUTPUT);

  servo0.attach(31);
  
  //waitForSwarmConnection();
  
  

}//end setup

void loop(){
  if(!enableSwarmMind) {
    routeControllerInput( receiveData() );
  }else{
    initSwarmMind();
  }
  

}//end loop

void changeColor(int r, int g, int b){
  
  analogWrite(10,r); //red
  analogWrite(12,g); //green (green indicates ready to go)
  analogWrite(11,b); //blue
  
}// end ChangeColor()


// waiting for swarm connection
void waitForSwarmConnection(){
  Serial.println("***Starting Swarm Contol Hub***");
  Serial.print("Connecting to Swarm...");
  
  String linked = " ";
  boolean state = HIGH;
  
  while( linked != "connectToHub" ){
    Serial.print(".");
    delay(150);
    state = !state; //toggle state
    digitalWrite(11,state);//blink blue led 
    linked = receiveData();
    
  }
  
  // Resumes program when connected to swarm
  
  //changeColor(r,g,b)
  changeColor(0,255,0);// green for good to GO
  
}// end waitForSwarmConnection()


// Routing controller data from the received bluetooth data
void routeControllerInput(String controllerInput) {     
  Serial.println("Controller Input: "+controllerInput);
  if ( controllerInput == "enableSwarmMind") {
    enableSwarmMind = true;
  }
  
  if ( controllerInput == "batteryCheck") {
    
    double batteryVoltage = 87.3;
    String batteryHealth = (String)batteryVoltage+"%";
    Serial3.println("  "+batteryHealth);//send msg via bluetooth
    //Serial.println("Battery health at: "+ batteryHealth+" sent");
    
    
  }
  
  if ( controllerInput == "Start") {
    enableSwarmMind = true;
  }
  if ( controllerInput == "X") {
//    servo0.write(20);
    power = 0;

  }
  if ( controllerInput == "A") {
    dcMotor1.setDirection(FORWARD);
    dcMotor2.setDirection(FORWARD);
    dcMotor3.setDirection(FORWARD);
    dcMotor4.setDirection(FORWARD);


  }
  if ( controllerInput == "B") {
    dcMotor1.setDirection(REVERSE); //REVERSE
    dcMotor2.setDirection(REVERSE); //REVERSE
    dcMotor3.setDirection(REVERSE); //REVERSE
    dcMotor4.setDirection(REVERSE); //REVERSE

  }
  if ( controllerInput == "L") {
//    servo0.write(0);


  }
  if ( controllerInput == "U") {
    if (power < 255) {
      power += POWER_INCREMENT;
    }

    

  }//end 'U'
  if ( controllerInput == "D") {
    if (power > 0) {
      power -= POWER_INCREMENT;
    }
    

  }//end 'D'
  if ( controllerInput == "R") {
   servo0.write(180);

    
  }// end 'R'
  if ( controllerInput == "L1") {
    


  }
  if ( controllerInput == "R1") {

    
    

  }
  
  //set power to motor
  dcMotor1.setMotorPower(power);
  dcMotor2.setMotorPower(power);
  dcMotor3.setMotorPower(power);
  dcMotor4.setMotorPower(power);
  delay(10);

}// routeControllerInput()


//Receiving bluetooth Data
String receiveData() {

  String bluetoothData;
  char incomingByte;
  while (Serial3.available()) {
    incomingByte = Serial3.read();
    bluetoothData.concat(incomingByte);
    if (incomingByte == '\r') { // find if there is carriage return Break out of loop
      //Serial.println( "BluetoothData: "+bluetoothData );
      break;
    }
  }
  return bluetoothData;
}//end receiveData()


/******Autonomous Mode Functions*******/

// Start receiveing data from android telemetry
void initSwarmMind() {

  String tempKey;
  String tempVal;
  while (Serial3.available()) {
    //Here we check for our Key data
    tempKey = Serial3.readStringUntil('=');
    tempKey.trim();

    if (tempKey.startsWith("{")) {
      tempKey.remove(0, 1);
    }
    //    Serial.println("Key: " + String(tempKey));

    if (tempKey != "") {
      //Here we check the Value data
      tempVal = Serial3.readStringUntil(',');
      tempVal.trim();

      if ( tempVal.endsWith( "}" ) ) {
        tempVal.remove(tempVal.length() - 1, 1);
      }

      //      Serial.println("Value: " + String(tempVal));
      Serial.println();
    }


  }//end while()

  routeA2AData(tempKey, tempVal);
  tempKey = "";
  tempVal = "";

}//endSwarmind


// Routing Data to run autonomously
void routeA2AData(String key, String val) {
  key.trim();
  val.trim();


  if ( key == "delay" ) {

    autoDelay = (unsigned long)val.toFloat();
    Serial.print("****Delay value: " + String(autoDelay));

  }//end if == delay




  else if ( key == "m1_name" ) {
    
    dcMotor1.setPortName(val);
    Serial.println("****Motor name changed to: " + dcMotor1.getPortName());

  }//end if == m1_name





  else if ( key == "m1_power" ) {

    if (val.startsWith("-")) {
      
      val.remove(0, 1);
      dcMotor1.setDirection( REVERSE );
      
    } else {
      
      dcMotor1.setDirection( FORWARD );
    }
    power = val.toDouble();
    dcMotor1.setMotorPower(power);
    Serial.println("****Motor 1 power set to: " + String( dcMotor1.getMotorPower() ));

  }//end if key == m1_power




  else if( key == "buzz_kill" ){
    enableSwarmMind = false;
  }

}//end routeA2AData()
