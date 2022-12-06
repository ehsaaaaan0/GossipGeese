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
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.bouncycastle.jcajce.provider.digest.MD2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    AppCompatButton startChat,archive,recent;
    LinearLayout upload_story;
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
        upload_story = findViewById(R.id.upload_story);
        menu  =findViewById(R.id.bottomNav);
        archive = findViewById(R.id.archiveBTN);
        recent = findViewById(R.id.recentBTN);
        rv = findViewById(R.id.recent_rv);
        hide = findViewById(R.id.hide_layout);
        storyRv = findViewById(R.id.stories_rv);
//        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
//            @Override
//            public void onSuccess(String s) {
//                FirebaseMessaging.getInstance().subscribeToTopic(s);
//                HashMap<String,Object> map = new HashMap<>();
//                map.put("token",s);
//                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
//                                .updateChildren(map);
//
//            }
//        });
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getUid());
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
                        story.setStoryKey(dataSnapshot.child("postedBy").child("storyKey").getValue(String.class));
                        story.setStoryAt(dataSnapshot.child("postedBy").child("storyAt").getValue(Long.class));
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
                NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
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
                        NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
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
                        NewMessageModel model = new NewMessageModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
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
                                String key = FirebaseDatabase.getInstance().getReference().child("key").push().getKey();
                                story.setStoryAt(new Date().getTime());
                                story.setStoryKey(key);
                                FirebaseDatabase.getInstance().getReference().child("stories").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("postedBy").setValue(story);
                                UserStories stories = new UserStories(uri.toString(), story.getStoryAt());
                                FirebaseDatabase.getInstance().getReference().child("stories").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("userstories").child(key).setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    startActivity(new Intent(MainActivity.this,GroupChat.class));
                }
                else if (item.getItemId()==R.id.chats){
                    startActivity(new Intent(MainActivity.this, StartChat.class));
                }
                else if (item.getItemId()==R.id.status){
                    startActivity(new Intent(MainActivity.this, MyProfile.class));
                }else if (item.getItemId()==R.id.callLog){
                    startActivity(new Intent(MainActivity.this,CallLog.class));
                }

                return true;
            }
        });




//        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()){
//                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                            String id = snapshot1.child("id").getValue(String.class);
//                            String name = snapshot1.child("name").getValue(String.class);
//                            String image = snapshot1.child("image").getValue(String.class);
//                            String time = snapshot1.child("time").getValue(String.class);
//                            String token = snapshot1.child("token").getValue(String.class);
//                            if (!Objects.equals(id, FirebaseAuth.getInstance().getUid())) {
//                                Cursor cursor = db.realAllData();
//                                while (cursor.moveToNext()){
//                                    if (cursor.getCount()>0){
//                                        if (!Objects.equals(cursor.getString(0), id)){
//                                            db.insert(id,name,image,"",time,"false","false",token);
//                                        }
//                                    }else{
//                                        db.insert(id,name,image,"",time,"false","false",token);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }else{
//            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//        }



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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,MainLoginScreen.class));
                finish();
                break;
            default:
                Toast.makeText(this, "Not CLicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}