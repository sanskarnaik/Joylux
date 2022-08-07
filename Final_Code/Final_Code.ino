#include "Button.h"
#include <SoftwareSerial.h>

//Bluetooth Part:
SoftwareSerial BTserial(2, 3); // RX | TX
char btValue;
  //all mode selection values:
bool BT5min = false;
bool BT10min = false;
bool BT15min = false;
bool BTvibMode = false;
bool BTthrMode = false;
bool BTvibrator = false;
bool BTdeviceOn = false;
char BTvibSpeed;
int count = 0;


//Button logic:
Button button(11);
bool device_on;
bool led_mode;
bool motor_mode = false;
bool led_off = true;

//Led Code:
//Digital pins:
int LED_PIN1 = 9;
int LED_PIN2 = 8;
int LED_PIN3 = 7;
int LED_PIN4 = 6;
int LED_PIN5 = 5;
int LED_PIN6 = 4;
int BUTTON_PIN1 = 14;
int BUTTON_PIN2 = 19;
int val_led = 0;
int flag = 0;
int status_flag = 0;
int t = 0;
bool reset_flag = true;
int r = 0;
int ledState = LOW;
unsigned long previousMillis;
unsigned long currentMillis;
unsigned long previousMillis_led1 = 0;
unsigned long previousMillis_led2 = 0;
unsigned long previousMillis_led3 = 0;
unsigned long previousMillis_prs = 0;


//Pressure Sensor code:
float cf = 33.5;
float offset = 0.00558;
//analog pins:
int fsrPin1 = 1;
int fsrPin2 = 2;
int ffsdata = 0;
float vout1;
float vout2;
float voutf;
bool prsState = true;

//pin for temp sensor:
int temppin = 7;
float tempdata;

//pin for motor:
int motorpin1 = 10; 
// int motorpin2 = 12;
char s;
char buffer [33];
int val_motor = 0;

void setup() {
  //Led Code:
  Serial.begin(9600);
  BTserial.begin(9600); 
  pinMode(LED_PIN1, OUTPUT);
  pinMode(LED_PIN2, OUTPUT);
  pinMode(LED_PIN3, OUTPUT);
  pinMode(LED_PIN4, OUTPUT);
  pinMode(LED_PIN5, OUTPUT);
  pinMode(LED_PIN6, OUTPUT);
  pinMode(BUTTON_PIN1, INPUT);
  pinMode(BUTTON_PIN2, INPUT);
  previousMillis = millis();
  Serial.println("You can select between 3 modes:"); 
  Serial.println("1->10sec therapy, press the green button 1 time.");
  Serial.println("2->20sec therapy, press the green button 2 times.");
  Serial.println("3->30sec therapy, press the green button 3 times.");
  Serial.println("For going back to previous mode:");
  Serial.println("Just press the red button!!");

  //Pressure sensor code:
  pinMode(fsrPin1, INPUT);
  pinMode(fsrPin2, INPUT);

  //Temperature Sensor code:
  pinMode(temppin, INPUT);

  //Motor code:
  pinMode(motorpin1, OUTPUT);
  pinMode(motorpin1, OUTPUT);
  // pinMode(motorpin2, OUTPUT);
  // pinMode(motorpin2, OUTPUT);

  //button logic:
  button.on_press(on_press);
  button.on_long_press(on_long_press);
  button.set_maximum_gap(5000);
//  pinMode(Button_Pin3, INPUT);
}

void handler(const char btValue) 
{
  switch(btValue) {
    case 'O':
      // BTdeviceOn = true;
      // BTthrMode = true;
      // BTvibMode = false;
      on_long_press();
      break;
    case 'F':
      // BTdeviceOn = false;
      on_long_press();
      break;
    case 'A':
      val_led = 1;
      // BT5min = true;
      // BT10min = false;
      // BT15min = false;
      t=1;
      break;
    case 'B':
      val_led = 2;
      // BT10min = true;
      // BT5min = false;
      // BT15min = false;
      t=1;
      break;
    case 'C':
      val_led = 3;
      // BT15min = true;
      // BT5min = false;
      // BT10min = false;
      t=1;
      break;
    case 'V':
      // BTvibMode = true;
      // BTthrMode = false;
      on_press();
      break;
    case 'T':
      // BTthrMode = true;
      // BTvibMode = false;
      // val_led = 0;
      on_press();
      break;
    default:
      // BTvibrator = true;
      // BTvibMode = false;
      // BTvibSpeed = btValue;
      s = btValue;
  }
}

void loop() {

  //button logic:
  button.init();

  //start pressure timer:
  pressure_timer(1000);

  //bluetooth logic:
  if (BTserial.available()) { 
    char c = BTserial.read();
    handler(c);
    Serial.println(c);
  }
  
  //code to hold and wait for the user:*/
  if(device_on) {

      if(led_mode) {

        //Stop motor once in LED Mode:
        analogWrite(motorpin1, 0);

        //turn off all led's before mode switching:
        if (led_off) {
          off_all_led();
          led_off = false;
        }

        if (r == 0) {
          if (digitalRead(BUTTON_PIN1) == HIGH) {
            
            if (val_led < 3) {
              val_led++;
              Serial.println(val_led);
              delay(500);
              reset_timer();
              t=1;
            } else {
              val_led = 3;
              //Serial.println("No such Mode");
              delay(500);
            }
          }

          if (digitalRead(BUTTON_PIN2) == HIGH) {
            
            if (val_led > 0) {
              val_led--;
              Serial.println(val_led);
              delay(500);
              reset_timer();
              t=1;
            } else {
              Serial.println("No such Mode");
              delay(500);
            }
          }
        }
      
      
        //code for delay:

        if (t==1) {
          // timer_to_count(60000);
          if (val_led == 1) {
            digitalWrite(LED_PIN1, LOW);
            digitalWrite(LED_PIN5, LOW);
            led_blink_timer1(1000);
          } else if (val_led == 2) {
            digitalWrite(LED_PIN5, LOW);
            led_blink_timer2(1000);
          } else if (val_led == 3) {
            led_blink_timer3(1000);
          } else {
            off_all_led();
            Serial.println("You are in the Therapy Mode!!");
          }
        }
      }

      if(motor_mode) {

       //turn off all led's before mode switching:
        if (!led_off) {
          //off_all_led();
          led_off = true;
        }

       //therapy on logic:
       if (reset_flag) {
          reset_timer();
          reset_flag = false;
        }
      
        if (!reset_flag) {
          // r=1;
          switch(val_led) {
          case(1):
            Serial.println("1st mode selected!!");
            timer_for_led(10000);
            break;
          case(2):
            Serial.println("2nd mode selected!!");
            timer_for_led(20000);
            break;
          case(3):
            Serial.println("3rd mode selected!!");
            timer_for_led(30000);
            break;
          default:
            Serial.println("You are in the Vibration Mode!!");
            // r=0;
          }
        }   

       //code for motor:
        if (digitalRead(BUTTON_PIN1) == HIGH) {
          if (val_motor < 9) {
            val_motor++;
            itoa (val_motor,buffer,10);
            s = *buffer;
            delay(500);
          } else {
            Serial.println("Max speed reached");
          }

        }
        if (digitalRead(BUTTON_PIN2) == HIGH) {
          if (val_motor > 0) {
            val_motor--;
            itoa (val_motor,buffer,10);
            s = *buffer;
            delay(500);
          } else {
            Serial.println("Min speed reached");
          }
        }

        if (s == '0' || BTvibSpeed == '0') {analogWrite(motorpin1, 0)  ; Serial.println("Speed is = 0");}
        if (s == '1' || BTvibSpeed == '1') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 175) ; Serial.println("Speed is = 1");}
        if (s == '2' || BTvibSpeed == '2') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 185) ; Serial.println("Speed is = 2");}
        if (s == '3' || BTvibSpeed == '3') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 195); Serial.println("Speed is = 3");}
        if (s == '4' || BTvibSpeed == '4') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 205); Serial.println("Speed is = 4");}
        if (s == '5' || BTvibSpeed == '5') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 215); Serial.println("Speed is = 5");}
        if (s == '6' || BTvibSpeed == '6') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 225); Serial.println("Speed is = 6");}
        if (s == '7' || BTvibSpeed == '7') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 235); Serial.println("Speed is = 7");}
        if (s == '8' || BTvibSpeed == '8') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 245); Serial.println("Speed is = 8");}
        if (s == '9' || BTvibSpeed == '9') {analogWrite(motorpin1, 255); delay(10); analogWrite(motorpin1, 255); Serial.println("Speed is = 9");}
      }

  
  
  //  ffsdata = analogRead(fsrPin2);
  //  vout = (ffsdata *5.0)/ 1023.0;
  //  vout = vout * cf + offset;
  //  Serial.print("Sensor2: ");
  //  Serial.print(vout,3);
  //  Serial.println("");
  //  //Serial.print("Offset value: ");
  //  //Serial.print(offset);
  //  delay(100);

  //   //Code for Temperature Sensor:
  //   tempdata = analogRead(temppin);
  //   tempdata = tempdata * 0.48828125;
  //   Serial.print("Temperature: ");
  //   Serial.print(tempdata);
  //   Serial.println("");
  //   delay(100);

  } else {
     Serial.println("Device is OFF!!");
//    Serial.println("Press the power button for atleat 5 sec to TURN ON the device!!");
  }   
}
void timer_for_led(const long timing) {

  if (status_flag == 0) {
    currentMillis = millis();
  }
  else {
    currentMillis = 0;
  }
  if ( currentMillis - previousMillis >= timing) {
    if (flag == 0) {
      Serial.print("timer started for ");
      Serial.print(timing/1000);
      Serial.print("s");
      flag = 1;
      digitalWrite(LED_PIN1, HIGH);
      digitalWrite(LED_PIN2, HIGH);
      digitalWrite(LED_PIN3, HIGH);
      digitalWrite(LED_PIN4, HIGH);
      digitalWrite(LED_PIN5, HIGH);
      digitalWrite(LED_PIN6, HIGH);
      previousMillis = currentMillis;
    }
    else {
      digitalWrite(LED_PIN1, LOW);
      digitalWrite(LED_PIN2, LOW);
      digitalWrite(LED_PIN3, LOW);
      digitalWrite(LED_PIN4, LOW);
      digitalWrite(LED_PIN5, LOW);
      digitalWrite(LED_PIN6, LOW);
      status_flag = 1;
      // t=-10;
      // k=-10;
      // r=0;
      val_led=0;
      Serial.println("timer stopped!!");
    }
  }
}
void reset_timer() {
  status_flag = 0;
  flag = 0;
  previousMillis = 0;
}
void led_blink_timer1(const long interval) {
  unsigned long currentMillis_led = millis();
  if (currentMillis_led - previousMillis_led1 >= interval) {
    // save the last time you blinked the LED
    previousMillis_led1 = currentMillis_led;

    // if the LED is off turn it on and vice-versa:
    if (ledState == LOW) {
      ledState = HIGH;
    } else {
      ledState = LOW;
    }

    // set the LED with the ledState of the variable:
    digitalWrite(LED_PIN4, ledState);
  }
}
void led_blink_timer2(const long interval) {
  unsigned long currentMillis_led = millis();
  if (currentMillis_led - previousMillis_led2 >= interval) {
    // save the last time you blinked the LED
    previousMillis_led2 = currentMillis_led;

    // if the LED is off turn it on and vice-versa:
    if (ledState == LOW) {
      ledState = HIGH;
    } else {
      ledState = LOW;
    }

    // set the LED with the ledState of the variable:
    digitalWrite(LED_PIN4, ledState);
    digitalWrite(LED_PIN1, ledState);
  }
}
void led_blink_timer3(const long interval) {
  unsigned long currentMillis_led = millis();
  if (currentMillis_led - previousMillis_led3 >= interval) {
    // save the last time you blinked the LED
    previousMillis_led3 = currentMillis_led;

    // if the LED is off turn it on and vice-versa:
    if (ledState == LOW) {
      ledState = HIGH;
    } else {
      ledState = LOW;
    }

    // set the LED with the ledState of the variable:
    digitalWrite(LED_PIN4, ledState);
    digitalWrite(LED_PIN1, ledState);
    digitalWrite(LED_PIN5, ledState);
  }
}
void off_all_led() {
  digitalWrite(LED_PIN1, LOW);
  digitalWrite(LED_PIN2, LOW);
  digitalWrite(LED_PIN3, LOW);
  digitalWrite(LED_PIN4, LOW);
  digitalWrite(LED_PIN5, LOW);
  digitalWrite(LED_PIN6, LOW);
}
void on_press() {
  if(device_on) {
    if(led_mode) {
      motor_mode = true;
      led_mode = false;
      val_motor = 0;
      s = '0';
      Serial.println(val_led);
//      buffer = '0';
    } else {
      led_mode = true;
      motor_mode = false;
      val_led = 0;
    }
  }
  // Serial.print(String("button pressed immediately: ") + button.gap() + String(" milliseconds\n"));
  
}
void on_long_press() {
  led_mode = true;
  motor_mode = false;
  if(device_on) {
    device_on = false;
    off_all_led();
  } else {
    device_on = true;
  }
  Serial.println("Device turned ON!!");
  // Serial.print(String("button pressed after long time: ") + button.gap() + String(" milliseconds\n"));
}
void pressure_timer(const long interval) {
  unsigned long currentMillis_prs = millis();
  if (currentMillis_prs - previousMillis_prs >= interval) {
    // save the last time you blinked the LED
    previousMillis_prs = currentMillis_prs;

    // if the pressure is off turn it on and vice-versa:
    if (prsState) {
      prsState = false;
      //Code for pressure sensor:
      ffsdata = analogRead(fsrPin1);
      vout1 = (ffsdata *25.0)/ 1023.0;
      vout1 = vout1 * cf + offset;

      ffsdata = analogRead(fsrPin2);
      vout2 = (ffsdata *5.0)/ 1023.0;
      vout2 = vout2 * cf + offset;
//        Serial.print("FSR1: ");
//        Serial.print(vout1,3);
//        Serial.println("");
//        Serial.print("FSR2: ");
//        Serial.print(vout2,3);
//        Serial.println("");

      voutf = (vout1 + vout2)/2;
      BTserial.write(voutf);
    } else {
      prsState = true;
      //Code for pressure sensor:
      ffsdata = analogRead(fsrPin1);
      vout1 = (ffsdata *25.0)/ 1023.0;
      vout1 = vout1 * cf + offset;

      ffsdata = analogRead(fsrPin2);
      vout2 = (ffsdata *5.0)/ 1023.0;
      vout2 = vout2 * cf + offset;
//        Serial.print("FSR1: ");
//        Serial.print(vout1,3);
//        Serial.println("");
//        Serial.print("FSR2: ");
//        Serial.print(vout2,3);
//        Serial.println("");

      voutf = (vout1 + vout2)/2;
      BTserial.write(voutf);
    }
  }
}

//-----------------------------------------------------------------------------------------------------------------------------------
//Spare Code:
// void timer_to_count(const long timing) {

//     if (status_flag == 0) {
//       currentMillis = millis();
//     }
//     else {
//       currentMillis = 0;
//     }
//     if ( currentMillis - previousMillis >= timing) {
//       if (flag == 0) {
//         Serial.print("timer started for ");
//         Serial.print(timing/1000);
//         Serial.print("s");
//         flag = 1;
//         //digitalWrite(LED_PIN, HIGH);
//         previousMillis = currentMillis;
//       }
//       else {
//         //digitalWrite(LED_PIN, LOW);
//         status_flag = 1;
//         // t=0;
//         // k=1;
//         Serial.println("timer stopped!!");
//       }
//     }
//     // Serial.print("Val: ");
//     // Serial.println(val);
//     //   switch(val) {
//     //   case(1):
//     //     digitalWrite(LED_PIN1, HIGH);
//     //     delay(200);
//     //     digitalWrite(LED_PIN1, LOW);
//     //     delay(200);
//     //   case(2):
//     //     digitalWrite(LED_PIN1, HIGH);
//     //     digitalWrite(LED_PIN2, HIGH);
//     //     delay(200);
//     //     digitalWrite(LED_PIN1, LOW);
//     //     digitalWrite(LED_PIN2, LOW);
//     //     delay(200);
//     //    case(3):
//     //      digitalWrite(LED_PIN1, HIGH);
//     //      digitalWrite(LED_PIN2, HIGH);
//     //      digitalWrite(LED_PIN3, HIGH);
//     //      delay(200);
//     //      digitalWrite(LED_PIN1, LOW);
//     //      digitalWrite(LED_PIN2, LOW);
//     //      digitalWrite(LED_PIN3, LOW);
//     //      delay(200);
//     //   }
// }
//-----------------------------------------------------------------------------------------------------------------------------------
