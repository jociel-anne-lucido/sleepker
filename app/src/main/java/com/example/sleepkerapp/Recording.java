package com.example.sleepkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextClock;

import androidx.appcompat.app.AppCompatActivity;

public class Recording extends AppCompatActivity {

    private Button stop_tracker;
    private Chronometer timer;
    private long currentTime, timeInterval;
    private TextClock clock;

    String wakeTime, totalDur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        stop_tracker = findViewById(R.id.stop_button);
        timer = findViewById(R.id.chronometer_txt);
        clock = findViewById(R.id.textClock);

        // for notif
        Intent intent1 = new Intent(this, ForegroundService.class);

        startForegroundService(intent1);

        stop_tracker.setOnClickListener(v -> {
            timer.stop();
            stopService(intent1);

            // from tracker class
            Intent intent = getIntent();
            String text = intent.getStringExtra("sleepTime");

            wakeTime = clock.getText().toString();

            Intent newIntent = new Intent(Recording.this, Mood.class);
            newIntent.putExtra("wakeTime", wakeTime);
            newIntent.putExtra("sleepTime", text);
            newIntent.putExtra("totalDur", totalDur);

            startActivity(newIntent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentTime = SystemClock.elapsedRealtime();
        timeInterval = SystemClock.elapsedRealtime() - currentTime;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentTime != 0) {
            timer.setBase(timer.getBase() + timeInterval);
        } else {
            timer.setBase(SystemClock.elapsedRealtime());
        }

        timer.setOnChronometerTickListener(chronometer -> {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            int hr = (int) (time / 3600000);
            int min = (int) (time - hr * 3600000) / 60000;
            int sec = (int) (time - hr * 3600000 - min * 60000) / 1000;

            String hh = hr < 10 ? "0" + hr : hr + "";
            String mm = min < 10 ? "0" + min : min + "";
            String ss = sec < 10 ? "0" + sec : sec + "";
            totalDur = (hh + ":" + mm);

            // for checking only, will delete after
            chronometer.setText(hh + ":" + mm + ":" + ss);

        });
        timer.start();
    }

}