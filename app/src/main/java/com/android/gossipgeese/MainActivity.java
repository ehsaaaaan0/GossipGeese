package com.android.gossipgeese;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gossipgeese.adapter.NewMessageAdapter;
import com.android.gossipgeese.adapter.StoryAdapter;
import com.android.gossipgeese.database.DbHealper;
import com.android.gossipgeese.model.NewMessageModel;
import com.android.gossipgeese.model.StoryModel;
import com.android.gossipgeese.model.UserStories;
import com.android.gossipgeese.registerfragments.UsernameandDateofbirth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    AppCompatButton startChat,archive,recent;
    LinearLayout justForNow,upload_story;
    RecyclerView rv,storyRv;
    ConstraintLayout hide;
    BottomNavigationView menu;
    DbHealper db;
    ArrayList<NewMessageModel>list;
    NewMessageAdapter adapter;
    TextView fazoolText;
    ActivityResultLauncher<String> gallary;
    ArrayList<StoryModel> storyList;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startChat = findViewById(R.id.startChat);
        fazoolText = findViewById(R.id.fazoolText);
        justForNow = findViewById(R.id.justForNow);
        upload_story = findViewById(R.id.upload_story);
        menu  =findViewById(R.id.bottomNav);
        archive = findViewById(R.id.archiveBTN);
        recent = findViewById(R.id.recentBTN);
        rv = findViewById(R.id.recent_rv);
        hide = findViewById(R.id.hide_layout);
        storyRv = findViewById(R.id.stories_rv);


        storyList = new ArrayList<>();
        StoryAdapter adapter1 = new StoryAdapter(storyList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setNestedScrollingEnabled(false);


        FirebaseDatabase.getInstance().getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        StoryModel story = new StoryModel();
                        story.setStoryBy(dataSnapshot.getKey());
                        story.setStoryAt(dataSnapshot.child("postedBy").getValue(Long.class));
                        ArrayList<UserStories> stories = new ArrayList<>();

                        for (DataSnapshot snapshot1 : dataSnapshot.child("userstories").getChildren()){
                            UserStories userStories = snapshot1.getValue(UserStories.class);
                            stories.add(userStories);
                        }
                        story.setStories(stories);
                        storyList.add(story);
                    }
                    storyRv.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        db = new DbHealper(this);
        adapter = new NewMessageAdapter(list,this);
        Cursor cursor = db.realAllData();
        while (cursor.moveToNext()){
            if (cursor.getCount()>0){
                NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                if (Objects.equals(model.getRecent(), "true") && Objects.equals(model.getArchive(), "false")) {
                    list.add(model);
                    hide.setVisibility(View.GONE);
                }
            }
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        upload_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                gallary.launch("image/*");
            }
        });



        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                archive.setVisibility(View.GONE);
                recent.setVisibility(View.VISIBLE);
                list.clear();
                fazoolText.setText("To Make Chat Private! All The Archive Chats Appear Here");
                startChat.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);
                Cursor cursor = db.realAllData();
                while (cursor.moveToNext()){
                    if (cursor.getCount()>0){
                        NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                        if (Objects.equals(model.getRecent(), "false") && Objects.equals(model.getArchive(), "true")) {
                            list.add(model);
                            hide.setVisibility(View.GONE);
                        }
                    }
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recent.setVisibility(View.GONE);
                archive.setVisibility(View.VISIBLE);
                hide.setVisibility(View.VISIBLE);
                fazoolText.setText("To Make It Easy! All The Recent Chat Comes Here");
                list.clear();
                Cursor cursor = db.realAllData();
                while (cursor.moveToNext()){
                    if (cursor.getCount()>0){
                        NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                        if (Objects.equals(model.getRecent(), "true") && Objects.equals(model.getArchive(), "false")) {
                            list.add(model);
                            hide.setVisibility(View.GONE);
                        }
                    }
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StartChat.class));
            }
        });
        justForNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,MainLoginScreen.class));
                finish();
            }
        });


        gallary = registerForActivityResult(new ActivityResultContracts.GetContent() , new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {


                StorageReference reference = FirebaseStorage.getInstance().getReference().child("stories").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(new Date().getTime()+"");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StoryModel story = new StoryModel();
                                story.setStoryAt(new Date().getTime());
                                FirebaseDatabase.getInstance().getReference().child("stories").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("postedBy").setValue(story.getStoryAt());
                                UserStories stories = new UserStories(uri.toString(), story.getStoryAt());
                                FirebaseDatabase.getInstance().getReference().child("stories").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("userstories").push().setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
                    }
                });

            }
        });



        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.calls){
                    Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId()==R.id.chats){
                    startActivity(new Intent(MainActivity.this, StartChat.class));
                }
                else if (item.getItemId()==R.id.status){

                    Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("status").setValue("Online");
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("online").setValue(currentTime);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("status").setValue("offline");
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("online").setValue(currentTime);
        }
    }




//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            if (data.getData()!=null){
//                uri = data.getData();
//
//
//
//            }
//        }
//
//    }
}