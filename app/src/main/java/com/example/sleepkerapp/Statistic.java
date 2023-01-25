package com.example.sleepkerapp;

import static java.lang.String.valueOf;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Statistic extends AppCompatActivity {
    private TextView aveTotalDur, aveSleepQual, lowestSleepQual, highestSleepQual, highestSleepDur, lowestSleepDur, lowestDurDate, highestDurDate;
    String totaldur, uid;
    GraphView qual_graph, dur_graph;
    LineGraphSeries qual_line, dur_line;
    CircularProgressBar lowestProgBar, highestProgBar, averageProgBar;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        aveTotalDur = findViewById(R.id.ave_total_duration);
        aveSleepQual = findViewById(R.id.ave_sleep_quality);
        lowestSleepQual = findViewById(R.id.lowest_sleepqual);
        highestSleepQual = findViewById(R.id.highest_sleepqual);
        lowestSleepDur = findViewById(R.id.lowest_sleepdur);
        highestSleepDur = findViewById(R.id.highest_sleepdur);
        averageProgBar = findViewById(R.id.aveProgressBar);
        highestProgBar = findViewById(R.id.highestProgressBar);
        lowestProgBar = findViewById(R.id.lowestProgressBar);
        lowestDurDate = findViewById(R.id.lowest_durDate);
        highestDurDate = findViewById(R.id.highest_durDate);

        qual_graph = findViewById(R.id.sleepqual_graph);
        qual_line = new LineGraphSeries();
        qual_graph.addSeries(qual_line);
        qual_line.setColor(Color.rgb(54, 75, 91));
        qual_line.setDrawBackground(true);
        qual_line.setBackgroundColor(Color.argb(60, 98, 157,158));
        qual_line.setDrawDataPoints(true);
        qual_line.setTitle("Quality");
        qual_graph.getLegendRenderer().setVisible(true);
        qual_graph.getLegendRenderer().setTextColor(Color.rgb(85,115,125));
        qual_graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);
        qual_graph.getLegendRenderer().setBackgroundColor(Color.argb(40,181,189,192));

        qual_graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + "%";
                }
            }
        });

        dur_graph = findViewById(R.id.sleepdur_graph);
        dur_line = new LineGraphSeries();
        dur_graph.addSeries(dur_line);
        dur_line.setColor(Color.rgb(54, 75, 91));
        dur_line.setDrawBackground(true);
        dur_line.setBackgroundColor(Color.argb(60, 98, 157,158));
        dur_line.setDrawDataPoints(true);
        dur_line.setTitle("Duration");
        dur_graph.getLegendRenderer().setVisible(true);
        dur_graph.getLegendRenderer().setTextColor(Color.rgb(85,115,125));
        dur_graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        dur_graph.getLegendRenderer().setBackgroundColor(Color.argb(50,181,189,192));

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-SleepData");


        // average sleep qual
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0.0, average;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object price = map.get("sleepQual");
                    int p = Integer.parseInt(String.valueOf(price));
                    long count = snapshot.getChildrenCount();
                    total += p;
                    average = (total / count);
                    DecimalFormat df = new DecimalFormat("#.#");
                    aveSleepQual.setText(df.format(average)+ "%");
                    averageProgBar.setProgress(Float.parseFloat((String.valueOf(average))));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        GetTotalDur();
        DisplayGraphSQ();
        DisplayGraphDuration();

        // highest dur
        dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-SleepData");
        Query highestSleepdur = dbRef.orderByChild("sleepQual").limitToLast(1);
        highestSleepdur.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    String Key = scd.getTotalDur();
                    String date = scd.getWakeTime();
                    highestSleepDur.setText(Key);
                    highestDurDate.setText(date);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });

        // highest sleep qual
        Query highestSleepqual = dbRef.orderByChild("sleepQual").limitToLast(1);
        highestSleepqual.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    String Key = scd.getSleepQual();
                    highestSleepQual.setText(Key + "%");
                    highestProgBar.setProgress(Float.parseFloat(Key));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });

        // lowest sleep qual
        Query lowestSleepqual = dbRef.orderByChild("sleepQual").limitToFirst(1);
        lowestSleepqual.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    String Key = scd.getSleepQual();
                    lowestSleepQual.setText(Key + "%");
                    lowestProgBar.setProgress(Float.parseFloat(Key));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });

        // lowest sleep dur
        Query lowestSleepdur = dbRef.orderByChild("sleepQual").limitToFirst(1);
        lowestSleepdur.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    String Key = scd.getTotalDur();
                    String date = scd.getWakeTime();
                    lowestSleepDur.setText(Key);
                    lowestDurDate.setText(date);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.analysis);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    Intent intent1= new Intent(Statistic.this,Clock.class);
                    startActivity(intent1);
                    break;
                case R.id.tracker:
                    Intent intent2= new Intent(Statistic.this,Tracker.class);
                    startActivity(intent2);
                    break;
                case R.id.analysis:
                    Intent intent3= new Intent(Statistic.this,Analysis.class);
                    startActivity(intent3);
                    break;
            } return false;
        });

    }
    private void GetTotalDur(){

        Query query = dbRef.orderByKey().limitToLast(1);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                totaldur = snapshot.child("totalDur").getValue(String.class);
                aveTotalDur.setText(totaldur);

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void DisplayGraphSQ() {
        GridLabelRenderer gridLabel = qual_graph.getGridLabelRenderer();
        gridLabel.setVerticalAxisTitle("Percentage %");
        gridLabel.setHorizontalAxisTitle("Input");
        gridLabel.setHorizontalAxisTitleColor(Color.rgb(54,75,91));
        gridLabel.setVerticalAxisTitleColor(Color.rgb(54,75,91));
        gridLabel.setLabelVerticalWidth(110);
        gridLabel.setLabelHorizontalHeight(50);
        gridLabel.setHumanRounding(false);
        gridLabel.setVerticalLabelsColor(Color.rgb(85,115,125));
        gridLabel.setHorizontalLabelsColor(Color.rgb(85,115,125));
        gridLabel.setLabelsSpace(10);

        qual_graph.getViewport().setXAxisBoundsManual(true);
        qual_graph.getViewport().setMinX(1);
        qual_graph.getViewport().setMaxX(2);

        qual_graph.getViewport().setYAxisBoundsManual(true);
        qual_graph.getViewport().setMinY(0);
        qual_graph.getViewport().setMaxY(2);

        qual_graph.getViewport().setScalable(true);
        qual_graph.getViewport().setScrollable(true);
        qual_graph.getViewport().setScrollableY(true);
        qual_graph.getViewport().setScalableY(true);


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataPoint[] data = new DataPoint[(int) snapshot.getChildrenCount()];
                int index = 0, x = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object price = map.get("sleepQual");
                    int p = Integer.parseInt(String.valueOf(price));

                    x += 1;
                    data[index] = new DataPoint(x, p);
                    index++;
                }
                qual_line.resetData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }
    private void DisplayGraphDuration() {
        GridLabelRenderer gridLabel1 = dur_graph.getGridLabelRenderer();
        gridLabel1.setPadding(30);
        gridLabel1.setVerticalAxisTitle("Time (h.m)");
        gridLabel1.setHorizontalAxisTitle("Input");
        gridLabel1.setHorizontalAxisTitleColor(Color.rgb(54,75,91));
        gridLabel1.setVerticalAxisTitleColor(Color.rgb(54,75,91));
        gridLabel1.setLabelVerticalWidth(60);
        gridLabel1.setLabelHorizontalHeight(50);
        gridLabel1.setHumanRounding(false);
        gridLabel1.setVerticalLabelsColor(Color.rgb(85,115,125));
        gridLabel1.setHorizontalLabelsColor(Color.rgb(85,115,125));
        gridLabel1.setLabelsSpace(10);


        dur_graph.getViewport().setXAxisBoundsManual(true);
        dur_graph.getViewport().setMinX(1);
        dur_graph.getViewport().setMaxX(2);

        dur_graph.getViewport().setYAxisBoundsManual(true);
        dur_graph.getViewport().setMinY(0);
        dur_graph.getViewport().setMaxY(2);

        dur_graph.getViewport().setScalable(true);
        dur_graph.getViewport().setScrollable(true);
        dur_graph.getViewport().setScrollableY(true);
        dur_graph.getViewport().setScalableY(true);


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataPoint[] data = new DataPoint[(int) snapshot.getChildrenCount()];
                int index = 0, x = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SleepCycleData scd = dataSnapshot.getValue(SleepCycleData.class);
                    float duration_graph = Float.parseFloat(scd.getTotalDur().replace(":","."));

                    x += 1;
                    data[index] = new DataPoint(x, duration_graph);
                    index++;
                }
                dur_line.resetData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }


}