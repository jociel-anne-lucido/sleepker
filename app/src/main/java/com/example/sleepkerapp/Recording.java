package com.example.sleepkerapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class Recording extends AppCompatActivity {

    private Button stop_tracker, alarm_buttons;
    private Chronometer timer;
    private long currentTime, timeInterval;
    private TextClock clock;
    Switch aSwitch;

    String wakeTime, totalDur, sleep, sleepQual, hours, minute;
    int minutes = 60;
    int hours_to_minutes = 0;
    int sleepQual_percent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        stop_tracker = findViewById(R.id.stop_button);
        timer = findViewById(R.id.chronometer_txt);
        clock = findViewById(R.id.textClock);
        aSwitch=findViewById(R.id.switch_btn);
        Intent intentS = new Intent(this, MyService.class);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (aSwitch.isChecked()){
                    startService(intentS);
                }else{
                    stopService(intentS);
                }
            }
        });

        // for notif
        Intent intent1 = new Intent(this, ForegroundService.class);

        startForegroundService(intent1);

        stop_tracker.setOnClickListener(v -> {
            timer.stop();
            stopService(intent1);
            stopService(intentS);

            sleep = totalDur.replace(":",".");
            String[] splits = sleep.split("\\.");
            String whole = splits[0];
            String fractional = splits[1];
            hours_to_minutes += (Integer.parseInt(whole) * minutes);
            sleepQual_percent += (((Integer.parseInt(fractional) + hours_to_minutes) * 100) / 600);
            sleepQual = String.valueOf(sleepQual_percent);

            // from tracker class
            Intent intent = getIntent();
            String text = intent.getStringExtra("sleepTime");

            wakeTime = clock.getText().toString();

            Intent newIntent = new Intent(Recording.this, Mood.class);
            newIntent.putExtra("wakeTime", wakeTime);
            newIntent.putExtra("sleepTime", text);
            newIntent.putExtra("totalDur", totalDur);
            newIntent.putExtra("sleepQual", sleepQual);

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