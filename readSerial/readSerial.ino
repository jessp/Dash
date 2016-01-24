
//informed by http://stackoverflow.com/questions/5697047/convert-serial-read-into-a-useable-string-using-arduino
String receivedString;
String oneMessage[20];
String completedMessage = "";
const int outPin = 5;
int oneNote = 3000;
int longNote = 6; //dash
int shortNote = 1; //dots
int innerGap = 1; //gap between dashes/dots in single letter
int letterGap = 6; //gap between letters
int wordGap = 14; //gap between words
int currentTone = 0; //keep track of current note played
int rest_count = 200;

void setup() {
  pinMode(outPin, OUTPUT);
  // Bean Serial is at a fixed baud rate. Changing the value in Serial.begin() has no effect.
  Serial.begin();
}


void loop() {
  if(Serial.available() > 0) {
            receivedString = Serial.readStringUntil('~');
            if (receivedString.length() != 30 && receivedString.charAt(receivedString.length()-1) != '`'){
              Serial.println("n");
            } else {
              Serial.println(receivedString); 
              completedMessage = completedMessage + receivedString;
              if (receivedString.charAt(receivedString.length()-1) == '`'){
              sonifyMessage(completedMessage);
              completedMessage = "";
              }
            }
        }
    }

void sonifyMessage(String oneMessage){
    for (int i = 0; i < oneMessage.length(); i++){
        currentTone = int(oneMessage.charAt(i) - '0');
        playTone();
    }

}


void playTone() {
  long elapsed_time = 0;
  if (currentTone == 0) {
    //  played less long than 'duration', pulse speaker HIGH and LOW
    while (elapsed_time < (oneNote * shortNote)) {

      digitalWrite(outPin,HIGH);

      // Keep track of how long we pulsed
      elapsed_time ++;
    } 
    digitalWrite(outPin,LOW);
    Bean.sleep(50);
  }  else if (currentTone == 1) {
    //  played less long than 'duration', pulse speaker HIGH and LOW
    while (elapsed_time < (oneNote * longNote)) {

      digitalWrite(outPin,HIGH);

      // Keep track of how long we pulsed
      elapsed_time ++;
    } 
    digitalWrite(outPin,LOW);
    Bean.sleep(50);
  }
  else if (currentTone == 2){ 
     //  played less long than 'duration', pulse speaker HIGH and LOW
    while (elapsed_time < (oneNote * innerGap)) {

      // Keep track of how long we pulsed
      elapsed_time ++;
    }                                 
  }  else if (currentTone == 3){ 
     //  played less long than 'duration', pulse speaker HIGH and LOW
    while (elapsed_time < (oneNote * letterGap)) {

      // Keep track of how long we pulsed
      elapsed_time ++;
    }                                 
  }  
  else if (currentTone == 4){ 
     //  played less long than 'duration', pulse speaker HIGH and LOW
    while (elapsed_time < (oneNote * wordGap)) {

      // Keep track of how long we pulsed
      elapsed_time ++;
    }                                 
  }  
                           
}

