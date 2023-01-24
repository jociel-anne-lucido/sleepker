package com.example.sleepkerapp;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Analysis extends AppCompatActivity {

    private TextView wokeUpText, sleepText, totalDuration, newDuration, oldDuration, moodText, dateText, aveText, aveText1;
    ImageView weekly_button;
    Button statistic_button;
    String wake, sleep, totaldur, newdur, olddur, mood, uid, pushid, dateRecorded, sleepqual;
    CircularProgressBar circularProgressBar;
    SleepCycleData scd;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        wokeUpText = findViewById(R.id.wokeupTime);
        sleepText = findViewById(R.id.sleepTime);
        totalDuration = findViewById(R.id.sleepdurTime);
        newDuration = findViewById(R.id.new_duration);
        oldDuration = findViewById(R.id.old_duration);
        moodText = findViewById(R.id.moodText);
        dateText = findViewById(R.id.date_text);
        aveText = findViewById(R.id.average);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        weekly_button = findViewById(R.id.weekly_report);
        statistic_button = findViewById(R.id.statistic_button);
        statistic_button.setOnClickListener(v -> startActivity(new Intent(Analysis.this, Statistic.class)));
        weekly_button.setOnClickListener(v -> startActivity(new Intent(Analysis.this, WeeklyReport.class)));



        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-SleepData");

        ShowDate();
        GetIntentData();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.analysis);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    Intent intent1= new Intent(Analysis.this,Clock.class);
                    startActivity(intent1);
                    break;
                case R.id.tracker:
                    Intent intent2= new Intent(Analysis.this,Tracker.class);
                    startActivity(intent2);
                    break;
                case R.id.analysis:
                    break;
            } return false;
        });

    }

    private void ShowDate() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        dateText.setText(dateFormat.format(today));
    }

    private void GetIntentData() {
        scd = new SleepCycleData();

        // from mood class
        Intent intent = getIntent();
        wake = intent.getStringExtra("wakeTime");
        sleep = intent.getStringExtra("sleepTime");
        totaldur = intent.getStringExtra("totalDur");
        sleepqual = intent.getStringExtra("sleepQual");
        mood = intent.getStringExtra("moodQual");

        // handles null value from intent
        if (wake == null && sleep == null && totaldur == null) {
            wake = "---";
            sleep = "---";
            totaldur = "---";
            sleepqual = "---";
            mood = "---";
            newdur = "---";
            olddur = "---";
        }

        // formats totaldur string for sleep duration data
        if (totaldur != null && !totaldur.equals("---")) {
            String text = totaldur;
            String[] sep = text.split(":");
            newdur = (sep[0] + "hr" + sep[1] + "min");

            GetLastRecorded();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            olddur = pref.getString("lastRecorded", "");

        }

        // stores sleep data attributes to sleepcycledata class
        scd.setLastRecorded(olddur);
        scd.setNewRecorded(newdur);
        scd.setTotalDur(totaldur);
        scd.setMoodQual(mood);
        scd.setSleepTime(sleep);
        scd.setWakeTime(wake);
        scd.setSleepQual(sleepqual);

        DisplayText();
        StoreData();
    }

    private void StoreData() {
        dateRecorded = dateText.getText().toString();
        pushid = userRef.push().getKey();

        SleepCycleData data = new SleepCycleData(olddur, newdur, totaldur, mood, sleep, wake, dateRecorded, sleepqual);

        if (!wake.equals("---")) {
            userRef.child(pushid).setValue(data);
        }
        DisplayText();
    }

    private void DisplayText() {
        GetAllData();
    }

    // retrieves data from fb
    private void GetAllData() {
        Query query = userRef.orderByKey().limitToLast(1);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                sleep = snapshot.child("sleepTime").getValue(String.class);
                wake = snapshot.child("wakeTime").getValue(String.class);
                totaldur = snapshot.child("totalDur").getValue(String.class);
                mood = snapshot.child("moodQual").getValue(String.class);
                newdur = snapshot.child("newRecorded").getValue(String.class);
                olddur = snapshot.child("lastRecorded").getValue(String.class);
                sleepqual = snapshot.child("sleepQual").getValue(String.class);

                sleepText.setText(sleep);
                wokeUpText.setText(wake);
                totalDuration.setText(totaldur);
                moodText.setText(mood);
                newDuration.setText(newdur);
                oldDuration.setText(olddur);
                aveText.setText(sleepqual + "%");
                circularProgressBar.setProgress(Float.parseFloat(sleepqual));
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void GetLastRecorded() {
        Query query = userRef.orderByChild("lastRecorded").limitToLast(1);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                olddur = snapshot.child("newRecorded").getValue(String.class);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Analysis.this);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("lastRecorded", olddur);
                editor.apply();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }



}