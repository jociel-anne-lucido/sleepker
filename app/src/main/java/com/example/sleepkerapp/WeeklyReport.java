package com.example.sleepkerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeeklyReport extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<SleepCycleData> list;
    MyAdapter myAdapter;
    DatabaseReference databaseRef;
    FirebaseUser user;
    FirebaseAuth auth;
    String uid;
    ImageView btn_back;
    EditText minimal_input;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_report);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        btn_back = findViewById(R.id.back_button1);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);
        FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-SleepData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    SleepCycleData sleepCycleData = dataSnapshot.getValue(SleepCycleData.class);
                    list.add(sleepCycleData);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        btn_back.setOnClickListener(v -> startActivity(new Intent(WeeklyReport.this, Analysis.class)));

    }
    public void searchList(String text){
        ArrayList<SleepCycleData> searchList = new ArrayList<>();
        for(SleepCycleData sleepCycleData: list){
            if(sleepCycleData.getDateRecorded().toLowerCase().contains(text.toLowerCase())){
                searchList.add(sleepCycleData);
            }
        }
        myAdapter.searchDataList(searchList);
    }
}
