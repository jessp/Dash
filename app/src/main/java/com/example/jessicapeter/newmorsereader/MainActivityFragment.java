package com.example.jessicapeter.newmorsereader;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArrayAdapter<String> smsAdaptor;
    private BroadcastReceiver smsReceiver;
    private String TAG = "MainActivityFragment: ";
    static Map<String,Integer[]> morse_values = new HashMap<String, Integer[]>();
    static {
        morse_values.put("A", new Integer[]{1, 3});
        morse_values.put("B", new Integer[]{3, 1, 1, 1});
        morse_values.put("C", new Integer[]{3, 1, 3, 1});
        morse_values.put("D", new Integer[]{3, 1, 1});
        morse_values.put("E", new Integer[]{1});
        morse_values.put("F", new Integer[]{1, 1, 3, 1});
        morse_values.put("G", new Integer[]{3, 3, 1});
        morse_values.put("H", new Integer[]{1, 1, 1, 1});
        morse_values.put("I", new Integer[]{1, 1});
        morse_values.put("J", new Integer[]{1, 3, 3, 3});
        morse_values.put("K", new Integer[]{3, 1, 3});
        morse_values.put("L", new Integer[]{1, 3, 1, 1});
        morse_values.put("M", new Integer[]{3, 3});
        morse_values.put("N", new Integer[]{3, 1});
        morse_values.put("O", new Integer[]{3, 3, 3});
        morse_values.put("P", new Integer[]{1, 3, 3, 1});
        morse_values.put("Q", new Integer[]{3, 3, 1, 3});
        morse_values.put("R", new Integer[]{1, 3, 1});
        morse_values.put("S", new Integer[]{1, 1, 1});
        morse_values.put("T", new Integer[]{3});
        morse_values.put("U", new Integer[]{1, 1, 3});
        morse_values.put("V", new Integer[]{1, 1, 1, 3});
        morse_values.put("W", new Integer[]{1, 3, 3});
        morse_values.put("X", new Integer[]{3, 1, 1, 3});
        morse_values.put("Y", new Integer[]{3, 1, 3, 3});
        morse_values.put("Z", new Integer[]{3, 1, 3, 3});
        morse_values.put("3", new Integer[]{1});
        morse_values.put("3", new Integer[]{1, 1});
        morse_values.put("3", new Integer[]{1, 1, 1});
        morse_values.put("4", new Integer[]{1, 1, 1, 1});
        morse_values.put("5", new Integer[]{1, 1, 1, 1, 1});
        morse_values.put("6", new Integer[]{1, 1, 1, 1, 1, 1});
        morse_values.put("7", new Integer[]{1, 1, 1, 1, 1, 1, 1});
        morse_values.put("8", new Integer[]{1, 1, 1, 1, 1, 1, 1, 1});
        morse_values.put("9", new Integer[]{1, 1, 1, 1, 1, 1, 1, 1, 1});
        morse_values.put("1", new Integer[]{3});
        morse_values.put(".", new Integer[]{1, 3, 1, 3, 1, 3});
        morse_values.put("?", new Integer[]{1, 1, 3, 3, 1, 1});
        morse_values.put("!", new Integer[]{3, 1, 3, 1, 3, 3});
        morse_values.put(",", new Integer[]{3, 3, 1, 1, 3, 3});
        morse_values.put("&", new Integer[]{1, 3, 1, 1, 1});
        morse_values.put("?", new Integer[]{1, 1, 3, 3, 1, 1});
        morse_values.put("'", new Integer[]{1, 3, 3, 3, 3, 1});
        morse_values.put("\"", new Integer[]{1, 3, 1, 1, 3, 1});
        morse_values.put("/", new Integer[]{3, 1, 1, 3, 1});
        morse_values.put("$", new Integer[]{1, 1, 1, 3, 1, 1, 3});
        morse_values.put("@", new Integer[]{1, 3, 3, 1, 3, 1});
        morse_values.put("+", new Integer[]{1, 3, 1, 3, 1});
        morse_values.put("=", new Integer[]{3, 1, 1, 1, 3});
        morse_values.put("-", new Integer[]{3, 1, 1, 1, 1, 3});
        morse_values.put("_", new Integer[]{1, 1, 3, 3, 1, 3});
        morse_values.put("+", new Integer[]{1, 3, 1, 3, 1});
        morse_values.put(";", new Integer[]{3, 1, 3, 1, 3, 1});
        morse_values.put(":", new Integer[]{3, 3, 3, 1, 1, 1});
        morse_values.put("(", new Integer[]{3, 1, 3, 3, 1});
        morse_values.put(")", new Integer[]{3, 1, 3, 3, 1, 3});
    }
    //MainActivity code
    private static final int DOTS_IN_INNER_LETTER_GAP        = 1;
    private static final int DOTS_IN_LETTER_GAP              = 3;
    private static final int DOTS_IN_WORD_GAP                = 5;
    private static final int DURATION_PER_NODE               = 100;


    //Character sets
    private final String CHARSET_MORSE  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,?'!/()&:;=+-_\"$@0123456789";




    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeSMSReceiver();
        registerSMSReceiver();

        return inflater.inflate(R.layout.fragment_morse, container, false);

    }

    private void initializeSMSReceiver(){
        smsReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

//                if (intent.getAction().equals(SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[])bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    if (messages.length > -1) {
                        String theMessage = "";
                        for (int k = 0; k < messages.length; k++){
                            theMessage += messages[k].getMessageBody();
                        }
                        Log.v("Received?", theMessage);
                        String message = sanitzeMessage(theMessage);
                        ((MainActivity)getActivity()).sendTextToBean(message);
                    }
                }

            }
        };
    }


    private String sanitzeMessage(String message){
        message = message.toUpperCase();
        message = message.replaceAll("[^ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,?'!/()&:;=+-_\"$@ \n]", "");
        Log.v(TAG, message);
        message = message + "~";
        return message;
    }

    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(smsReceiver, intentFilter);
    }

    private long[] generateBuzzes(String message){
        ArrayList<Integer> buzzes = new ArrayList<Integer>();
        buzzes.add(250);


        for (int i = 0; i < message.length(); i++){
            if (morse_values.containsKey(Character.toString(message.charAt(i)))){
                for (int k = 0; k < morse_values.get(Character.toString(message.charAt(i))).length; k++){
                    buzzes.add(morse_values.get(Character.toString(message.charAt(i)))[k] * DURATION_PER_NODE);
                    if (k != morse_values.get(Character.toString(message.charAt(i))).length-1){
                        buzzes.add(DOTS_IN_INNER_LETTER_GAP * DURATION_PER_NODE);
                    }
                }
                buzzes.add(DOTS_IN_LETTER_GAP * DURATION_PER_NODE);
            } else {
                buzzes.add(0);
                buzzes.add(DOTS_IN_WORD_GAP * DURATION_PER_NODE);
            }


        }
        long[] buzzArray = new long[buzzes.size()];
        for (int j = 0; j < buzzes.size(); j++){
            buzzArray[j] = (long) buzzes.get(j);
        }
        return buzzArray;
    }

    static String[] concat(String[]... arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int pos = 0;
        for (String[] array : arrays) {
            for (String element : array) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }



}
