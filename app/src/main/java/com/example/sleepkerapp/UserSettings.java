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
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class UserSettings extends AppCompatActivity {

    private TextView nameviewer;
    private EditText new_name, new_email, new_pass;
    private ProgressBar progressBar;

    String name, email, pass, uid, oldName, oldEmail, oldPass;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        ImageView button_logout = findViewById(R.id.logout_button);
        ImageView button_back = findViewById(R.id.back_button);
        Button button_update = findViewById(R.id.update_button);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        nameviewer = findViewById(R.id.nametxt);
        new_name = findViewById(R.id.upd_name);
        new_email = findViewById(R.id.upd_email);
        new_pass = findViewById(R.id.upd_pass);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("UserData");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        GetPrefData();

        button_logout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(UserSettings.this, "Logout successful.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserSettings.this, LoginActivity.class));
        });

        button_back.setOnClickListener(v -> startActivity(new Intent(UserSettings.this, Clock.class)));

        button_update.setOnClickListener(v -> UpdateData());
    }

    private void GetPrefData() {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        oldName = pref.getString("name", "");
        oldEmail = pref.getString("email", "");
        oldPass = pref.getString("pass", "");

        if (oldName.equals("")) {
            DatabaseReference nameRef = ref.child(uid).child("Fullname");

            nameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    oldName = snapshot.getValue(String.class);
                    nameviewer.setText(oldName);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
        nameviewer.setText(oldName);
    }

    private void UpdateData() {
        if (!ValidateName() && !ValidateEmail() && !ValidatePass()) {
            Toast.makeText(UserSettings.this, "No changes made.",Toast.LENGTH_SHORT).show();
        }

        else if (ValidateEmail() || ValidatePass()) {
            if (ValidateEmail() && ValidatePass()) {
                ValidatePass();
            }

            Toast.makeText(UserSettings.this, "Data has been updated. Please login again to continue.",
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);

            new Timer().schedule(
                    new TimerTask(){
                        @Override
                        public void run() {
                            auth.signOut();
                            startActivity(new Intent(UserSettings.this, LoginActivity.class));
                        }
                    }, 8000);
        }

        else {
            Toast.makeText(UserSettings.this, "Data has been updated.",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean ValidateName() {
        name = new_name.getText().toString();

        if (name.isEmpty()) { return false; }

        else if (name.equals(oldName)) {
            new_name.setError("No changes detected.");
            new_name.requestFocus();
            return false;

        } else {
            nameviewer.setText(name);
            HashMap hashMap = new HashMap();
            hashMap.put("Fullname", name);
            ref.child(uid).updateChildren(hashMap);
            return true;
        }
    }

    private boolean ValidateEmail() {
        email = new_email.getText().toString();
        oldEmail = user.getEmail();

        GetCurrentPass();
        SharedPreferences newpref = PreferenceManager.getDefaultSharedPreferences(this);
        oldPass = newpref.getString("currentPass", "");

        if (email.isEmpty()) {
            return false;
        } else if (email.equals(oldEmail)) {
            new_email.setError("No changes detected.");
            new_email.requestFocus();
            return false;

        } else {
            assert oldEmail != null;
            AuthCredential authCredential = EmailAuthProvider.getCredential(oldEmail, oldPass);

            user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updateEmail(email).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("EmailId", email);
                            ref.child(uid).updateChildren(hashMap);
                        }
                    });
                }
            }); return true;
        }
    }

    private boolean ValidatePass() {
        pass = new_pass.getText().toString();

        GetCurrentPass();
        SharedPreferences newpref = PreferenceManager.getDefaultSharedPreferences(this);
        oldPass = newpref.getString("currentPass", "");

        if (pass.isEmpty()) {
            return false;

        } else if (pass.equals(oldPass)) {
            new_pass.setError("No changes detected.");
            new_pass.requestFocus();
            return false;

        } else if (pass.length() < 6) {
            new_pass.setError("Password should be at least 6 characters long.");
            new_pass.requestFocus();
            return false;

        } else {
            oldEmail = user.getEmail();
            AuthCredential authCredential = EmailAuthProvider.getCredential(oldEmail, oldPass);

            user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(pass).addOnCompleteListener(task1 -> {
                        HashMap hashMap = new HashMap();
                        hashMap.put("Password", pass);
                        ref.child(uid).updateChildren(hashMap);
                    });
                }
            }); return true;
        }
    }

    private void GetCurrentPass() {
        DatabaseReference passRef = ref.child(uid).child("Password");

        passRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oldPass = snapshot.getValue(String.class);

                SharedPreferences newpref = PreferenceManager.getDefaultSharedPreferences(UserSettings.this);
                SharedPreferences.Editor editor = newpref.edit();
                editor.putString("currentPass", oldPass);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}