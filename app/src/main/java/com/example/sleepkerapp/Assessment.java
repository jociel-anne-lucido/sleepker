package com.example.sleepkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Assessment extends AppCompatActivity {

    private GenerateQuestion genQues= new GenerateQuestion();

    private TextView questionView;
    private RadioButton rbChoice1, rbChoice2, rbChoice3, rbChoice4, rbChoice5;
    private Button button_next;
    private ProgressBar progressBar;

    AssessmentData data;

    String answer = "";
    String bedtime, deepsleep, troublesleep, avesleep, qualitysleep;
    private int quesNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        questionView = findViewById(R.id.questionView);
        rbChoice1 = findViewById(R.id.radioButton1);
        rbChoice2 = findViewById(R.id.radioButton2);
        rbChoice3 = findViewById(R.id.radioButton3);
        rbChoice4 = findViewById(R.id.radioButton4);
        rbChoice5 = findViewById(R.id.radioButton5);
        button_next = findViewById(R.id.button_next);

        progressBar = findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.GONE);

        button_next.setOnClickListener(v -> {

            if (CheckAnswer()) {
                return;
            }

            StoreAnswer();
            progressBar.setVisibility(View.VISIBLE);

            if (quesNum == genQues.questions.length) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-AssessmentData");

                AssessmentData data = new AssessmentData(bedtime, deepsleep, troublesleep, avesleep, qualitysleep);

                userRef.setValue(data).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Assessment.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(Assessment.this, Welcome.class));
                });

            } else {
                UpdateQuestion();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean CheckAnswer() {
        if (rbChoice1.isChecked()) {
            answer = rbChoice1.getText().toString().trim();
            return false;
        }
        if (rbChoice2.isChecked()) {
            answer = rbChoice2.getText().toString().trim();
            return false;
        }
        if (rbChoice3.isChecked()) {
            answer = rbChoice3.getText().toString().trim();
            return false;
        }
        if (rbChoice4.isChecked()) {
            answer = rbChoice4.getText().toString().trim();
            return false;
        }
        if (rbChoice5.isChecked()) {
            answer = rbChoice5.getText().toString().trim();
            return false;
        } else {
            Toast.makeText(Assessment.this, "Select your answer.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void StoreAnswer() {
        data = new AssessmentData();
        if (quesNum == 1) {
            bedtime = answer;
            data.setBedTime(bedtime);
        }
        else if (quesNum == 2) {
            deepsleep= answer;
            data.setDeepSleep(deepsleep);
        }
        else if (quesNum == 3) {
            troublesleep = answer;
            data.setTroubleSleep(troublesleep);
        }
        else if (quesNum == 4) {
            avesleep = answer;
            data.setTotalSleep(avesleep);
        }
        else if (quesNum == 5) {
            qualitysleep = answer;
            data.setQualitySleep(qualitysleep);
        }
    }

    private void UpdateQuestion() {
        questionView.setText(genQues.getQuestion(quesNum));
        rbChoice1.setText(genQues.getChoice1(quesNum));
        rbChoice2.setText(genQues.getChoice2(quesNum));
        rbChoice3.setText(genQues.getChoice3(quesNum));
        rbChoice4.setText(genQues.getChoice4(quesNum));
        rbChoice5.setText(genQues.getChoice5(quesNum));

        quesNum++;

        if (quesNum == 5) {
            button_next.setText("Submit");
        }
    }

}