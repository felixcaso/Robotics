//arduino Robot Contorller
//Hi its Eitan
#include <SoftwareSerial.h>
SoftwareSerial BT(2, 3);

char data =' ';

//Right game pad buttons
int A =13;
int Y =12;
int X =11;
int B =10;

// Dpad buttons
int left  = 9;
int up    = 8;
int right = 7;
int down  = 6;

int motor = 5;
int spd = 0;

void setup() 
{
  Serial.begin(9600); 
  BT.begin(9600);
  
  pinMode(A,OUTPUT); //green
  pinMode(Y,OUTPUT); //yellow
  pinMode(X,OUTPUT); // blue
  pinMode(B,OUTPUT); //red

  pinMode(up,OUTPUT); 
  pinMode(down,OUTPUT);
  pinMode(left,OUTPUT);
  pinMode(right,OUTPUT);
  pinMode(motor,OUTPUT);
       
  
}
void loop()
{
  if(Serial.available() > 0)  
  {
    data = (char) Serial.read();
  }

  //Serial.println(data);

  if( data == 'Y'){
    digitalWrite(Y,HIGH);
    digitalWrite(B,LOW);
    digitalWrite(A,LOW);
    digitalWrite(X,LOW);
  }
  if( data == 'X'){
    digitalWrite(Y,LOW);
    digitalWrite(B,LOW);
    digitalWrite(A,LOW);
    digitalWrite(X,HIGH);
  }
  if( data == 'A'){
    digitalWrite(Y,LOW);
    digitalWrite(B,LOW);
    digitalWrite(A,HIGH);
    digitalWrite(X,LOW);
  }
  if( data == 'B'){
    digitalWrite(Y,LOW);
    digitalWrite(B,HIGH);
    digitalWrite(A,LOW);
    digitalWrite(X,LOW);
  }
  if( data == 'L'){
    digitalWrite(left,HIGH);
    digitalWrite(up,LOW);
    digitalWrite(down,LOW);
    digitalWrite(right,LOW);
  }
  if( data == 'U'){
    digitalWrite(left,LOW);
    digitalWrite(up,HIGH);
    digitalWrite(down,LOW);
    digitalWrite(right,LOW);
    digitalWrite(motor,HIGH);
  }
  if( data == 'D'){
    digitalWrite(left,LOW);
    digitalWrite(up, LOW);
    digitalWrite(down,HIGH);
    digitalWrite(right,LOW);
    digitalWrite(motor,LOW);
  }
  if( data == 'R'){
    digitalWrite(left,LOW);
    digitalWrite(up,LOW);
    digitalWrite(down,LOW);
    digitalWrite(right,HIGH);
  }
  
}//end loop                 
