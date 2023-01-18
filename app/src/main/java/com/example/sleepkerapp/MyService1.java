package com.example.sleepkerapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Provider;

public class MyService1 extends Service {

    MediaPlayer mediaPlayer1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started successfully", Toast.LENGTH_SHORT).show();
        mediaPlayer1 = MediaPlayer.create(this,R.raw.example1);
        mediaPlayer1.start();
        mediaPlayer1.setLooping(false);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this, "Service started playing song...", Toast.LENGTH_SHORT).show();
        mediaPlayer1.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped playing", Toast.LENGTH_SHORT).show();
        mediaPlayer1.stop();
    }
}
