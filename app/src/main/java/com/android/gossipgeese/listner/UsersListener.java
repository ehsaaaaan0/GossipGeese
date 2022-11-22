package com.android.gossipgeese.listner;

import com.android.gossipgeese.model.NewMessageModel;

public interface UsersListener {

    void initiateVideoMeeting(NewMessageModel user);

    void initiateAudioMeeting(NewMessageModel user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
