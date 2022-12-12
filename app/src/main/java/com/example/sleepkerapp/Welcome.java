package com.example.sleepkerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class Welcome extends AppCompatActivity {

    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadingBar = findViewById(R.id.loadingBar);
        loadingBar.setMax(100);
        loadingBar.setScaleY(3f);

        loadingBarAnim();
    }

    public void loadingBarAnim() {
        LoadingAnimation anim = new LoadingAnimation(this, loadingBar, 0f, 100f);
        anim.setDuration(8000);
        loadingBar.setAnimation(anim);
    }
}