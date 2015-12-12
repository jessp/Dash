package com.example.jessicapeter.newmorsereader;

//https://github.com/samanyukansara/smartplants/blob/master/app/src/main/java/nz/ac/aucklanduni/smartplants/BeanServices/BeanConnector.java

/**
 * Created by jessicapeter on 2015-11-08.
 */


import android.content.Context;
import android.util.Log;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//maybe look at below url for more complete example
//https://github.com/PunchThrough/Bean-Android-SDK/blob/master/sdk/src/androidTest/java/com/punchthrough/bean/sdk/TestBean.java




public class BeanClass {

    private static final String TAG = "BeanLog";
    private static final String BEAN_NAME = "lle";

    protected Context context;
    protected Bean myBean;
    int currentMessageIterator = 0;
    ArrayList<String> messageToSend;
    int attemptsIterator = 0;

    boolean waitingForReceipt = false;



    public BeanClass(Context context) {
        this.context = context;
        this.myBean = null;
    }

    public Boolean isConnected() {
        return this.myBean.isConnected();
    }

    public void findBean(){
        Log.d(TAG, "Start Search...");
        if (myBean == null) {
            Log.d(TAG, "Starting Bean search...");
            BeanManager.getInstance().startDiscovery(discoveryListener);
        }
    }

    public void sendMessage(String message){
        if (myBean != null) {
            attemptsIterator = 0;
            Log.d(TAG, "" + myBean.isConnected());
            ArrayList<String> thisMessage = new ArrayList<String>();
            int index = 0;
            messageToSend = thisMessage;

            while (index < message.length()) {
                thisMessage.add(message.substring(index, Math.min(index + 30,message.length())));
                index += 30;
            }
                waitingForReceipt = true;
                Log.d(TAG, "" + thisMessage.get(0));
                if (0 == thisMessage.size()-1){
                    myBean.sendSerialMessage(thisMessage.get(0) + "`~");
                } else {
                    myBean.sendSerialMessage(thisMessage.get(0) + "~");
                }


        } else {
            Log.d(TAG, "Bean not connected :(");
//            add code in to locate bean
        }
    }

    void proceedWithNextChunk(){
        attemptsIterator = 0;
        if (currentMessageIterator != messageToSend.size()-1) {
            currentMessageIterator++;
            if (currentMessageIterator == messageToSend.size() - 1) {
                myBean.sendSerialMessage(messageToSend.get(currentMessageIterator) + "`~");
            } else {
                myBean.sendSerialMessage(messageToSend.get(currentMessageIterator) + "~");
            }
        } else {
            currentMessageIterator = 0;
            waitingForReceipt = false;
        }
    }

    void repeatLastChunk(){
        attemptsIterator++;
        if (attemptsIterator < 5) {
            if (currentMessageIterator == messageToSend.size() - 1) {
                myBean.sendSerialMessage(messageToSend.get(currentMessageIterator) + "`~");
            } else {
                myBean.sendSerialMessage(messageToSend.get(currentMessageIterator) + "~");
            }
        } else {
            Log.v(TAG, "Give up on this one");
            waitingForReceipt = false;
        }
    }




    public void sync() {
        Log.d(TAG, "Start Sync...");
        if (myBean == null) {
            Log.d(TAG, "Starting Bean discovery...");
            BeanManager.getInstance().startDiscovery(discoveryListener);
        }
        if (!myBean.isConnected()) {
            Log.d(TAG, "Starting to connect to bean named " + BEAN_NAME);
            myBean.connect(this.context, beanListener);
        }
        Log.d(TAG, "Sync complete...");
    }

    private BeanDiscoveryListener discoveryListener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            Log.d(TAG, "Bean found");

            if (bean.getDevice().getName().equals(BEAN_NAME)) {
                Log.d(TAG, "Found my bean");
                myBean = bean;
                sync();
            } else {
                Log.d(TAG, "Other bean found named: " + bean.getDevice().getName());
            }
        }

        @Override
        public void onDiscoveryComplete() {
            Log.d(TAG, "Bean Discovery Complete");
        }
    };

    private BeanListener beanListener = new BeanListener() {
        @Override
        public void onConnected() {
            Log.d(TAG, "Connected!");
        }



        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onSerialMessageReceived(byte[] bytes) {
            String myMessage = new String(bytes, StandardCharsets.UTF_8);
            if (!myMessage.trim().equals("")) {
                Log.d(TAG, "onSerialMessageReceived: " + new String(bytes, StandardCharsets.UTF_8));
                if (waitingForReceipt){
                        if (myMessage.trim().equals("n")){
                            Log.v(TAG, "yes, tis true");
                            repeatLastChunk();
                        } else {
                            proceedWithNextChunk();
                        }
                    }
                }
        }

        @Override
        public void onScratchValueChanged(ScratchBank scratchBank, byte[] bytes) {
//            Log.d(TAG, "onScratchValueChanged " + bytes);
        }

        @Override
        public void onError(BeanError beanError) {
            Log.d(TAG, "Error! " + beanError);
        }
    };



}