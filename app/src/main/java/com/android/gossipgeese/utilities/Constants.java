package com.android.gossipgeese.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";


    public static final String REMOTE_MSG_AUTHORIZATION = "authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "content-type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";
    public static final String API_KEY_SERVER = "AAAAOup2zPI:APA91bGH1SJluZyFSvuMKU1d1qZCQf-Kw03GMoUMnJsf08D79QhA9Qbe13TwPJKSXbPdhXjPVBCaYnHUFlP-J8_FFCfWl13tokOh-9aqZXsTnsA-lIQznmzfRVe5Ki40LYYbNMjLzr9E";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();

        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key="+ API_KEY_SERVER
        );

        return headers;
    }
}
