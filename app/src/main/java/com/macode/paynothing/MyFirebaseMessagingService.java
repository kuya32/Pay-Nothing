package com.macode.paynothing;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.ChatActivity;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String notificationTitle, notificationBody, clickAction, sellersId, itemKey;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Log.d("Notification", remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
            clickAction = remoteMessage.getNotification().getClickAction();
            sellersId = remoteMessage.getData().get("otherUserId");
            itemKey = remoteMessage.getData().get("itemKey");
            System.out.println("VERY CLOSE TO LOSING EVERYTHING!");
        }

        showNotification(notificationTitle, notificationBody, clickAction, sellersId, itemKey);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("NewToken", s);
    }

    private void showNotification(String notificationTitle, String notificationBody, String clickAction, String sellersId, String itemKey) {
        NotificationChannel channel = new NotificationChannel("message", "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Message Notification");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(clickAction);
        intent.putExtra("sellersId", sellersId);
        intent.putExtra("itemKey", itemKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, "message")
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.pay_nothing_logo) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSound(defaultSoundUri);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
