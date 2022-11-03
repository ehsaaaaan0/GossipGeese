package com.android.gossipgeese;

import static com.android.gossipgeese.notification.App.CHANNEL_1_ID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gossipgeese.adapter.ChatAdapter;
import com.android.gossipgeese.model.MessageModel;
import com.android.gossipgeese.notification.NotificationReceiver;
import com.android.gossipgeese.registerfragments.UsernameandDateofbirth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartMessaging extends AppCompatActivity {


    String receiverId;
    String senderId;
    RecyclerView rv;
    CircleImageView userImage;
    String senderRoom, receiverRoom;
    ImageView send,sendImage;
    EditText msg;
    ChatAdapter adapter;
    ArrayList<MessageModel>list;
    Uri uri;
    public static List<com.android.gossipgeese.notification.MessageModel> MESSAGES = new ArrayList<>();
    TextView userStatus;
    private NotificationManagerCompat notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_messaging);
        send = findViewById(R.id.sendMessage);
        rv = findViewById(R.id.showMessaged);
        msg = findViewById(R.id.enterMessage);
        userImage = findViewById(R.id.userImage);
        sendImage = findViewById(R.id.sendImage);
        userStatus = findViewById(R.id.userStatus);
        notificationManager = NotificationManagerCompat.from(StartMessaging.this);
        MESSAGES.add(new com.android.gossipgeese.notification.MessageModel("Good Morning","JIM"));
        senToChaanel1();
        Intent i = getIntent();
        receiverId = i.getStringExtra("receiver");
        String image = i.getStringExtra("image");
        String msgT = i.getStringExtra("msg");

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
                        //Dexter.withActivity(MainActivity.this)
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

    }

    private void senToChaanel1() {
        sendChannel1Notification(StartMessaging.this);
    }

    public static void sendChannel1Notification(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);



            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                    .setLabel("your Answer...").build();
            Intent replyIntent = new Intent(context, NotificationReceiver.class);
            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, 0);
            NotificationCompat.Action replayAction = new NotificationCompat.Action.Builder(
                    R.drawable.ic_gossipgeese, "Replay", replyPendingIntent
            ).addRemoteInput(remoteInput).build();
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(
                    "me"
            );
            messagingStyle.setConversationTitle("Group Chat");

            for(com.android.gossipgeese.notification.MessageModel chatMessage : MESSAGES){
                NotificationCompat.MessagingStyle.Message notificationMesage = new NotificationCompat.MessagingStyle.Message(
                        chatMessage.getText(), chatMessage.getTimeSpam(),chatMessage.getSender()
                );
                messagingStyle.addMessage(notificationMesage);
            }

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_gossipgeese)

                    .setStyle(messagingStyle)
                    .addAction(replayAction)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, notification);
        }
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
                        .putExtra("senderId",senderId));

            }
        }

    }
}