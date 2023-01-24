package com.example.sleepkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class SleepData extends AppCompatActivity {
    TextView sleep_date, last_time, wake_time, new_time, total_dur, sleep_time, mood_qual, sleep_qual;
    CircularProgressBar sleepqProgBar;
    ImageView btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_data);
        sleep_date = findViewById(R.id.sleepDate);
        sleep_qual = findViewById(R.id.sleepqual);
        btn_back = findViewById(R.id.back_button);
        last_time = findViewById(R.id.lastTimeRecord);
        wake_time = findViewById(R.id.wakeTimeRecord);
        new_time = findViewById(R.id.newTimeRecord);
        sleep_time = findViewById(R.id.sleepTimeRecord);
        total_dur = findViewById(R.id.totalDurRecord);
        mood_qual = findViewById(R.id.moodQualRecord);
        sleepqProgBar = findViewById(R.id.sleepqProgressBar);


        Intent intent = getIntent();
        String date = intent.getExtras().getString("dateRecorded");
        String lastTime = intent.getExtras().getString("lastRecorded");
        String wakeTimeRec = intent.getExtras().getString("wakeTime");
        String newTimeRec = intent.getExtras().getString("newRecorded");
        String totalDuration = intent.getExtras().getString("totalDur");
        String sleepTimeRec = intent.getExtras().getString("sleepTime");
        String moodQualRec = intent.getExtras().getString("moodQual");
        String sleepQualRec = intent.getExtras().getString("sleepQual");
        sleep_date.setText(date);
        last_time.setText(lastTime);
        wake_time.setText(wakeTimeRec);
        new_time.setText(newTimeRec);
        total_dur.setText(totalDuration);
        sleep_time.setText(sleepTimeRec);
        mood_qual.setText(moodQualRec);
        sleep_qual.setText(sleepQualRec + "%");
        sleepqProgBar.setProgress(Float.parseFloat(sleepQualRec));

        btn_back.setOnClickListener(v -> startActivity(new Intent(SleepData.this, WeeklyReport.class)));

    }

}
