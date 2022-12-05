package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gossipgeese.model.StoryModel;
import com.android.gossipgeese.model.UserStories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartStory extends AppCompatActivity {
    String id, key,n,i;
    CircleImageView profile;
    TextView name;
    ArrayList<UserStories> stories = new ArrayList<>();
    UserStories userStories;
    ImageView story_image,next,back;
    DownloadManager manager = null;
    AppCompatButton saveImage;
    int newi;
    int l = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_story);
        getSupportActionBar().hide();
        id = getIntent().getStringExtra("id");
        key = getIntent().getStringExtra("key");
        n = getIntent().getStringExtra("name");
        i = getIntent().getStringExtra("image");
        profile = findViewById(R.id.circleImageView);
        name = findViewById(R.id.profileName);
        next = findViewById(R.id.nextStory);
        back = findViewById(R.id.backStory);
        saveImage = findViewById(R.id.saveImage);
        story_image = findViewById(R.id.story_image);
        name.setText(n);
        Picasso.get().load(i).placeholder(R.drawable.ic_gossipgeese).into(profile);
        FirebaseDatabase.getInstance().getReference().child("stories").child(id).child("userstories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        userStories = snapshot1.getValue(UserStories.class);
                        stories.add(userStories);

                        for (int i=0; i<stories.size(); i++) {
                             newi=0;
                            UserStories model = stories.get(newi);
                            Picasso.get().load(model.getImage()).into(story_image);
                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    newi++;
                                    if (newi<=stories.size()-1){
                                        UserStories model = stories.get(newi);
                                        Picasso.get().load(model.getImage()).into(story_image);
                                    }else{
                                        finish();
                                    }
                                }
                            });
                            back.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    newi--;
                                    if (newi>=0){
                                        UserStories model = stories.get(newi);
                                        Picasso.get().load(model.getImage()).into(story_image);
                                    }else{
                                        finish();
                                    }
                                }
                            });

                            saveImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        downloadImage("filename.jpg", model.getImage());
                                    }
                                }
                            });

                        }

//                        int i=1;
//                        while (i==1){
//                            UserStories model = stories.get(i);
//                            Picasso.get().load(model.getImage()).into(story_image);
//                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    void downloadImage(String fileName, String imageUri){
        try{
            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            UserStories model = stories.get(newi);
            String image = imageUri+".jpg";
            Uri uri = Uri.parse(image);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE).setAllowedOverRoaming(true)
                    .setTitle(fileName).setMimeType("image/jpeg").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+fileName+".jpg");
            manager.enqueue(request);
            Toast.makeText(StartStory.this, "saved ", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(this, e.getMessage().toString()+"", Toast.LENGTH_SHORT).show();
        }
    }
}