package com.android.gossipgeese;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.provider.FontRequest;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gossipgeese.adapter.ChatAdapter;
import com.android.gossipgeese.model.CallLogModel;
import com.android.gossipgeese.model.CurrentUserModel;
import com.android.gossipgeese.model.MessageModel;
import com.android.gossipgeese.model.NewMessageModel;
import com.android.gossipgeese.model.User;
import com.android.gossipgeese.notification.APIService;
import com.android.gossipgeese.notification.Client;
import com.android.gossipgeese.notification.Data;
import com.android.gossipgeese.notification.MyResponse;
import com.android.gossipgeese.notification.Sender;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class StartMessaging extends AppCompatActivity {

    FirebaseUser fuser;
    String receiverId;
    User user = new User();
    String token;
    String senderId;
    RecyclerView rv;
    CircleImageView userImage;
    ImageView vc,ac;
    String senderRoom, receiverRoom,name,email;
    ImageView send,sendImage,back;
    EditText msg;
    LinearLayout sendM;
    RecordView recordView;
    RecordButton voice;
    ChatAdapter adapter;
    APIService apiService;
    ArrayList<MessageModel>list;
    Uri uri;
    String senderName;
    MediaRecorder mediaRecorder;
    String audioPath;
    LinearLayout video;
    //    public static List<com.android.gossipgeese.notification.MessageModel> MESSAGES = new ArrayList<>();
    TextView userStatus,userName;

    String URL = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;
    ProgressDialog audioDialog;
    CurrentUserModel c = new CurrentUserModel();
    ImageView emojiKeyboard;

    private NotificationManagerCompat notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        setContentView(R.layout.activity_start_messaging);
        video = findViewById(R.id.videoLiner);
        send = findViewById(R.id.sendMessageBTN);
        rv = findViewById(R.id.showMessaged);
        msg = findViewById(R.id.enterMessage);
        recordView = findViewById(R.id.record_view);
        voice = findViewById(R.id.sendVoiceBTN);
        back = findViewById(R.id.back);
        userImage = findViewById(R.id.userImage);
        sendImage = findViewById(R.id.sendImage);
        userStatus = findViewById(R.id.userStatus);
        userName = findViewById(R.id.userName);
        emojiKeyboard = findViewById(R.id.emojiKeyoard);
        sendM = findViewById(R.id.sendMessageLayout);
        vc = findViewById(R.id.vc);/////// Video Call
        ac=findViewById(R.id.ac);/////// Audio Call
        notificationManager = NotificationManagerCompat.from(StartMessaging.this);

        Intent i = getIntent();
        receiverId = i.getStringExtra("receiver");
        String image = i.getStringExtra("image");
        String msgT = i.getStringExtra("msg");
        token = i.getStringExtra("token");
        name = i.getStringExtra("name");
        userName.setText(name);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("users").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String token = snapshot.child("token").getValue(String.class);
                String id = snapshot.child("id").getValue(String.class);

                c.setToken(token);
                c.setEmail(email);
                c.setFirstName(name);
                c.setLastName(name);
                c.setId(id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        user.setEmail(email);
                        user.setFirstName(name);
                        user.setLastName(name);
                        user.setToken(token);
                        Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                        intent.putExtra("user",user );
                        intent.putExtra("current",c);
                        intent.putExtra("type", "audio");
                        startActivity(intent);


            }
        });

        vc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        user.setEmail(email);
                        user.setFirstName(name);
                        user.setLastName(name);
                        user.setToken(token);
                        Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("current",c);
                        intent.putExtra("type", "video");
                        startActivity(intent);
                }
        });
        requestQueue = Volley.newRequestQueue(this);
        if (!TextUtils.isEmpty(msgT)){
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            String key = FirebaseDatabase.getInstance().getReference().child("Key").push().getKey();
            MessageModel model = new MessageModel(senderId,key,"",currentTime,msgT,"null");
            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StartMessaging.this, "Unable to Send Message", Toast.LENGTH_SHORT).show();
                }
            });
        }
        getUserStatus();
        Picasso.get().load(image).into(userImage);
        senderId = FirebaseAuth.getInstance().getUid();
        senderRoom = senderId+receiverId;
        receiverRoom = receiverId+senderId;
        list = new ArrayList<>();
        adapter = new ChatAdapter(list,this,senderRoom,receiverRoom);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.smoothScrollToPosition(adapter.getItemCount());
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
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

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(StartMessaging.this)
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msg.getText().toString();
                msg.setText("");
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String key = FirebaseDatabase.getInstance().getReference().child("Key").push().getKey();
                MessageModel model = new MessageModel(senderId,key,currentTime,message,"null");
                FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                sendNotification(name,message,token);
                                sendNotification(receiverId, c.getFirstName(),message,token);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StartMessaging.this, "Unable to Send Message", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        emojiKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(msg, InputMethodManager.SHOW_IMPLICIT);
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

        audioDialog = new ProgressDialog(StartMessaging.this);
        audioDialog.setMessage("Sending Voice Message");
        voice.setRecordView(recordView);
        voice.setListenForRecord(false);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()==true){
                    voice.setListenForRecord(true);
                }else{
                    ActivityCompat.requestPermissions(StartMessaging.this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

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
                    Toast.makeText(StartMessaging.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }




    private boolean checkPermission(){
        int first = ActivityCompat.checkSelfPermission(StartMessaging.this, Manifest.permission.RECORD_AUDIO);
        int scond  = ActivityCompat.checkSelfPermission(StartMessaging.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        audioDialog.dismiss();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                audioDialog.dismiss();
                                Toast.makeText(StartMessaging.this, "Unable to Send Message", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                audioDialog.dismiss();
                Toast.makeText(StartMessaging.this, "Error While Sending Audio", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void getUserStatus() {
        FirebaseDatabase.getInstance().getReference().child("users").child(receiverId).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatus.setText(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data.getData()!=null){
                uri = data.getData();
                startActivity(new Intent(StartMessaging.this, ViewStories.class)
                        .putExtra("image", uri)
                        .putExtra("receiverRoom",receiverRoom)
                        .putExtra("senderRoom",senderRoom)
                        .putExtra("senderId",senderId)
                        .putExtra("group","individual"));

            }
        }

    }


//    private void sendMessagenotification(String message) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("to","/topics/"+receiverId);
//            JSONObject jsonObject1 = new JSONObject();
//            jsonObject1.put("title", "Message from "+senderName);
//            jsonObject1.put("body",message);
//            JSONObject jsonObject2 = new JSONObject();
//            jsonObject2.put("userId",FirebaseAuth.getInstance().getUid());
//            jsonObject2.put("recId",receiverId);
//            jsonObject2.put("type","sms");
//            jsonObject.put("notification",jsonObject1);
//            jsonObject.put("data",jsonObject2);
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, jsonObject, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                }
//            }){
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String,String>map = new HashMap<>();
//                    map.put("content-type","application/json");
//                    map.put("authorization","key=AAAAOup2zPI:APA91bGH1SJluZyFSvuMKU1d1qZCQf-Kw03GMoUMnJsf08D79QhA9Qbe13TwPJKSXbPdhXjPVBCaYnHUFlP-J8_FFCfWl13tokOh-9aqZXsTnsA-lIQznmzfRVe5Ki40LYYbNMjLzr9E");
//                    return map;
//                }
//            };
//            requestQueue.add(request);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    void sendNotification(String name, String message,String token){
//        try {
//            RequestQueue queue = Volley.newRequestQueue(this);
//
//            String url = "https://fcm.googleapis.com/fcm/send";
//
//            JSONObject data = new JSONObject();
//            data.put("title", name);
//            data.put("body", message);
//            JSONObject notificationData = new JSONObject();
//            notificationData.put("notification", data);
//            notificationData.put("to","/topics/"+token);
//
//            JsonObjectRequest request = new JsonObjectRequest(url, notificationData
//                    , new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    // Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(StartMessaging.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> map = new HashMap<>();
//                    String key = "Key=AAAAOup2zPI:APA91bGH1SJluZyFSvuMKU1d1qZCQf-Kw03GMoUMnJsf08D79QhA9Qbe13TwPJKSXbPdhXjPVBCaYnHUFlP-J8_FFCfWl13tokOh-9aqZXsTnsA-lIQznmzfRVe5Ki40LYYbNMjLzr9E";
//                    map.put("Content-Type", "application/json");
//                    map.put("Authorization", key);
//
//                    return map;
//                }
//            };
//
//            queue.add(request);
//
//
//        } catch (Exception ex) {
//
//        }
//
//
//    }
private void sendNotification(String receiver, String username, String message, String token) {


                Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                        receiver);

                Sender sender = new Sender(data, token);

                apiService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                if (response.code() == 200){
                                    if (response.body().success != 1){
                                        Toast.makeText(StartMessaging.this, "Notification Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });



}
}