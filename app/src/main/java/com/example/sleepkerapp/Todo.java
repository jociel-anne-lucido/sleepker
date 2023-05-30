package com.example.sleepkerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    EditText todo_txt;
    String todo;
    Integer index;
    String uid, id;
    FirebaseAuth auth, auths;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> arrayList= new ArrayList<>();
    ArrayList<String> keysList= new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    TextView deleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        listView=findViewById(R.id.todotxt);
        bottomsheet = findViewById(R.id.botttom_sheet);
        deleteTextView = findViewById(R.id.delete_button);

        btn_back = findViewById(R.id.back_button);
        auths = FirebaseAuth.getInstance();
        FirebaseUser todos = auths.getCurrentUser();
        id = todos.getUid();
        mRef = FirebaseDatabase.getInstance().getReference("UserData").child(id).child("-TodoData");
        arrayAdapter = new ArrayAdapter<String>(Todo.this, R.layout.list_item, R.id.item_text, arrayList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                int colorRes = position % 2 == 0 ? R.color.background : R.color.line;
                int bgColor = ContextCompat.getColor(getContext(), colorRes);
                view.setBackgroundColor(bgColor);
                ImageView imageView = view.findViewById(R.id.item_image);
                imageView.setImageResource(R.drawable.thoughts);
                deleteTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteAllConfirmationDialog();
                    }
                });
                return view;
            }
        };

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
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0x6E, 0xA5, 0xA6)));
                deleteItem.setIcon(R.drawable.todo_delete);
                deleteItem.setWidth(180);
                menu.addMenuItem(deleteItem);

                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0xD5, 0xA6, 0xBD)));
                editItem.setIcon(R.drawable.todo_edit);
                editItem.setWidth(180);
                menu.addMenuItem(editItem);
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
                        showEditDialog(position);
                        break;

                }
                return false;
            }
        });

    }
    private void showEditDialog(final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_save);

        todo_txt = dialog.findViewById(R.id.newTaskText);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);

        todo_txt.setText(arrayAdapter.getItem(position));

        auth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckTodo()) {
                    return;
                }

                String newTodo = todo_txt.getText().toString();
                String key = keysList.get(position);

                Map<String, Object> update = new HashMap<>();
                update.put(key, newTodo);

                mRef.updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    arrayList.set(position, newTodo);
                                    arrayAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(Todo.this, "Error updating todo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.show();
    }
    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Todo.this, R.style.CustomDialogTheme);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete all your thoughts?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllItems();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    private void deleteAllItems() {
        mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    arrayList.clear();
                    keysList.clear();
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(Todo.this, "All items deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Todo.this, "Error deleting items", Toast.LENGTH_SHORT).show();
                }
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

                if (!CheckTodo()) {
                    return;
                }
                FirebaseUser user = auth.getCurrentUser();
                uid = user.getUid();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UserData").child(uid).child("-TodoData");

                dbRef.push().setValue(todo);

                // stores user attributes to db
                Toast.makeText(Todo.this,"Todo Saved",Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    private boolean CheckTodo() {
        todo = todo_txt.getText().toString();
        if (todo.isEmpty()) {
            todo_txt.setError("Enter todo.");
            todo_txt.requestFocus();
            return false;
        } else {
            return true;
        }
    }

}