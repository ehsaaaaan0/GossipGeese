package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.jitsi.meet.sdk.JitsiMeetActivity;


import com.android.gossipgeese.model.CallLogModel;
import com.android.gossipgeese.model.CurrentUserModel;
import com.android.gossipgeese.model.User;
import com.android.gossipgeese.network.ApiClient;
import com.android.gossipgeese.network.ApiService;
import com.android.gossipgeese.utilities.Constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    private String inviterToken = null;
    private String meetingRoom  = null;
    private String meetingType  = null;

    private TextView textFirstChar, textUsername, textEmail;

    private int rejectionCount = 0;
    CurrentUserModel current;
    private int totalReceivers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra("type");

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_video);
            } else {
                imageMeetingType.setImageResource(R.drawable.ic_audio);
            }
        }

        textFirstChar  = findViewById(R.id.textFirstChar);
        textUsername   = findViewById(R.id.textUserName);
        textEmail      = findViewById(R.id.textEmail);

        User user = (User) getIntent().getSerializableExtra("user");
         current = (CurrentUserModel) getIntent().getSerializableExtra("current");

        if (user != null) {
//            textFirstChar.setText(user.getName().substring(0, 1));
//            textUsername.setText(String.format("%s %s", user.getName()));
//            textEmail.setText(user.);
            textUsername.setText(user.getFirstName());
        }

        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(view -> {
            if (getIntent().getBooleanExtra("isMultiple", false)) {
                Type type = new TypeToken<ArrayList<User>>(){}.getType();
                ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                cancelInvitation( null, receivers);
            } else {
                if (user != null) {
                    cancelInvitation(user.getToken(), null);
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();

                if (meetingType != null) {
                    if (getIntent().getBooleanExtra("isMultiple", false)) {
                        Type type = new TypeToken<ArrayList<User>>(){}.getType();
                        ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                        if (receivers != null) {
                            totalReceivers = receivers.size();
                        }
                        initiateMeeting(meetingType, null, receivers);
                    } else {
                        if (user != null) {
                            totalReceivers = 1;
                            initiateMeeting(meetingType, user.getToken(), null);
                        }
                    }
                }
            }
        });
    }
    private void initiateMeeting(String meetingType, String receiverToken, ArrayList<User> receivers) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }




            if (receivers != null && receivers.size() > 0) {
                StringBuilder userNames = new StringBuilder();
                for (int i=0; i < receivers.size(); i++) {
                    tokens.put(receivers.get(i).getToken());
                    userNames.append(receivers.get(i).getFirstName()).append(" ").append(receivers.get(i).getFirstName()).append("\n");
                }

                textFirstChar.setVisibility(View.GONE);
                textEmail.setVisibility(View.GONE);
                textUsername.setText(userNames.toString());
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_FIRST_NAME, current.getFirstName());
            data.put(Constants.KEY_LAST_NAME, current.getLastName());
            data.put(Constants.KEY_EMAIL, current.getToken());
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom =
                    current.getId() + "_" +
                            UUID.randomUUID().toString().substring(0, 5);
            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                    } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cancelInvitation(String receiverToken, ArrayList<User> receivers) {
        try {

            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            if (receivers != null && receivers.size() > 0) {
                for (User user : receivers) {
                    tokens.put(user.getToken());
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                    String key = FirebaseDatabase.getInstance().getReference().child("test").push().getKey();
                    String id = FirebaseAuth.getInstance().getUid();
                    CallLogModel callLog = new CallLogModel(id,key,"Accepted OutGoing Call");
                    FirebaseDatabase.getInstance().getReference().child("Call").child(id).child(key).setValue(callLog);

                    try {
                        URL serverURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
//                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);

                        if (meetingType.equals("audio")) {
                            builder.setVideoMuted(true);

                        }
                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(OutgoingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    rejectionCount += 1;
                    if (rejectionCount == totalReceivers) {

                        String key = FirebaseDatabase.getInstance().getReference().child("test").push().getKey();
                        String id = FirebaseAuth.getInstance().getUid();
                        CallLogModel callLog = new CallLogModel(id,key,"Rejected OutGoing Call");
                        FirebaseDatabase.getInstance().getReference().child("Call").child(id).child(key).setValue(callLog);
                        Toast.makeText(context, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}