package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.android.gossipgeese.adapter.CallLogAdapter;
import com.android.gossipgeese.model.CallLogModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CallLog extends AppCompatActivity {
    ConstraintLayout hide;
    RecyclerView rv;

    ArrayList<CallLogModel>list;
    CallLogAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        hide = findViewById(R.id.hideThis);
        rv = findViewById(R.id.recyclerView_CallLog);
        rv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new CallLogAdapter(list,this);
        FirebaseDatabase.getInstance().getReference().child("Call").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        hide.setVisibility(View.GONE);
                        CallLogModel model = snapshot1.getValue(CallLogModel.class);
                        list.add(model);
                    }

                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}