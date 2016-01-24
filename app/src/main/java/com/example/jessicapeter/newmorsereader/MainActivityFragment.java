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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> smsAdaptor;

    private BroadcastReceiver smsReceiver;
    private String TAG = "MainActivityFragment: ";
    static Map<String,Integer[]> morse_values = new HashMap<String, Integer[]>();
    List<String> listSms = new ArrayList<String>();

    static {
        morse_values.put("A", new Integer[]{0, 1});
        morse_values.put("B", new Integer[]{1, 0, 0, 0});
        morse_values.put("C", new Integer[]{1, 0, 1, 0});
        morse_values.put("D", new Integer[]{1, 0, 0});
        morse_values.put("E", new Integer[]{0});
        morse_values.put("F", new Integer[]{0, 0, 1, 0});
        morse_values.put("G", new Integer[]{1, 1, 0});
        morse_values.put("H", new Integer[]{0, 0, 0, 0});
        morse_values.put("I", new Integer[]{0, 0});
        morse_values.put("J", new Integer[]{0, 1, 1, 1});
        morse_values.put("K", new Integer[]{1, 0, 1});
        morse_values.put("L", new Integer[]{0, 1, 0, 0});
        morse_values.put("M", new Integer[]{1, 1});
        morse_values.put("N", new Integer[]{1, 0});
        morse_values.put("O", new Integer[]{1, 1, 1});
        morse_values.put("P", new Integer[]{0, 1, 1, 0});
        morse_values.put("Q", new Integer[]{1, 1, 0, 1});
        morse_values.put("R", new Integer[]{0, 1, 0});
        morse_values.put("S", new Integer[]{0, 0, 0});
        morse_values.put("T", new Integer[]{1});
        morse_values.put("U", new Integer[]{0, 0, 1});
        morse_values.put("V", new Integer[]{0, 0, 0, 1});
        morse_values.put("W", new Integer[]{0, 1, 1});
        morse_values.put("X", new Integer[]{1, 0, 0, 1});
        morse_values.put("Y", new Integer[]{1, 0, 1, 1});
        morse_values.put("Z", new Integer[]{1, 0, 1, 1});
        morse_values.put("1", new Integer[]{0});
        morse_values.put("1", new Integer[]{0, 0});
        morse_values.put("1", new Integer[]{0, 0, 0});
        morse_values.put("4", new Integer[]{0, 0, 0, 0});
        morse_values.put("5", new Integer[]{0, 0, 0, 0, 0});
        morse_values.put("6", new Integer[]{0, 0, 0, 0, 0, 0});
        morse_values.put("7", new Integer[]{0, 0, 0, 0, 0, 0, 0});
        morse_values.put("8", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0});
        morse_values.put("9", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        morse_values.put("0", new Integer[]{1});
        morse_values.put(".", new Integer[]{0, 1, 0, 1, 0, 1});
        morse_values.put("?", new Integer[]{0, 0, 1, 1, 0, 0});
        morse_values.put("!", new Integer[]{1, 0, 1, 0, 1, 1});
        morse_values.put(",", new Integer[]{1, 1, 0, 0, 1, 1});
        morse_values.put("&", new Integer[]{0, 1, 0, 0, 0});
        morse_values.put("'", new Integer[]{0, 1, 1, 1, 1, 0});
        morse_values.put("\"", new Integer[]{0, 1, 0, 0, 1, 0});
        morse_values.put("/", new Integer[]{1, 0, 0, 1, 0});
        morse_values.put("$", new Integer[]{0, 0, 0, 1, 0, 0, 1});
        morse_values.put("@", new Integer[]{0, 1, 1, 0, 1, 0});
        morse_values.put("+", new Integer[]{0, 1, 0, 1, 0});
        morse_values.put("=", new Integer[]{1, 0, 0, 0, 1});
        morse_values.put("-", new Integer[]{1, 0, 0, 0, 0, 1});
        morse_values.put("_", new Integer[]{0, 0, 1, 1, 0, 1});
        morse_values.put(";", new Integer[]{1, 0, 1, 0, 1, 0});
        morse_values.put(":", new Integer[]{1, 1, 1, 0, 0, 0});
        morse_values.put("(", new Integer[]{1, 0, 1, 1, 0});
        morse_values.put(")", new Integer[]{1, 0, 1, 1, 0, 1});
    }


    //Character sets
    private final String CHARSET_MORSE  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,?'!/()&:;=+-_\"$@0123456789";


    public MainActivityFragment() {
    }

    //onCreate happens when a fragment is created -- happens before the oncreateview
    @Override
    public void onCreate(Bundle savedInstanceState){
        // Add this line in order for this fragment to handle menu events.{
        super.onCreate(savedInstanceState);
        //call this to report that this fragment has menu options
//        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeSMSReceiver();
        registerSMSReceiver();


        smsAdaptor =
                new ArrayAdapter<String>(
                        //the current context (this activity)
                        getActivity(),
                        //the name of the layout id
                        R.layout.list_item_morse,
                        //the id of the textview to populate
                        R.id.list_item_sms_textview,
                        //the arraylist
                        listSms
                );
        //find the listview and attach the adaptor
        View rootView = inflater.inflate(R.layout.fragment_morse, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_sms);
        listView.setAdapter(smsAdaptor);

        return rootView;

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
                        listSms.add(message);
                        smsAdaptor.clear();
                        smsAdaptor.add(message);
                        String binaryMessage = convertToBinary(message);
                        Log.v(TAG, binaryMessage);
                        ((MainActivity) getActivity()).sendTextToBean(binaryMessage);

                    }
                }
            }
        };
    }

    private String sanitzeMessage(String message){
        message = message.toUpperCase();
        message = message.replaceAll("[^ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,?'!/()&:;=+-_\"$@ \n]", "");
        Log.v(TAG, message);
        return message;
    }

    private String convertToBinary(String message){
        String binaryList = "";
        for (int i = 0; i < message.length(); i++){
            if (morse_values.containsKey(Character.toString(message.charAt(i)))){
                for (int k = 0; k < morse_values.get(Character.toString(message.charAt(i))).length; k++){
                    binaryList += morse_values.get(Character.toString(message.charAt(i)))[k];
                    //below is the gap between dots and dashes in a single character
                    if (k != morse_values.get(Character.toString(message.charAt(i))).length-1){
                        binaryList += 2;
                    }
                }
                //below is gap between letters
                    binaryList +=3;
            } else {
                //below is gap between words
                    binaryList +=4;
            }
        }
        return binaryList;
    }

    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(smsReceiver, intentFilter);
    }



}
