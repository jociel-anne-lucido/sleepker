package com.example.sleepkerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Signup extends AppCompatActivity {

    private EditText name_txt, age_txt, email_txt, pass_txt;
    private Button button_next, button_login;
    private ImageView button_back;
    private ProgressBar progressBar;
    Gender genders;
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    CheckBox checkBox;


    String [] planets = {"Female", "Male", "None"};
    String name, age, gender, email, password, uid, passwords, gend;

    FirebaseAuth auth;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name_txt = findViewById(R.id.name_input);
        age_txt = findViewById(R.id.age_input);
        email_txt = findViewById(R.id.email_input);
        pass_txt = findViewById(R.id.pass_input);
        button_next = findViewById(R.id.next_button);
        button_login = findViewById(R.id.login_button);
        button_back = findViewById(R.id.back);
        textInputLayout = findViewById(R.id.textInputLayout2);
        progressBar = findViewById(R.id.progressBar);
        checkBox = findViewById(R.id.checkboxTerm);
        progressBar.setVisibility(View.GONE);
        autoCompleteTextView = findViewById(R.id.complete);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    View view = LayoutInflater.from(Signup.this).inflate(R.layout.terms_conditions,(ConstraintLayout)findViewById(R.id.layoutDialogContainer));
                    builder.setView(view);
                    final AlertDialog alertDialog = builder.create();
                    view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            checkBox.setChecked(true);
                            Toast.makeText(Signup.this, "Accept", Toast.LENGTH_SHORT).show();
                        }
                    });
                    view.findViewById(R.id.decline_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Toast.makeText(Signup.this, "Decline", Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(false);
                        }
                    });
                    if (alertDialog.getWindow() != null){
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    alertDialog.show();
                }
            }
        });
        auth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        genders = new Gender();
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item, planets);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                gender = adapterView.getItemAtPosition(position).toString();
            }
        });

        button_next.setOnClickListener(v -> {

            CheckBox();
            if (!CheckName() | !CheckAge() | !CheckGender() | !CheckBox() | !CheckEmail() | !CheckPass()) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(Signup.this, "Please wait while you are redirected to the assessment page.", Toast.LENGTH_SHORT).show();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser user = auth.getCurrentUser();
                    uid = user.getUid();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid);
                    genders.setPlanets(gender);
                    dbRef.setValue(genders);
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
        gend = autoCompleteTextView.getText().toString().trim();
        if (gend.isEmpty()) {
            textInputLayout.setError("Select Gender.");
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
            pass_txt.setError("Enter password.", null);
            pass_txt.requestFocus();
            return false;
        } else if (password.length() < 6) {
            pass_txt.setError("Password should be at least 6 characters long.", null);
            pass_txt.requestFocus();
            return false;
        } else if (!password.matches(".*[0-9].*")) {
            pass_txt.setError("Password should contain at least one digit.", null);
            pass_txt.requestFocus();
            return false;
        } else if (!password.matches(".*[A-Z].*")) {
            pass_txt.setError("Password should contain at least one uppercase.", null);
            pass_txt.requestFocus();
            return false;
        } else if (!password.matches(".*[a-z].*")) {
            pass_txt.setError("Password should contain at least one lowercase.", null);
            pass_txt.requestFocus();
            return false;
        } else if (!password.matches(".*[#?!@_.$%^&*-].*")) {
            pass_txt.setError("Password should contain at least one character #?!@$%^&*-.", null);
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
    private boolean CheckBox(){
        if (checkBox.isChecked()) {
            return true;
        } else {
            Toast.makeText(this, "Please check the box to proceed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}