
//informed by http://stackoverflow.com/questions/5697047/convert-serial-read-into-a-useable-string-using-arduino
String receivedString;
String oneMessage[20];
String completedMessage = "";
const int outPin = 5;
int oneNote = 100;
int longNote = 3; //dash
int shortNote = 1; //dots
int innerGap = 1; //gap between dashes/dots in single letter
int letterGap = 3; //gap between letters
int wordGap = 7; //gap between words
int currentTone = 0; //keep track of current note played

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
  if (currentTone == 0) {
      digitalWrite(outPin,HIGH);
      Bean.sleep(oneNote * shortNote);
      digitalWrite(outPin,LOW);
      Bean.sleep(10);
  } else if (currentTone == 1) {
      digitalWrite(outPin,HIGH);
      Bean.sleep(oneNote * longNote);
      digitalWrite(outPin,LOW);
      Bean.sleep(10);
  } else if (currentTone == 2){ 
      digitalWrite(outPin,LOW);
      Bean.sleep(oneNote * innerGap);                              
  } else if (currentTone == 3){ 
      digitalWrite(outPin,LOW);
      Bean.sleep(oneNote * letterGap);                             
  } else if (currentTone == 4){ 
      digitalWrite(outPin,LOW);
      Bean.sleep(oneNote * wordGap);                     
  }                         
}

