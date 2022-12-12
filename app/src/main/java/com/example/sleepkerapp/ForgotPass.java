package com.example.sleepkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    private EditText email_txt;
    private Button button_send, forgot_txt;
    private ImageView button_back;
    private ProgressBar progressBar;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        email_txt = findViewById(R.id.email_input);
        button_send = findViewById(R.id.send_button);
        button_back = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.progressBar3);
        forgot_txt = findViewById(R.id.forgot_button);
        progressBar.setVisibility(View.GONE);

        button_send.setOnClickListener(v -> {

            email = email_txt.getText().toString().trim();

            if (email.isEmpty()) {
                email_txt.setError("Email address is required.");
                email_txt.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            FirebaseAuth.getInstance().sendPasswordResetEmail(email_txt.getText().toString())
                    .addOnCompleteListener(task -> {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    ForgotPass.this, "Please check your email and click on the provided link to reset your password.",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPass.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(ForgotPass.this, "No email access. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        button_back.setOnClickListener(v -> startActivity(new Intent(ForgotPass.this, Login.class)));
        forgot_txt.setOnClickListener(v -> startActivity(new Intent(ForgotPass.this, Signup.class)));
    }
}