package com.example.sleepkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText email_txt, pass_txt;
    private Button button_login, signup_txt;
    private TextView button_frgtpass;
    private ImageView button_back;
    private ProgressBar progressBar;
    DatabaseReference userRef;
    private int counter = 3;
    FirebaseAuth auth;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        email_txt = findViewById(R.id.email_input);
        pass_txt = findViewById(R.id.pass_input);
        button_login = findViewById(R.id.login_button);
        button_frgtpass = findViewById(R.id.frgtpass_button);
        button_back = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.loadingBar);
        signup_txt = findViewById(R.id.signup_button);
        progressBar.setVisibility(View.GONE);
        userRef = FirebaseDatabase.getInstance().getReference("UserData");


        button_login.setOnClickListener(v -> {

            password = pass_txt.getText().toString().trim();

            email = email_txt.getText().toString().trim();

            if (email.isEmpty()) {
                email_txt.setError("Email address is required.");
                email_txt.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                pass_txt.setError("Password is required.");
                pass_txt.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login successful.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, Clock.class));
                    finish();

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Incorrect email and/or password. Please try again.", Toast.LENGTH_LONG).show();
                    counter --;
                    Toast.makeText(this, "Login Attempt " + String.valueOf(counter), Toast.LENGTH_SHORT).show();
                    if (counter==0){
                        finishAffinity();
                    }
                }
            });
        });

        button_frgtpass.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgotPass.class)));

        button_back.setOnClickListener(v -> startActivity(new Intent(Login.this, LoginActivity.class)));
        signup_txt.setOnClickListener(v -> startActivity(new Intent(Login.this, Signup.class)));
    }
}