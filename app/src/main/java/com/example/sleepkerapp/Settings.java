package com.example.sleepkerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageView button_back = findViewById(R.id.back_button);
        LinearLayout profilecon = findViewById(R.id.profilecon);
        RelativeLayout termcon = findViewById(R.id.termscon);
        LinearLayout aboutcon = findViewById(R.id.aboutcon);
        RelativeLayout manualcon = findViewById(R.id.manualscon);

        button_back.setOnClickListener(v -> startActivity(new Intent(Settings.this, Clock.class)));
        profilecon.setOnClickListener(v -> startActivity(new Intent(Settings.this, UserSettings.class)));
        termcon.setOnClickListener(v -> startActivity(new Intent(Settings.this, Terms.class)));
        aboutcon.setOnClickListener(v -> startActivity(new Intent(Settings.this, AboutUs.class)));
        manualcon.setOnClickListener(v -> startActivity(new Intent(Settings.this, Manual.class)));


    }

}