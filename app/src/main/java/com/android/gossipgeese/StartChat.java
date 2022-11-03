package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.gossipgeese.adapter.NewMessageAdapter;
import com.android.gossipgeese.database.DbHealper;
import com.android.gossipgeese.model.NewMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class StartChat extends AppCompatActivity {
    RecyclerView rv;
    NewMessageAdapter adapter;
    DbHealper db;
    ArrayList<NewMessageModel>list;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_chat);
        uploadToLocalDatabase();
        rv = findViewById(R.id.users_rv);
        search = findViewById(R.id.search_main);
        db = new DbHealper(StartChat.this);

        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewMessageAdapter(list,this);

        Cursor cursor = db.realAllData();
        while (cursor.moveToNext()){
            if (cursor.getCount()>0){
                NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                    list.add(model);

                }
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
            }
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String id = snapshot1.child("id").getValue(String.class);
                        String name = snapshot1.child("name").getValue(String.class);
                        String image = snapshot1.child("image").getValue(String.class);
                        String time = snapshot1.child("time").getValue(String.class);
                        if (!Objects.equals(id, FirebaseAuth.getInstance().getUid())) {
                            db.insert(id,name,image,"",time,"false","false");
                            Cursor cursor = db.realAllData();
                            while (cursor.moveToNext()){
                                if (cursor.getCount()>0){
                                    if (!Objects.equals(cursor.getString(0), id)){
                                        db.insert(id,name,image,"",time,"false","false");
                                    }
                                }else{
                                    db.insert(id,name,image,"",time,"false","false");
                                }
                            }
                          adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String getText = search.getText().toString();
                searchh(getText);
                if (!getText.isEmpty()){
                    rv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {


                    searchh(editable.toString());
//                    seachA(editable.toString());
                } else {
                    searchh("");
                }
            }
        });

    }

    private void searchh(String s) {


//        Query query = ref.orderByChild("username").startAt(s).endAt(s +"\uf8ff");
        list.clear();
        Cursor cursor = db.realAllData();
        while (cursor.moveToNext()){
            if (cursor.getCount()>0){
                String n = cursor.getString(1);
                if (n.startsWith(s)){
                    NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                    list.add(model);
                }
            }
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
        }

    }

    private void uploadToLocalDatabase() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String id = snapshot1.child("id").getValue(String.class);
                        String name = snapshot1.child("name").getValue(String.class);
                        String image = snapshot1.child("image").getValue(String.class);
                        String time = snapshot1.child("time").getValue(String.class);
                        if (!Objects.equals(id, FirebaseAuth.getInstance().getUid())) {
                            db.insert(id,name,image,"",time,"false","false");

//                            NewMessageModel model = new NewMessageModel(id, name, image, " ", time, "false", "false");
//                            list.add(model);
                            Cursor cursor = db.realAllData();
                            while (cursor.moveToNext()){
                                if (cursor.getCount()>0){
                                    if (!Objects.equals(cursor.getString(0), id)){
                                        db.insert(id,name,image,"",time,"false","false");
                                    }
                                }else{
                                    db.insert(id,name,image,"",time,"false","false");
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String id = snapshot1.child("id").getValue(String.class);
                        String name = snapshot1.child("name").getValue(String.class);
                        String image = snapshot1.child("image").getValue(String.class);
                        String time = snapshot1.child("time").getValue(String.class);
                        if (!Objects.equals(id, FirebaseAuth.getInstance().getUid())) {
//                            NewMessageModel model = new NewMessageModel(id, name, image, " ", time, "false", "false");
//                            list.add(model);
                            db.insert(id,name,image,"",time,"false","false");

                            Cursor cursor = db.realAllData();
                            while (cursor.moveToNext()){
                                if (cursor.getCount()>0){
                                    if (!Objects.equals(cursor.getString(0), id)){
                                        db.insert(id,name,image,"",time,"false","false");
                                    }
                                }else{
                                    db.insert(id,name,image,"",time,"false","false");
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(StartChat.this,MainActivity.class));
    }
}