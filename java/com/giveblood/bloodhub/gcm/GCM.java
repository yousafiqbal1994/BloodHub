package com.giveblood.bloodhub.gcm;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.util.ArrayList;
import java.util.List;


public class GCM {

    public void sendMessage(String API_KEY,String regID,String messageText)
    {
        Sender sender = new Sender(API_KEY);
        Message message = new Message.Builder()
                .collapseKey("TEST")
                .timeToLive(30)
                .delayWhileIdle(false)
                .addData("data", messageText)
                .build();
        try
        {
            List<String> androidTargets = new ArrayList<>();
            androidTargets.add(regID);
            Result result = sender.send(message, regID,5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}