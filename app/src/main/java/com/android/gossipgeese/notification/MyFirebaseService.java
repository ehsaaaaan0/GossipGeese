//package com.android.gossipgeese.notification;
//
//
//import static com.android.gossipgeese.notification.App.CHANNEL_1_ID;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.app.RemoteInput;
//
//import com.android.gossipgeese.MainActivity;
//import com.android.gossipgeese.R;
//import com.android.gossipgeese.StartMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MyFirebaseService extends FirebaseMessagingService {
//    public static List<MessageModel> MESSAGES = new ArrayList<>();
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        MESSAGES.add(new com.android.gossipgeese.notification.MessageModel(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody()));
//            sendChannel1Notification(this);
//
//    }
//    public static void sendChannel1Notification(Context context){
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
//            Intent activityIntent = new Intent(context, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
//
//
//
//            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
//                    .setLabel("your Answer...").build();
//            Intent replyIntent = new Intent(context, NotificationReceiver.class);
//            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, 0);
//            NotificationCompat.Action replayAction = new NotificationCompat.Action.Builder(
//                    R.drawable.ic_gossipgeese, "Replay", replyPendingIntent
//            ).addRemoteInput(remoteInput).build();
//            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(
//                    "me"
//            );
//            messagingStyle.setConversationTitle("Group Chat");
//
//            for(com.android.gossipgeese.notification.MessageModel chatMessage : MESSAGES){
//                NotificationCompat.MessagingStyle.Message notificationMesage = new NotificationCompat.MessagingStyle.Message(
//                        chatMessage.getText(), chatMessage.getTimeSpam(),chatMessage.getSender()
//                );
//                messagingStyle.addMessage(notificationMesage);
//            }
//
//            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
//                    .setSmallIcon(R.drawable.ic_gossipgeese)
//
//                    .setStyle(messagingStyle)
//                    .addAction(replayAction)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setContentIntent(contentIntent)
//                    .setAutoCancel(true)
//                    .build();
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//            notificationManager.notify(1, notification);
//        }
//    }
//
//
//    private void sendNotification(String title,String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_IMMUTABLE);
//
//        String channelId = "fcm_default_channel";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setContentTitle(title)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//}
