package com.giveblood.bloodhub.messagingfeature;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.giveblood.bloodhub.R;
import com.google.android.gms.gcm.GcmListenerService;

public  class MessageReceiver extends GcmListenerService{

} /*extends GcmListenerService{

    static final String TAG = MessageReceiver.class.getSimpleName();
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.e(TAG,"onMessageReceived called");
        String daata = data.getString("data");
        if(daata==null){
            // call is received
        }else{
            createMessageNotification(daata);
        }

    }

    private void createMessageNotification(String daata) {
        String[] parts = daata.split(",,,@@@Uc@Y@U...,,,");
        Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        serviceIntent.putExtra("ID",parts[2]);
        serviceIntent.putExtra("receiverGCMID",parts[6]);
        startService(serviceIntent);
        String text = parts[0];
        String SenderID = parts[1];
        String ReceiverID = parts[2];
        String SenderName= parts[3];
        String ReceiverName = parts[4];
        String SenderGCMID = parts[5];
        String ReceiverGCMID = parts[6];
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(MessageReceiver.this, MessagingActivity.class);
        notificationIntent.putExtra("RECEIVER_NAME", SenderName);
        notificationIntent.putExtra("Sender_NAME", ReceiverName);
        notificationIntent.putExtra("receiverGCMID",SenderGCMID);
        notificationIntent.putExtra("SenderGCMID",ReceiverGCMID);
        notificationIntent.putExtra("ReceiverID", SenderID);
        notificationIntent.putExtra("SenderID", ReceiverID);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(MessageReceiver.this, 0, notificationIntent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(SenderName)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.iconn)
                .setContentIntent(pi);
        notificationManager.notify(1, mBuilder.build());
    }


}*/





