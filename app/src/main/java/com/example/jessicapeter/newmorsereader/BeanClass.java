package com.example.jessicapeter.newmorsereader;

//https://github.com/samanyukansara/smartplants/blob/master/app/src/main/java/nz/ac/aucklanduni/smartplants/BeanServices/BeanConnector.java

/**
 * Created by jessicapeter on 2015-11-08.
 */


import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//maybe look at below url for more complete example
//https://github.com/PunchThrough/Bean-Android-SDK/blob/master/sdk/src/androidTest/java/com/punchthrough/bean/sdk/TestBean.java




public class BeanClass {

    private static final String TAG = "BeanLog";
    private static final String BEAN_NAME = "lle";

    protected Context context;
    protected Bean myBean;
    boolean isReceived = false;


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
            Log.d(TAG, "" + myBean.isConnected());
            List<String> thisMessage = new ArrayList<String>();
            int index = 0;

            while (index < message.length()) {
                thisMessage.add(message.substring(index, Math.min(index + 30,message.length())));
                index += 30;
            }

            for (int i = 0; i < thisMessage.size(); i++){
                Log.d(TAG, "" + thisMessage.get(i));
                myBean.sendSerialMessage(thisMessage.get(i) + "~");
                SystemClock.sleep(1000);
            }

            myBean.sendSerialMessage("`~");
        } else {
            Log.d(TAG, "Bean not connected :(");
//            findBean();
//            myBean.sendSerialMessage(message);
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

//            myThread.start();
        }



        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onSerialMessageReceived(byte[] bytes) {
            Log.d(TAG, "onSerialMessageReceived: " + new String(bytes, StandardCharsets.UTF_8));
            isReceived = true;
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


    Thread spamSend = new Thread() {
        @Override
        public void run() {
            while(!isReceived) {
                try {
                    sleep(1000);
                    if (!isReceived) {
                        Log.d(TAG, "Sending Echo");
                        myBean.sendSerialMessage("Echo~");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

//    Thread myThread = new Thread(spamSend);


}