package com.example.sleepkerapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent intent1 = new Intent(this, Recording.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleepker")
                .setContentText("Sleep tracking in progress...")
                .setSmallIcon(R.drawable.sleep_symbol)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        stopForeground(true);
        manager.cancel(1);
        stopSelf();

        super.onDestroy();
    }
}

