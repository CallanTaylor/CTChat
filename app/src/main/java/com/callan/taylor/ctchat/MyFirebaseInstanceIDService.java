package com.callan.taylor.ctchat;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String notificationToken;

    public MyFirebaseInstanceIDService() {
        this.notificationToken = "";
    }
    @Override
    public void onTokenRefresh() {
        
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        this.notificationToken = refreshedToken;
    }

    public String getNotificationToken() {
        if (!notificationToken.equals("")) {
            return notificationToken;
        }
        else {
            return "TokenError";
        }
    }
}
