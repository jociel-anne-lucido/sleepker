package com.example.sleepkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class Mood extends AppCompatActivity {

    private Button submit_btn;
    private ImageView moodView;
    private RadioGroup radioButton;

    private Integer[] mood = {R.drawable.good, R.drawable.relaxing, R.drawable.soso, R.drawable.bad, R.drawable.worst};

    private String answer = "", wake, sleep, totaldur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        submit_btn = findViewById(R.id.submit_button);
        radioButton = findViewById(R.id.radioGroup);
        moodView = findViewById(R.id.moodView);

        // checks user input and displays mood pic
        radioButton.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            answer = rb.getText().toString();
            int index = group.indexOfChild(rb);
            moodView.setImageResource(mood[index]);
        });

        submit_btn.setOnClickListener(v -> {
            Intent intent = getIntent();
            wake = intent.getStringExtra("wakeTime");
            sleep = intent.getStringExtra("sleepTime");
            totaldur = intent.getStringExtra("totalDur");

            Intent newIntent = new Intent(Mood.this, Analysis.class);
            newIntent.putExtra("wakeTime", wake);
            newIntent.putExtra("sleepTime", sleep);
            newIntent.putExtra("totalDur", totaldur);
            newIntent.putExtra("moodQual", answer);
            startActivity(newIntent);
        });
    }

}


