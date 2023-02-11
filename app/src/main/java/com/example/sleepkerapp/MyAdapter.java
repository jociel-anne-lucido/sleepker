package com.example.sleepkerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<SleepCycleData> list;

    public MyAdapter(Context context, ArrayList<SleepCycleData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_sleep_record,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.sleepDateTxt.setText(list.get(position).getDateRecorded());
        holder.sleepTimeTxt.setText(list.get(position).getSleepTime());
        holder.sleepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Date " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SleepData.class);
                intent.putExtra("dateRecorded", list.get(position).getDateRecorded());
                intent.putExtra("lastRecorded", list.get(position).getLastRecorded());
                intent.putExtra("wakeTime", list.get(position).getWakeTime());
                intent.putExtra("newRecorded", list.get(position).getNewRecorded());
                intent.putExtra("totalDur", list.get(position).getTotalDur());
                intent.putExtra("sleepTime", list.get(position).getSleepTime());
                intent.putExtra("moodQual", list.get(position).getMoodQual());
                intent.putExtra("sleepQual", list.get(position).getSleepQual());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchDataList(ArrayList<SleepCycleData> searchList){
        list = searchList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sleepTimeTxt, sleepDateTxt;
        CardView sleepCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sleepDateTxt = itemView.findViewById(R.id.sleep_date);
            sleepCardView = itemView.findViewById(R.id.sleep_card);
        }
    }

}