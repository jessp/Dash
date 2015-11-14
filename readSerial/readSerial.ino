
//informed by http://stackoverflow.com/questions/5697047/convert-serial-read-into-a-useable-string-using-arduino
String receivedString;

void setup() {
  // Bean Serial is at a fixed baud rate. Changing the value in Serial.begin() has no effect.
  Serial.begin();
}


void loop() {
  if(Serial.available() > 0)
    {
        receivedString = Serial.readStringUntil('~');
        Serial.println(receivedString);
        if (receivedString.equals("Echo")){
          Serial.println("Ready for duty!");
        }
    }

}



