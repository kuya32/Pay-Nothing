package com.macode.paynothing.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.ChatActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private String sent, user, icon, title, body;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sent = remoteMessage.getData().get("sent");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null && sent.equals(firebaseUser.getUid())) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        user = remoteMessage.getData().get("user");
        icon = remoteMessage.getData().get("icon");
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        notificationManager.notify(i, builder.build());
    }
}
