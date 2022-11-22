package com.android.gossipgeese;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class StartStory extends AppCompatActivity {
    String id, key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_story);
        id = getIntent().getStringExtra("id");
        key = getIntent().getStringExtra("key");

    }
}