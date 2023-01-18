package com.example.sleepkerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Music extends AppCompatActivity {
    ImageView btn_back, btn_music1, btn_music2, btn_music3, btn_music4, btn_music5, btn_music6, btn_music7, btn_music8, btn_music9;
    MediaPlayer mediaPlayer;
    private boolean isPlay, isPlay2, isPlay3, isPlay4, isPlay5, isPlay6, isPlay7, isPlay8, isPlay9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        btn_music1= findViewById(R.id.music);
        btn_music2= findViewById(R.id.music1);
        btn_music3= findViewById(R.id.music2);
        btn_music4= findViewById(R.id.music3);
        btn_music5= findViewById(R.id.music4);
        btn_music6= findViewById(R.id.music5);
        btn_music7= findViewById(R.id.music6);
        btn_music8= findViewById(R.id.music7);
        btn_music9= findViewById(R.id.music8);
        btn_back = findViewById(R.id.back_button);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Music.this, Clock.class));
                mediaPlayer.stop();
            }
        });

        mediaPlayer = new MediaPlayer();

        btn_music1.setOnClickListener(v -> {
            if (isPlay && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = true;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = false;

        });

        btn_music2.setOnClickListener(v -> {
            if (isPlay2 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = true;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = false;
        });
        btn_music3.setOnClickListener(v -> {
            if (isPlay3 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = true;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = false;
        });
        btn_music4.setOnClickListener(v -> {
            if (isPlay4 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = true;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = false;
        });
        btn_music5.setOnClickListener(v -> {
            if (isPlay5 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = true;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = false;
        });
        btn_music6.setOnClickListener(v -> {
            if (isPlay6 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = true;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = false;
        });
        btn_music7.setOnClickListener(v -> {
            if (isPlay7 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = true;
            isPlay8 = false;
            isPlay9 = false;
        });
        btn_music8.setOnClickListener(v -> {
            if (isPlay8 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = true;
            isPlay9 = false;
        });
        btn_music9.setOnClickListener(v -> {
            if (isPlay9 && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), raw2Uri(R.raw.example1));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlay = false;
            isPlay2 = false;
            isPlay3 = false;
            isPlay4 = false;
            isPlay5 = false;
            isPlay6 = false;
            isPlay7 = false;
            isPlay8 = false;
            isPlay9 = true;
        });


    }
    private Uri raw2Uri(int raw) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + raw);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // or mp.pause();
            mediaPlayer.release();
        }
    }

}