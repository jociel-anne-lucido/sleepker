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

public class UserSettings extends AppCompatActivity {

    private TextView nameviewer;
    private EditText new_name, new_email, new_pass;
    private ProgressBar progressBar;
    ImageView profileImage;

    String name, email, pass, uid, oldName, oldEmail, oldPass;

    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference ref, userRef;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        ImageView button_logout = findViewById(R.id.logout_button);
        ImageView button_back = findViewById(R.id.back_button);
        Button button_update = findViewById(R.id.update_button);
        ImageView button_profile = findViewById(R.id.updateprofile_button);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        nameviewer = findViewById(R.id.nametxt);
        profileImage = findViewById(R.id.imageView);
        new_name = findViewById(R.id.upd_name);
        new_email = findViewById(R.id.upd_email);
        new_pass = findViewById(R.id.upd_pass);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("UserData");

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        GetPrefData();

        button_logout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(UserSettings.this, "Logout successful.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserSettings.this, LoginActivity.class));
        });

        button_back.setOnClickListener(v -> startActivity(new Intent(UserSettings.this, Settings.class)));

        button_update.setOnClickListener(v -> UpdateData());
        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
        new_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Start the next activity here
                    Intent intent = new Intent(UserSettings.this, Password.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserSettings.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetPrefData() {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        oldName = pref.getString("name", "");
        oldEmail = pref.getString("email", "");
        oldPass = pref.getString("pass", "");

        DatabaseReference nameRef = ref.child(uid).child("Fullname");

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String oldNames = snapshot.getValue(String.class);
                nameviewer.setText(oldNames);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private void UpdateData() {
        if (!ValidateName() && !ValidateEmail()) {
            Toast.makeText(UserSettings.this, "No changes made.", Toast.LENGTH_SHORT).show();
        } else if (ValidateEmail()) {
            Toast.makeText(UserSettings.this, "Data has been updated. Please login again to continue.",
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    auth.signOut();
                    startActivity(new Intent(UserSettings.this, LoginActivity.class));
                }
            }, 8000);
        } else {
            Toast.makeText(UserSettings.this, "Data has been updated.", Toast.LENGTH_SHORT).show();
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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            new_email.setError("Please enter a valid email address.");
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
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("EmailId", email);
                            ref.child(uid).updateChildren(hashMap)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(UserSettings.this, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UserSettings.this, "Failed to update email in the database.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(UserSettings.this, "Failed to update email.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(UserSettings.this, "Failed to reauthenticate.", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
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

        } else if (!pass.matches(".*[0-9].*")) {
            new_pass.setError("Password should contain at least one digit.");
            new_pass.requestFocus();
            return false;

        } else if (!pass.matches(".*[A-Z].*")) {
            new_pass.setError("Password should contain at least one uppercase.");
            new_pass.requestFocus();
            return false;

        } else if (!pass.matches(".*[a-z].*")) {
            new_pass.setError("Password should contain at least one lowercase.");
            new_pass.requestFocus();
            return false;

        } else if (!pass.matches(".*[#?!@_.$%^&*-].*")) {
            new_pass.setError("Password should contain at least one character #?!@$%^&*-.");
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