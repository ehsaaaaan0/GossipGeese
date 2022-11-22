package com.android.gossipgeese.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.gossipgeese.IncomingInvitationActivity;
import com.android.gossipgeese.R;
import com.android.gossipgeese.StartMessaging;
import com.android.gossipgeese.utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    public static List<MessageModel> list = new ArrayList<>();
//    static String title;
//    static String body ;
//    String sendId, recId;
//    static Intent intent;
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//         title = remoteMessage.getNotification().getTitle();
//        body = remoteMessage.getNotification().getBody();
//        //////////
//        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
//
//        if (type != null) {
//            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
//                Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);
//                intent.putExtra(
//                        Constants.REMOTE_MSG_MEETING_TYPE,
//                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
//                );
//                intent.putExtra(
//                        Constants.KEY_FIRST_NAME,
//                        remoteMessage.getData().get(Constants.KEY_FIRST_NAME)
//                );
//                intent.putExtra(
//                        Constants.KEY_LAST_NAME,
//                        remoteMessage.getData().get(Constants.KEY_LAST_NAME)
//                );
//                intent.putExtra(
//                        Constants.KEY_EMAIL,
//                        remoteMessage.getData().get(Constants.KEY_EMAIL)
//                );
//                intent.putExtra(
//                        Constants.REMOTE_MSG_INVITER_TOKEN,
//                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
//                );
//                intent.putExtra(
//                        Constants.REMOTE_MSG_MEETING_ROOM,
//                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM)
//                );
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
//                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
//                intent.putExtra(
//                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
//                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
//                );
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//            }
//        }
////////////////////////
//         intent = null;
//        if (remoteMessage.getData().get("type").equalsIgnoreCase("sms")){
//            intent = new Intent(this, StartMessaging.class);
//            intent.putExtra("receiver", remoteMessage.getData().get("userId"));
//        }
//            sendId = remoteMessage.getData().get("userId");
//            recId = remoteMessage.getData().get("recId");
//
//        list.add(new com.android.gossipgeese.notification.MessageModel(title, body,recId,sendId));
//
//        sendNotification(this);
//
//    }
//
//    public static void sendNotification(Context context) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
//            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply").setLabel("Answer...")
//                    .build();
//            Intent replyIntent = new Intent(context, NotificationReceiver.class);
//            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, 0);
//            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 101, replyIntent, 0);
//            NotificationCompat.Action replayAction = new NotificationCompat.Action.Builder(
//                    R.drawable.ic_gossipgeese, "Replay", replyPendingIntent
//            ).addRemoteInput(remoteInput).build();
//
//            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(
//                    "me"
//            );
//            messagingStyle.setConversationTitle("Group Chat");
//            for(com.android.gossipgeese.notification.MessageModel chatMessage : list){
//                NotificationCompat.MessagingStyle.Message notificationMesage = new NotificationCompat.MessagingStyle.Message(
//                        chatMessage.getText(), chatMessage.getTimeSpam(),chatMessage.getSender()
//                );
//                messagingStyle.addMessage(notificationMesage);
//            }
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//            Notification notification = new NotificationCompat.Builder(context, "CHAT")
//                    .setSmallIcon(R.drawable.ic_gossipgeese)
//
//                    .setStyle(messagingStyle)
//                    .addAction(replayAction)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true)
//                    .build();
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//            notificationManager.notify(1, notification);
//
//        }
//
//    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);

        if (type != null) {
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        Constants.KEY_FIRST_NAME,
                        remoteMessage.getData().get(Constants.KEY_FIRST_NAME)
                );
                intent.putExtra(
                        Constants.KEY_LAST_NAME,
                        remoteMessage.getData().get(Constants.KEY_LAST_NAME)
                );
                intent.putExtra(
                        Constants.KEY_EMAIL,
                        remoteMessage.getData().get(Constants.KEY_EMAIL)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_ROOM,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent.getActivity(getApplicationContext(), 0, intent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                startActivity(intent);
            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }
}
