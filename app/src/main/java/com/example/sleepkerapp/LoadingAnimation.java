package com.example.sleepkerapp;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import android.widget.ProgressBar;

public class LoadingAnimation extends Animation {

    private Context context;
    private ProgressBar loadingBar;
    private float from, to;

    public LoadingAnimation(Context context, ProgressBar loadingBar, float from, float to) {
        this.context = context;
        this.loadingBar = loadingBar;
        this.from = from;
        this.to = to;
    }
    
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to-from) * interpolatedTime;
        loadingBar.setProgress((int) value);

        if (value == to) {
            context.startActivity(new Intent(context, Clock.class));
        }
    }
}
