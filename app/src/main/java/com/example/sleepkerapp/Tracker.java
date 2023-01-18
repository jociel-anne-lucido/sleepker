package com.example.sleepkerapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tracker extends AppCompatActivity {
    TextView time1, time2, time3,time4,time5;
    Button button, play_button, play_button1;
    String sleepTime;
    int button_status=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        time1 = findViewById(R.id.time1Text);
        time2 = findViewById(R.id.time2Text);
        time3 = findViewById(R.id.time3Text);
        time4 = findViewById(R.id.time4Text);
        time5 = findViewById(R.id.time5Text);

        DisplayWakeTime();

        BottomNavigationView bottomNavigationView= (BottomNavigationView)findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.tracker);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    Intent intent1= new Intent(Tracker.this,Clock.class);
                    startActivity(intent1);
                    break;
                case R.id.tracker:
                    break;
                case R.id.analysis:
                    Intent intent3= new Intent(Tracker.this,Analysis.class);
                    startActivity(intent3);
                    break;
            } return false;
        });

        play_button1 = findViewById(R.id.play_button1);
        play_button = findViewById(R.id.play_button);
        Intent intent1 = new Intent(this, MyService.class);
        Intent intent = new Intent(this, MyService1.class);

        play_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startorStop();
            }
        });


        button = findViewById(R.id.start_button);
        button.setOnClickListener(v -> {
            openRecording();

        });
    }

    public void DisplayWakeTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = simpleDateFormat.format(calendar.getTime());

        try {
            Date date = simpleDateFormat.parse(currentTime);
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE,25);
            String result1 = simpleDateFormat.format(calendar.getTime());
            time1.setText(result1);

            calendar.add(Calendar.MINUTE,35);
            String result2 = simpleDateFormat.format(calendar.getTime());
            time2.setText(result2);

            calendar.add(Calendar.MINUTE,30);
            String result3 = simpleDateFormat.format(calendar.getTime());
            time3.setText(result3);

            calendar.add(Calendar.MINUTE,30);
            String result4 = simpleDateFormat.format(calendar.getTime());
            time4.setText(result4);

            calendar.add(Calendar.MINUTE,30);
            String result5 = simpleDateFormat.format(calendar.getTime());
            time5.setText(result5);
        }
        catch (Exception e) { }
    }

    public void openRecording (){
        Date today = Calendar.getInstance().getTime();

        SimpleDateFormat dayFormat = new SimpleDateFormat("hh:mm a");
        String time = dayFormat.format(today);

        sleepTime = time;

        Intent intent = new Intent (Tracker.this, Recording.class);
        intent.putExtra("sleepTime", sleepTime);
        startActivity(intent);

    }
    boolean isStop = false;

    public void startorStop() {
        Intent intents = new Intent (Tracker.this, MyService.class);
        if(isStop) {
            startService(intents);
        } else {
            stopService(intents);
        }
        isStop = !isStop;
    }

}