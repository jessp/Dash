package com.example.jessicapeter.newmorsereader;

//https://github.com/samanyukansara/smartplants/blob/master/app/src/main/java/nz/ac/aucklanduni/smartplants/BeanServices/BeanConnector.java

/**
 * Created by jessicapeter on 2015-11-08.
 */


import android.content.Context;
import android.os.AsyncTask;
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
    boolean isReceived = false;
    String mostRecentMessage = "";
    boolean waitingForReceipt = false;
//    SendMessageChunks myAsyncHandler = new SendMessageChunks();


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
            ArrayList<String> thisMessage = new ArrayList<String>();
            int index = 0;

            while (index < message.length()) {
                thisMessage.add(message.substring(index, Math.min(index + 30,message.length())));
                index += 30;
            }

            SendMessageChunks myAsyncHandler = new SendMessageChunks();
            myAsyncHandler.execute(thisMessage);

//            for (int i = 0; i < thisMessage.size(); i++){
//                Log.d(TAG, "" + thisMessage.get(i));
//                myBean.sendSerialMessage(thisMessage.get(i) + "~");
//                SystemClock.sleep(1000);
//            }

            myBean.sendSerialMessage("`~");
        } else {
            Log.d(TAG, "Bean not connected :(");
//            findBean();
//            myBean.sendSerialMessage(message);
        }
    }

    private class SendMessageChunks extends AsyncTask<ArrayList<String>, Void, Void> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        int currentIterator;
        ArrayList<String> currentArray;
        int messageCounter = 0;

        protected Void doInBackground(ArrayList<String>... theMessage) {
            waitingForReceipt = true;

            currentArray = theMessage[0];
            currentIterator = 0;
                    if (currentIterator == theMessage[0].size() - 1) {
                        myBean.sendSerialMessage(theMessage[0].get(currentIterator) + "`~");
                    } else {
                        myBean.sendSerialMessage(theMessage[0].get(currentIterator) + "~");
                    }
            messageCounter ++;
            Log.v(TAG, "" + messageCounter);
            return null;
        }

        void proceed(){

            //if we received the message correctly...
            if (mostRecentMessage.equals(currentArray.get(currentIterator))){
                Log.v(TAG, "Yay :)");
                Log.v(TAG, "Most recent: " + mostRecentMessage);
                messageCounter = 0;
                //check if we've been through every part of the array
                if (currentIterator != currentArray.size()-1){
                    currentIterator += 1;
                    if (currentIterator == currentArray.size() - 1) {
                        myBean.sendSerialMessage(currentArray.get(currentIterator) + "`~");
                    } else {
                        myBean.sendSerialMessage(currentArray.get(currentIterator) + "~");
                    }

                } else {
                    waitingForReceipt = false;
                    Log.v(TAG, "We got the whole message!");
                }
                //but if we haven't received the message correctly...
            } else {
                messageCounter ++;
                Log.v(TAG, "Nay :(");
                Log.v(TAG, "Most recent: " + mostRecentMessage);
                messageCounter ++;
                if (messageCounter < 5){
                    if (currentIterator != currentArray.size()-1){
                        currentIterator += 1;
                        if (currentIterator == currentArray.size() - 1) {
                            myBean.sendSerialMessage(currentArray.get(currentIterator) + "`~");
                        } else {
                            myBean.sendSerialMessage(currentArray.get(currentIterator) + "~");
                        }

                    }
                } else {
                    waitingForReceipt = false;
                    Log.v(TAG, "Give up on this message, bro");
                }

            }
        }



        protected void onPostExecute(Boolean result) {
            if (result){
                Log.v(TAG, "finished!");
            } else {
                Log.v(TAG, "still going!");
            }

//            showDialog("Downloaded " + result + " bytes");
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
            Log.d(TAG, "onSerialMessageReceived: " + new String(bytes, StandardCharsets.UTF_8));
            isReceived = true;
            mostRecentMessage = bytes.toString();
            if (waitingForReceipt){
                myAsyncHandler.proceed();
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