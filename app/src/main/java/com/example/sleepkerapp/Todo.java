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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Todo extends AppCompatActivity {
    FloatingActionButton bottomsheet;
    SwipeMenuListView listView;
    DatabaseReference mRef;
    ImageView btn_back;
    EditText todo_txt, edit_txt;
    String todo;
    Integer index;
    String uid, id;
    FirebaseAuth auth, auths;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> arrayList= new ArrayList<>();
    ArrayList<String> keysList= new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        listView=findViewById(R.id.todotxt);
        bottomsheet = findViewById(R.id.botttom_sheet);
        btn_back = findViewById(R.id.back_button);
        auths = FirebaseAuth.getInstance();
        FirebaseUser todos = auths.getCurrentUser();
        id = todos.getUid();
        mRef = FirebaseDatabase.getInstance().getReference("UserData").child(id).child("-TodoData");
        arrayAdapter = new ArrayAdapter<String>(Todo.this, R.layout.list_item, arrayList);
        listView.setAdapter(arrayAdapter);
        Query query = mRef.orderByKey();

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String todos = snapshot.getValue(String.class);
                arrayList.add(todos);
                keysList.add(snapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                String key = snapshot.getKey();
                index = keysList.indexOf(key);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String todos = snapshot.getValue(String.class);
                arrayList.remove(todos);
                keysList.remove(snapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }
        });

        btn_back.setOnClickListener(v -> startActivity(new Intent(Todo.this, Clock.class)));
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0x6E, 0xA5, 0xA6)));
                openItem.setIcon(R.drawable.todo_edit);
                openItem.setWidth(180);
                menu.addMenuItem(openItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF6, 0x6C, 0x79)));
                deleteItem.setIcon(R.drawable.todo_delete);
                deleteItem.setWidth(180);
                menu.addMenuItem(deleteItem);
            }
        };



        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        String item = arrayAdapter.getItem(position);
                        arrayAdapter.remove(item);
                        arrayAdapter.notifyDataSetChanged();
                        String key = keysList.get(position);
                        mRef.child(key).removeValue();
                        break;
                    case 1:
                        showDialog1();
                        edit_txt.setText((String) listView.getAdapter().getItem(position));
                        arrayAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

    }


    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_save);

        todo_txt = dialog.findViewById(R.id.newTaskText);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);

        auth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                FirebaseUser user = auth.getCurrentUser();
                uid = user.getUid();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-TodoData");

                todo = todo_txt.getText().toString();
                dbRef.push().setValue(todo);

                // stores user attributes to db
                dialog.dismiss();
                Toast.makeText(Todo.this,"Todo Saved",Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    private void showDialog1() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_edit);

        edit_txt = dialog.findViewById(R.id.editText);
        LinearLayout editLayout = findViewById(R.id.layoutEdit);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}