package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.gossipgeese.model.MessageModel;
import com.android.gossipgeese.model.UserStories;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ViewStories extends AppCompatActivity {

    ImageView img,sendMessage;
    String receiverRoom, senderRoom,senderId;
    EditText msg;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stories);
        sendMessage = findViewById(R.id.sendMessage);
        msg = findViewById(R.id.enterMessage);
        img = findViewById(R.id.story_image);
        dialog = new ProgressDialog(ViewStories.this);
        dialog.setMessage("Sending Image");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Intent i = getIntent();
        Uri uri = i.getParcelableExtra("image");
        img.setImageURI(uri);
        receiverRoom = i.getStringExtra("receiverRoom");
        senderRoom = i.getStringExtra("senderRoom");
        senderId = i.getStringExtra("senderId");

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final StorageReference reference = FirebaseStorage.getInstance().getReference().child("chat").child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String message = msg.getText().toString();
                                String image = uri.toString();
                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                String key = FirebaseDatabase.getInstance().getReference().child("Key").push().getKey();
                                MessageModel model = new MessageModel(senderId,key,image,currentTime,message,"null");
                                FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                onBackPressed();
                                                finish();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(ViewStories.this, "Unable to Send Message", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewStories.this, "Error Sending Image", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });



    }
}