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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Statistic extends AppCompatActivity {

    private static final int VIEW_MODE_WEEKLY = 0;
    private static final int VIEW_MODE_MONTHLY = 1;
    private int viewMode = VIEW_MODE_WEEKLY;
    private TextView weekText, monthText, aveTotalDur, aveSleepQual, lowestSleepQual, highestSleepQual, highestSleepDur, lowestSleepDur, lowestDurDate, highestDurDate;
    String uid;
    GraphView qual_graph, dur_graph;

    private BarChart mChart;
    private static final String TAG = "Statistic";

    LineGraphSeries qual_line, dur_line;
    CircularProgressBar lowestProgBar, highestProgBar, averageProgBar;
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
        weekText = findViewById(R.id.weekly_button);
        monthText= findViewById(R.id.monthly_button);

        final int selectedColor = getResources().getColor(R.color.heading);
        final int defaultColor = getResources().getColor(R.color.whitesmoke);

        final TextView initiallyUnderlined = weekText;
        initiallyUnderlined.setTextColor(selectedColor);
        initiallyUnderlined.setLinkTextColor(selectedColor);

        final TextView[] currentlyUnderlined = { initiallyUnderlined };
        weekText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentlyUnderlined[0] != weekText) {
                    currentlyUnderlined[0].setTextColor(defaultColor);
                    currentlyUnderlined[0].setLinkTextColor(defaultColor);
                    currentlyUnderlined[0] = weekText;
                    currentlyUnderlined[0].setTextColor(selectedColor);
                    currentlyUnderlined[0].setLinkTextColor(selectedColor);
                }
                GetTotalDurA();
                DisplayGraphSQA();
                DisplayGraphDurationA();
                AverageSleepQualA();
                HighestDurA();
                LowestDurA();
                LowestSleepQualA();
                HighestSleepQualA();
                MoodA();
            }
        });
        monthText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentlyUnderlined[0] != monthText) {
                    currentlyUnderlined[0].setTextColor(defaultColor);
                    currentlyUnderlined[0].setLinkTextColor(defaultColor);
                    currentlyUnderlined[0] = monthText;
                    currentlyUnderlined[0].setTextColor(selectedColor);
                    currentlyUnderlined[0].setLinkTextColor(selectedColor);
                }
                GetTotalDur();
                DisplayGraphSQ();
                DisplayGraphDuration();
                AverageSleepQual();
                HighestDur();
                LowestDur();
                LowestSleepQual();
                HighestSleepQual();
                Mood();
            }
        });

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

        mChart = findViewById(R.id.bar_chart);
        mChart.getDescription().setEnabled(false);
        mChart.setFitBars(true);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(Color.parseColor("#364B5B"));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });

        leftAxis.setGranularity(1f);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#364B5B"));
        xAxis.setGranularity(1f);
        Legend legend = mChart.getLegend();
        mChart.setExtraOffsets(0f, 15f, 0f, 10f); // Set extra top and bottom offsets to 10 pixels
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align the legend to the right side of the chart
        legend.setTextColor(Color.parseColor("#364B5B"));


        GetTotalDurA();
        DisplayGraphSQA();
        DisplayGraphDurationA();
        AverageSleepQualA();
        HighestDurA();
        LowestDurA();
        LowestSleepQualA();
        HighestSleepQualA();
        MoodA();

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
    private void Mood(){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> moods = new ArrayList<>();
                moods.add("Good");
                moods.add("Relaxing");
                moods.add("So-so");
                moods.add("Bad");
                moods.add("Worst");
                List<BarEntry> entries = new ArrayList<>();
                for (String mood : moods) {
                    entries.add(new BarEntry(moods.indexOf(mood), countMood(mood, snapshot)));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Mood");
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        // Convert the float value to an integer and return as a string
                        return String.valueOf((int) value);
                    }
                });
                int[] colors = new int[]{Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.RED};
                for (int i = 0; i < colors.length; i++) {
                    if (colors[i] == Color.BLUE) {
                        colors[i] = Color.parseColor("#d2c3e0");
                    }
                    if (colors[i] == Color.GREEN) {
                        colors[i] = Color.parseColor("#c3e0d2");
                    }
                    if (colors[i] == Color.YELLOW) {
                        colors[i] = Color.parseColor("#fff7e0");
                    }
                    if (colors[i] == Color.MAGENTA) {
                        colors[i] = Color.parseColor("#fadab9");
                    }
                    if (colors[i] == Color.RED) {
                        colors[i] = Color.parseColor("#fbc4c3");
                    }
                }
                dataSet.setColors(colors);

                List<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);
                BarData data = new BarData(dataSets);
                mChart.setData(data);
                XAxis xAxis = mChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(moods.toArray(new String[0])));
                mChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error getting data", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error getting data.", error.toException());
            }
        });
    }
    private void MoodA(){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> moods = new ArrayList<>();
                moods.add("Good");
                moods.add("Relaxing");
                moods.add("So-so");
                moods.add("Bad");
                moods.add("Worst");
                List<BarEntry> entries = new ArrayList<>();
                for (String mood : moods) {
                    entries.add(new BarEntry(moods.indexOf(mood), countMoodA(mood, snapshot)));
                }
                BarDataSet dataSet = new BarDataSet(entries, "Mood");
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        // Convert the float value to an integer and return as a string
                        return String.valueOf((int) value);
                    }
                });
                int[] colors = new int[]{Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.RED};
                for (int i = 0; i < colors.length; i++) {
                    if (colors[i] == Color.BLUE) {
                        colors[i] = Color.parseColor("#d2c3e0");
                    }
                    if (colors[i] == Color.GREEN) {
                        colors[i] = Color.parseColor("#c3e0d2");
                    }
                    if (colors[i] == Color.YELLOW) {
                        colors[i] = Color.parseColor("#fff7e0");
                    }
                    if (colors[i] == Color.MAGENTA) {
                        colors[i] = Color.parseColor("#fadab9");
                    }
                    if (colors[i] == Color.RED) {
                        colors[i] = Color.parseColor("#fbc4c3");
                    }
                }
                dataSet.setColors(colors);
                List<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);
                BarData data = new BarData(dataSets);
                mChart.setData(data);
                XAxis xAxis = mChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(moods.toArray(new String[0])));
                mChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error getting data", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error getting data.", error.toException());
            }
        });
    }

    private int countMood(String mood, DataSnapshot snapshot) {
        int count = 0;
        for (DataSnapshot data : snapshot.getChildren()) {
            if (data.child("moodQual").getValue(String.class).equals(mood)) {
                count++;
            }
        }
        return count;
    }
    private int countMoodA(String mood, DataSnapshot snapshot) {
        int count = 0;
        int maxEntries = 7;
        int currentEntry = 0;
        for (DataSnapshot data : snapshot.getChildren()) {
            if (currentEntry >= maxEntries) {
                break;
            }
            if (data.child("moodQual").getValue(String.class).equals(mood)) {
                count++;
            }
            currentEntry++;
        }
        return count;
    }
    private void GetTotalDur(){

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalMins = 0;
                int numNodes = 0;

                // Iterate over each child node and add up the "totalDur" field values
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String totalDur = childSnapshot.child("totalDur").getValue(String.class);
                    String[] timeComponents = totalDur.split(":");
                    int hours = Integer.parseInt(timeComponents[0]);
                    int mins = Integer.parseInt(timeComponents[1]);
                    totalMins += (hours * 60) + mins;
                    numNodes++;
                }

                // Calculate the average and format it as "hr:min"
                int avgMins = totalMins / numNodes;
                int avgHours = avgMins / 60;
                int avgMinsRemainder = avgMins % 60;
                String avgTotalDur = String.format("%d:%02d", avgHours, avgMinsRemainder);

                // Do something with the average total duration string, e.g. display it in a TextView
                aveTotalDur.setText(avgTotalDur);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval process
            }
        });
    }
    private void GetTotalDurA(){

        Query query = dbRef.orderByKey().limitToLast(7);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalMins = 0;
                int numNodes = 0;

                // Iterate over each of the last 7 child nodes and add up the "totalDur" field values
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String totalDur = childSnapshot.child("totalDur").getValue(String.class);
                    String[] timeComponents = totalDur.split(":");
                    int hours = Integer.parseInt(timeComponents[0]);
                    int mins = Integer.parseInt(timeComponents[1]);
                    totalMins += (hours * 60) + mins;
                    numNodes++;
                }

                // Calculate the average and format it as "hr:min"
                int avgMins = totalMins / numNodes;
                int avgHours = avgMins / 60;
                int avgMinsRemainder = avgMins % 60;
                String avgTotalDur = String.format("%d:%02d", avgHours, avgMinsRemainder);

                // Do something with the average total duration string, e.g. display it in a TextView
                aveTotalDur.setText(avgTotalDur);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval process
            }
        });
    }
    private void AverageSleepQual(){
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
    }
    private void AverageSleepQualA(){
        dbRef.limitToLast(7).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0.0, average;
                int count = (int) snapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object price = map.get("sleepQual");
                    int p = Integer.parseInt(String.valueOf(price));
                    total += p;
                }

                average = (total / count);
                DecimalFormat df = new DecimalFormat("#.#");
                aveSleepQual.setText(df.format(average)+ "%");
                averageProgBar.setProgress(Float.parseFloat((String.valueOf(average))));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void HighestDur(){
        dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-SleepData");
        Query highestSleepdur = dbRef.orderByChild("totalDur").limitToLast(1);
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
    }
    private void HighestDurA(){
        Query query = dbRef.orderByChild("totalDur").limitToLast(7);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String highestDur = "";
                String highestDate = "";
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    if (scd != null) {
                        String dur = scd.getTotalDur();
                        String date = scd.getWakeTime();
                        if (highestDur.isEmpty() || dur.compareTo(highestDur) > 0) {
                            highestDur = dur;
                            highestDate = date;
                        }
                    }
                }
                highestSleepDur.setText(highestDur);
                highestDurDate.setText(highestDate);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });
    }
    private void LowestDur(){
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
    }
    private void LowestDurA(){
        Query latestSleepData = dbRef.orderByChild("totalDur").limitToLast(7);

        latestSleepData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float minSleepQual = Float.MAX_VALUE;
                SleepCycleData minSleepCycleData = null;

                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    float sleepQual = Float.parseFloat(scd.getSleepQual());
                    if (sleepQual < minSleepQual) {
                        minSleepQual = sleepQual;
                        minSleepCycleData = scd;
                    }
                }

                if (minSleepCycleData != null) {
                    String lowestDur = String.valueOf(minSleepCycleData.getTotalDur());
                    String lowestDate = minSleepCycleData.getWakeTime();
                    lowestSleepDur.setText(lowestDur);
                    lowestDurDate.setText(lowestDate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });
    }
    private void LowestSleepQual(){
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
    }
    private void LowestSleepQualA(){
        Query sleepDataQuery = dbRef.limitToLast(7).orderByChild("sleepQual");
        sleepDataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int lowestSleepQualA = 100; // set initial value to maximum
                SleepCycleData lowestSleepCycleData = null;
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    int sleepQual = Integer.parseInt(scd.getSleepQual());
                    if (sleepQual < lowestSleepQualA) {
                        lowestSleepQualA = sleepQual;
                        lowestSleepCycleData = scd;
                    }
                }
                if (lowestSleepCycleData != null) {
                    String lowestSleepQualStr = lowestSleepCycleData.getSleepQual() + "%";
                    lowestSleepQual.setText(lowestSleepQualStr);
                    float lowestSleepQualFloat = Float.parseFloat(lowestSleepCycleData.getSleepQual());
                    lowestProgBar.setProgress(lowestSleepQualFloat);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });
    }
    private void HighestSleepQual(){
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
    }
    private void HighestSleepQualA(){
        Query query = dbRef.orderByChild("sleepQual").limitToLast(7);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int highestSleepQualA = 0;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    SleepCycleData scd = childSnapshot.getValue(SleepCycleData.class);
                    int sleepQual = Integer.parseInt(scd.getSleepQual());
                    if (sleepQual > highestSleepQualA) {
                        highestSleepQualA = sleepQual;
                    }
                }
                highestSleepQual.setText(highestSleepQualA + "%");
                highestProgBar.setProgress(highestSleepQualA);
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
        gridLabel.setLabelVerticalWidth(80);
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

        gridLabel.setLabelFormatter(new DefaultLabelFormatter() {
            private String lastLabel = "";

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int intValue = (int) value;
                    String label = String.valueOf(intValue);
                    if (label.equals(lastLabel)) {
                        return ""; // skip label if it's the same as the previous one
                    } else {
                        lastLabel = label;
                        return label;
                    }
                } else {
                    return super.formatLabel(value, isValueX); // use the default format for y-axis labels
                }
            }
        });


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
    private void DisplayGraphSQA() {
        GridLabelRenderer gridLabel = qual_graph.getGridLabelRenderer();
        gridLabel.setVerticalAxisTitle("Percentage %");
        gridLabel.setHorizontalAxisTitle("Input");
        gridLabel.setHorizontalAxisTitleColor(Color.rgb(54,75,91));
        gridLabel.setVerticalAxisTitleColor(Color.rgb(54,75,91));
        gridLabel.setLabelVerticalWidth(80);
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


        gridLabel.setLabelFormatter(new DefaultLabelFormatter() {
            private String lastLabel = "";

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int intValue = (int) value;
                    String label = String.valueOf(intValue);
                    if (label.equals(lastLabel)) {
                        return ""; // skip label if it's the same as the previous one
                    } else {
                        lastLabel = label;
                        return label;
                    }
                } else {
                    return super.formatLabel(value, isValueX); // use the default format for y-axis labels
                }
            }
        });

        dbRef.orderByKey().limitToLast(7).addValueEventListener(new ValueEventListener() {
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
        gridLabel1.setLabelVerticalWidth(80);
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

        gridLabel1.setLabelFormatter(new DefaultLabelFormatter() {
            private String lastLabel = "";

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int intValue = (int) value;
                    String label = String.valueOf(intValue);
                    if (label.equals(lastLabel)) {
                        return ""; // skip label if it's the same as the previous one
                    } else {
                        lastLabel = label;
                        return label;
                    }
                } else {
                    return super.formatLabel(value, isValueX); // use the default format for y-axis labels
                }
            }
        });


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
    private void DisplayGraphDurationA() {
        GridLabelRenderer gridLabel1 = dur_graph.getGridLabelRenderer();
        gridLabel1.setPadding(30);
        gridLabel1.setVerticalAxisTitle("Time (h.m)");
        gridLabel1.setHorizontalAxisTitle("Input");
        gridLabel1.setHorizontalAxisTitleColor(Color.rgb(54, 75, 91));
        gridLabel1.setVerticalAxisTitleColor(Color.rgb(54, 75, 91));
        gridLabel1.setLabelVerticalWidth(80);
        gridLabel1.setLabelHorizontalHeight(50);
        gridLabel1.setHumanRounding(false);
        gridLabel1.setVerticalLabelsColor(Color.rgb(85, 115, 125));
        gridLabel1.setHorizontalLabelsColor(Color.rgb(85, 115, 125));
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


        gridLabel1.setLabelFormatter(new DefaultLabelFormatter() {
            private String lastLabel = "";

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int intValue = (int) value;
                    String label = String.valueOf(intValue);
                    if (label.equals(lastLabel)) {
                        return ""; // skip label if it's the same as the previous one
                    } else {
                        lastLabel = label;
                        return label;
                    }
                } else {
                    return super.formatLabel(value, isValueX); // use the default format for y-axis labels
                }
            }
        });


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                int dataSize = (int) snapshot.getChildrenCount();
                int startIndex = Math.max(dataSize - 7, 0);
                DataPoint[] data = new DataPoint[Math.min(7, dataSize)]; // Create a new array with a fixed size of 7 or the number of elements if less than 7

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (count >= startIndex) { // Only retrieve DataSnapshots from the last 7 records
                        SleepCycleData scd = dataSnapshot.getValue(SleepCycleData.class);
                        float duration_graph = Float.parseFloat(scd.getTotalDur().replace(":", "."));
                        int xValue = count - startIndex + 1; // Use the count variable as the x-value

                        data[xValue - 1] = new DataPoint(xValue, duration_graph); // Add data point to array
                    }

                    if (count >= dataSize - 1) { // Exit loop if all records have been processed
                        break;
                    }

                    count++;
                }

                dur_line.resetData(data); // Update the chart data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}