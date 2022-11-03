package com.android.gossipgeese.notification;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.gossipgeese.MainActivity;
import com.android.gossipgeese.StartMessaging;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remopteInput = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            remopteInput = RemoteInput.getResultsFromIntent(intent);
        }
        if (remopteInput!=null){
            CharSequence replyText = remopteInput.getCharSequence("key_text_reply");
            MessageModel answer = new MessageModel(replyText,null);
            StartMessaging.MESSAGES.add(answer);
            StartMessaging.sendChannel1Notification(context);
        }
    }
}
