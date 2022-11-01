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
import android.widget.TextView;

import com.android.gossipgeese.adapter.ForwardMsgAdapter;
import com.android.gossipgeese.adapter.NewMessageAdapter;
import com.android.gossipgeese.database.DbHealper;
import com.android.gossipgeese.model.NewMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ForwardMsg extends AppCompatActivity {
    TextView msg;
    RecyclerView rv;
    EditText search;
    DbHealper db;
    ArrayList<NewMessageModel> list;
    ForwardMsgAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_msg);
        msg = findViewById(R.id.msgHere);
        rv = findViewById(R.id.forwardRV);
        search = findViewById(R.id.search_user);
        Intent i = getIntent();
        String getMsg = i.getStringExtra("msg");
        msg.setText(getMsg);
        db = new DbHealper(this);
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForwardMsgAdapter(list,this,getMsg);
        Cursor cursor = db.realAllData();
        while (cursor.moveToNext()){
            if (cursor.getCount()>0){
                NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                list.add(model);
            }
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
        }
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
}