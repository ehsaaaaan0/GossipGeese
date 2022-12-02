package com.android.gossipgeese;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.gossipgeese.model.UserStories;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ViewImage extends AppCompatActivity {
    AppCompatButton save;
    DownloadManager manager;
    ImageView msgImage,back_tomsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        getSupportActionBar().hide();
        msgImage = findViewById(R.id.msgImage);
        save = findViewById(R.id.save);
        back_tomsg = findViewById(R.id.back_tomsg);
        String image_ = getIntent().getStringExtra("image");
        Picasso.get().load(image_).into(msgImage);
        back_tomsg = findViewById(R.id.back_tomsg);
        back_tomsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    String image = image_+".jpg";
                    Uri uri = Uri.parse(image);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE).setAllowedOverRoaming(true)
                            .setTitle("downloadIMage.jpg").setMimeType("image/jpeg").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+"downloadIMage"+".jpg");
                    manager.enqueue(request);
                    Toast.makeText(ViewImage.this, "saved ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}