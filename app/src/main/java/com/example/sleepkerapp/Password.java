package com.example.sleepkerapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Password extends AppCompatActivity {
    private EditText curpass_txt, newpass_txt, newpass_txt1;
    private ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference ref, userRef;
    String name, email, pass, pass1, uid, currentPassword, oldEmail, oldPass;


    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Button button_update = findViewById(R.id.upload1_button);
        ImageView button_back = findViewById(R.id.back_button);

        curpass_txt = findViewById(R.id.curpass_input);
        newpass_txt = findViewById(R.id.newpass_input);
        newpass_txt1 = findViewById(R.id.newpass_input1);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("UserData");
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        GetPrefData();
        button_update.setOnClickListener(v -> Update_Data());
        button_back.setOnClickListener(v -> startActivity(new Intent(Password.this, UserSettings.class)));


    }
    private void GetPrefData() {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        oldPass = pref.getString("pass", "");


    }
    private void Update_Data() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            currentPassword = curpass_txt.getText().toString();
            pass = newpass_txt.getText().toString();
            pass1 = newpass_txt1.getText().toString();

            if (currentPassword.isEmpty()) {
                curpass_txt.setError("Current password field cannot be empty.");
                curpass_txt.requestFocus();

                return;
            }if (pass.isEmpty() || pass1.isEmpty()) {
                newpass_txt.setError("New password fields cannot be empty.");
                newpass_txt.requestFocus();
                newpass_txt1.setError("New password fields cannot be empty.");
                newpass_txt1.requestFocus();
                return;
            }
            if (!ValidatePass()) {
                return;
            }
            if (!pass.equals(pass1)) {
                newpass_txt1.setError("Passwords do not match.");
                newpass_txt1.requestFocus();
                return;
            }

            DatabaseReference passRef = ref.child(uid).child("Password");

            passRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String oldPass = snapshot.getValue(String.class);

                    if (oldPass != null && oldPass.equals(currentPassword)) {
                        if (currentPassword.equals(pass)) {
                            curpass_txt.setError("No changes made.");
                            curpass_txt.requestFocus();
                            return;
                        } else {
                            UpdatePasswordInFirebase(pass);
                        }
                    } else {
                        curpass_txt.setError("Incorrect current password");
                        curpass_txt.requestFocus();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    Toast.makeText(Password.this, "Failed to retrieve password from the database.", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // No authenticated user, handle the error
            Toast.makeText(Password.this, "No authenticated user.", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean ValidatePass() {
        pass = newpass_txt.getText().toString();

        SharedPreferences newpref = PreferenceManager.getDefaultSharedPreferences(this);
        oldPass = newpref.getString("currentPass", "");

        if (pass.equals(oldPass)) {
            newpass_txt.setError("No changes detected.");
            newpass_txt.requestFocus();

            return false;

        } else if (pass.length() < 6) {
            newpass_txt.setError("Password should be at least 6 characters long.");
            newpass_txt.requestFocus();

            return false;

        } else if (!pass.matches(".*[0-9].*")) {
            newpass_txt.setError("Password should contain at least one digit.");
            newpass_txt.requestFocus();

            return false;

        } else if (!pass.matches(".*[A-Z].*")) {
            newpass_txt.setError("Password should contain at least one uppercase.");
            newpass_txt.requestFocus();

            return false;

        } else if (!pass.matches(".*[a-z].*")) {
            newpass_txt.setError("Password should contain at least one lowercase.");
            newpass_txt.requestFocus();

            return false;

        } else if (!pass.matches(".*[#?!@_.$%^&*-].*")) {
            newpass_txt.setError("Password should contain at least one character #?!@$%^&*-.");
            newpass_txt.requestFocus();

            return false;
        } else {

            return true;

        }
    }
    private void UpdatePasswordInFirebase(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String oldEmail = user.getEmail();
            String oldPassword = curpass_txt.getText().toString().trim();

            AuthCredential authCredential = EmailAuthProvider.getCredential(oldEmail, oldPassword);

            user.reauthenticate(authCredential).addOnCompleteListener(reauthTask -> {
                if (reauthTask.isSuccessful()) {
                    // User reauthenticated successfully

                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            // Password updated successfully in Firebase Authentication

                            // Update the password in the Realtime Database
                            DatabaseReference passwordRef = ref.child(uid).child("Password");
                            passwordRef.setValue(newPassword).addOnCompleteListener(databaseTask -> {
                                if (databaseTask.isSuccessful()) {
                                    // Password updated successfully in the Realtime Database
                                    Toast.makeText(Password.this, "Password updated successfully. Please login again to continue.",
                                            Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.VISIBLE);

                                    new Timer().schedule(
                                            new TimerTask(){
                                                @Override
                                                public void run() {
                                                    auth.signOut();
                                                    startActivity(new Intent(Password.this, LoginActivity.class));
                                                }
                                            }, 8000);

                                } else {
                                    // Error updating password in the Realtime Database
                                    Toast.makeText(Password.this, "Failed to update password in the database.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Error updating password in Firebase Authentication
                            Toast.makeText(Password.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error reauthenticating user
                    Toast.makeText(Password.this, "Failed to reauthenticate user.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No authenticated user
            Toast.makeText(Password.this, "No authenticated user.", Toast.LENGTH_SHORT).show();
        }
    }


}
