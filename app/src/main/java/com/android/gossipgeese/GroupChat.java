package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gossipgeese.adapter.GroupChatAdapter;
import com.android.gossipgeese.model.MessageModel;
import com.android.volley.RequestQueue;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity {

    String senderId;
    RecyclerView rv;
    CircleImageView userImage;
    ImageView vc,ac;
    String senderRoom;
    ImageView send,sendImage;
    EditText msg;
    LinearLayout sendM;
    RecordView recordView;
    RecordButton voice;
    GroupChatAdapter adapter;
    ArrayList<MessageModel>list;
    Uri uri;
    MediaRecorder mediaRecorder;
    String audioPath;
    LinearLayout video;
    TextView userStatus,userName;
    ProgressDialog audioDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getSupportActionBar().hide();
        video = findViewById(R.id.videoLiner);
        senderId = FirebaseAuth.getInstance().getUid();
        send = findViewById(R.id.sendMessageBTN);
        rv = findViewById(R.id.showMessaged);
        msg = findViewById(R.id.enterMessage);
        recordView = findViewById(R.id.record_view);
        voice = findViewById(R.id.sendVoiceBTN);
        userImage = findViewById(R.id.userImage);
        sendImage = findViewById(R.id.sendImage);
        userStatus = findViewById(R.id.userStatus);
        userName = findViewById(R.id.userName);
        sendM = findViewById(R.id.sendMessageLayout);
        vc = findViewById(R.id.vc);/////// Video Call
        ac=findViewById(R.id.ac);/////// Audio Call
        list = new ArrayList<>();
        adapter = new GroupChatAdapter(list, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference().child("public").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    rv.smoothScrollToPosition(adapter.getItemCount());
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        MessageModel model = snapshot1.getValue(MessageModel.class);
                        list.add(model);
                    }
                    rv.smoothScrollToPosition(adapter.getItemCount());
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msg.getText().toString();
                msg.setText("");
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String key = FirebaseDatabase.getInstance().getReference().child("Key").push().getKey();
                MessageModel model = new MessageModel(senderId,key,currentTime,message,"null");
                FirebaseDatabase.getInstance().getReference().child("public").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                                adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupChat.this, "Unable to Send Message", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    send.setVisibility(View.GONE);
                    voice.setVisibility(View.VISIBLE);
                }else{
                    send.setVisibility(View.VISIBLE);
                    voice.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(GroupChat.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);

                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        audioDialog = new ProgressDialog(GroupChat.this);
        audioDialog.setMessage("Sending Voice Message");
        voice.setRecordView(recordView);
        voice.setListenForRecord(false);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GroupChat.this, "Clicked", Toast.LENGTH_SHORT).show();
                if (checkPermission()==true){
                    voice.setListenForRecord(true);

                }else{
                    ActivityCompat.requestPermissions(GroupChat.this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }


            }
        });
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                audioPath = getExternalCacheDir().getAbsolutePath()+"/"+"recordingaudio" + System.currentTimeMillis() +".mp3";
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setOutputFile(audioPath);
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(GroupChat.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }



                sendM.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()){
                    file.delete();
                }
                sendM.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.

                Log.d("RecordView", "onFinish");

                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendM.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
                sendRecordingMessage(audioPath);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()){
                    file.delete();
                }
                sendM.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
            }
        });

    }

    private boolean checkPermission(){
        int first = ActivityCompat.checkSelfPermission(GroupChat.this, Manifest.permission.RECORD_AUDIO);
        int scond  = ActivityCompat.checkSelfPermission(GroupChat.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return first == PackageManager.PERMISSION_GRANTED && scond ==PackageManager.PERMISSION_GRANTED;
    }

    private void sendRecordingMessage(String audioPath){
        audioDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("voices").child(senderRoom+System.currentTimeMillis()+"");
        Uri audioFile = Uri.fromFile(new File(audioPath));
        storageReference.putFile(audioFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        String key = FirebaseDatabase.getInstance().getReference().child("push").push().getKey();
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        MessageModel model = new MessageModel(senderId,key,url,currentTime,msg.getText().toString(),"null","voice");
                        FirebaseDatabase.getInstance().getReference().child("public").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                        audioDialog.dismiss();
                                        adapter.notifyDataSetChanged();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                audioDialog.dismiss();
                                Toast.makeText(GroupChat.this, "Unable to Send Message", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                audioDialog.dismiss();
                Toast.makeText(GroupChat.this, "Error While Sending Audio", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data.getData()!=null){
                uri = data.getData();
                startActivity(new Intent(GroupChat.this, ViewStories.class)
                        .putExtra("image", uri)
                        .putExtra("receiverRoom","null")
                        .putExtra("senderRoom","null")
                        .putExtra("senderId","null")
                        .putExtra("group","group"));

            }
        }

    }
}