package com.example.sleepkerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Clock extends AppCompatActivity {
    TextView textView, textSubheading, textViewA;
    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference ref;

    private ImageView button_profile, moon_button, notes_button, music_button, record_button, sleep_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        textView=findViewById(R.id.greetings);
        textViewA=findViewById(R.id.greetings1);
        textSubheading=findViewById(R.id.greetingsub);
        button_profile = findViewById(R.id.profile);
        moon_button = findViewById(R.id.moon_button);
        notes_button = findViewById(R.id.notes_card);
        music_button = findViewById(R.id.music_card);
        record_button = findViewById(R.id.record_card);
        sleep_button = findViewById(R.id.sleep_card);

        button_profile.setOnClickListener(v -> startActivity(new Intent(Clock.this, Settings.class)));
        moon_button.setOnClickListener(v -> startActivity(new Intent(Clock.this, Tracker.class)));
        notes_button.setOnClickListener(v -> startActivity(new Intent(Clock.this, Todo.class)));
        music_button.setOnClickListener(v -> startActivity(new Intent(Clock.this, Music.class)));
        record_button.setOnClickListener(v -> startActivity(new Intent(Clock.this, WeeklyReport.class)));
        sleep_button.setOnClickListener(v -> startActivity(new Intent(Clock.this, NavigationActivity.class)));
        ShowDate();
        ShowGreetings();


        BottomNavigationView bottomNavigationView= (BottomNavigationView)findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.home:
                    break;
                case R.id.tracker:
                    Intent intent2= new Intent(Clock.this,Tracker.class);
                    startActivity(intent2);
                    break;
                case R.id.analysis:
                    Intent intent3= new Intent(Clock.this,Analysis.class);
                    startActivity(intent3);
                    break;
            }
            return false;
        });
    }

    private void ShowDate() {
        Date today = Calendar.getInstance().getTime();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");

        String day = dayFormat.format(today);
        String date = dateFormat.format(today);

        TextView textViewDay = findViewById(R.id.day);
        TextView textViewDate = findViewById(R.id.date);

        textViewDay.setText("Today, "+day);
        textViewDate.setText(date);
    }

    private void ShowGreetings() {
        Calendar calendar = Calendar.getInstance();
        int Hours=calendar.get(Calendar.HOUR_OF_DAY);

        if (Hours > 0 && Hours < 12) {
            textView.setText("Good");
            textViewA.setText("Morning!");
            textSubheading.setText("A great morning to track your sleep.");
        } else if(Hours>=12 && Hours<17) {
            textView.setText("Good");
            textViewA.setText("Afternoon!");
            textSubheading.setText("A great afternoon to track your sleep.");
        } else {
            textView.setText("Good");
            textViewA.setText("Night!");
            textSubheading.setText("A great night to track your sleep.");
        }
    }
}