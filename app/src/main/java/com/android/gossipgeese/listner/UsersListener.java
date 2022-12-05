package com.android.gossipgeese.listner;

import com.android.gossipgeese.model.NewMessageModel;
import com.android.gossipgeese.model.User;

public interface UsersListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
