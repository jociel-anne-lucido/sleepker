package com.example.sleepkerapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Provider;

public class MyService extends Service {

    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started successfully", Toast.LENGTH_SHORT).show();
        mediaPlayer = MediaPlayer.create(this,R.raw.relaxing);
        mediaPlayer.start();
        mediaPlayer.setLooping(false);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this, "Service started playing song...", Toast.LENGTH_SHORT).show();
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped playing", Toast.LENGTH_SHORT).show();
        mediaPlayer.stop();
    }
}