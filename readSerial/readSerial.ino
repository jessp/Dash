
//informed by http://stackoverflow.com/questions/5697047/convert-serial-read-into-a-useable-string-using-arduino
String receivedString;
String oneMessage[20];
String completedMessage = "";

void setup() {
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
      if (int(oneMessage.charAt(i) - '0') == 0){
          Bean.setLed(0,0,255);  // blue
      } else if (int(oneMessage.charAt(i) - '0') == 1){
          Bean.setLed(255,0,0);  // blue
      } else {
          Bean.setLed(0,255,0);  // blue
      }
    }
    Bean.setLed(0,0,0);
}


