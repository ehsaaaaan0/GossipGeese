package com.android.gossipgeese;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {
    AppCompatButton save;
    DownloadManager manager;
    ImageView msgImage,back_tomsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        msgImage = findViewById(R.id.msgImage);
        save = findViewById(R.id.save);
        back_tomsg = findViewById(R.id.back_tomsg);
        String image = getIntent().getStringExtra("image");
        Picasso.get().load(image).into(msgImage);
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
                }
                Uri uri = Uri.parse(image);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                long reference = manager.enqueue(request);
                Toast.makeText(ViewImage.this, "saved ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}