package com.example.sleepkerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private EditText name_txt, age_txt, gender_txt, email_txt, pass_txt;
    private Button button_next, button_login;
    private ImageView button_back;
    private ProgressBar progressBar;

    String name, age, gender, email, password, uid;

    FirebaseAuth auth;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name_txt = findViewById(R.id.name_input);
        age_txt = findViewById(R.id.age_input);
        gender_txt = findViewById(R.id.gender_input);
        email_txt = findViewById(R.id.email_input);
        pass_txt = findViewById(R.id.pass_input);
        button_next = findViewById(R.id.next_button);
        button_login = findViewById(R.id.login_button);
        button_back = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        button_next.setOnClickListener(v -> {
            if (!CheckName() | !CheckAge() | !CheckGender() | !CheckEmail() | !CheckPass()) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(Signup.this, "Please wait while you are redirected to the assessment page.", Toast.LENGTH_SHORT).show();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser user = auth.getCurrentUser();
                    uid = user.getUid();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid);

                    // stores user attributes to db

                    User data = new User(name, age, gender, email, password);

                    dbRef.setValue(data).addOnCompleteListener(task1 -> {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(Signup.this, Assessment.class));
                        finish();
                    });

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Signup.this, "Email is already registered.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        button_back.setOnClickListener(v -> startActivity(new Intent(Signup.this, LoginActivity.class)));
        button_login.setOnClickListener(v -> startActivity(new Intent(Signup.this, Login.class)));
    }

    // checks if user input is valid

    private boolean CheckName() {
        name = name_txt.getText().toString().trim();
        if (name.isEmpty()) {
            name_txt.setError("Enter name.");
            name_txt.requestFocus();
            return false;
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", name);
            editor.apply();
            return true;
        }
    }
    private boolean CheckAge() {
        age = age_txt.getText().toString().trim();
        if (age.isEmpty()) {
            age_txt.setError("Enter age.");
            age_txt.requestFocus();
            return false;
        } else { return true; }
    }

    private boolean CheckGender() {
        gender = gender_txt.getText().toString().trim();
        if (gender.isEmpty()) {
            gender_txt.setError("Enter gender.");
            gender_txt.requestFocus();
            return false;
        } else { return true; }
    }

    private boolean CheckEmail() {
        email = email_txt.getText().toString().trim();
        if (email.isEmpty()) {
            email_txt.setError("Enter email address.");
            email_txt.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Signup.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            editor.putString("email", email);
            editor.apply();
            return true;
        }
    }

    private boolean CheckPass() {
        password = pass_txt.getText().toString().trim();
        if (password.isEmpty()) {
            pass_txt.setError("Enter password.");
            pass_txt.requestFocus();
            return false;
        } else if (password.length() < 6) {
            pass_txt.setError("Password should be at least 6 characters long.");
            pass_txt.requestFocus();
            return false;
        } else {
            editor.putString("pass", password);
            editor.apply();
            return true;
        }
    }

    // checks if user is already logged in then redirects to homepage if true

    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(Signup.this, Clock.class));
        }
    }

}